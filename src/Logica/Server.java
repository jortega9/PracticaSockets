package Logica;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GUI.ErrorWindow;
import Mensajes.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Server {
	
	private ServerSocket serverSocket; // Socket del servidor
	private SemaforoTablaID semUsuariosIds; // Lock para acceder a la lista de usuarios activos (usuariosActivosIds) 
								  // Se necesita bloquear ya que es una variable compartida por varios hilos (OyenteCliente)
	private MonitorUsuariosOyentes monitorUsuariosOyentes; // Monitor para acceder a la lista de oyentes de los usuarios activos 
	
	public void start(int port) {
		monitorUsuariosOyentes = new MonitorUsuariosOyentes(); // Inicializar el monitor
		semUsuariosIds = new SemaforoTablaID(); // Inicializar el lock

        try {
			serverSocket = new ServerSocket(port); // Crear el socket del servidor
			
			while (true) {
				Socket clienteSocket = serverSocket.accept(); // Esperar a que se conecte un cliente
	        	new OyenteCliente(clienteSocket ).start();   // Crear un nuevo oyente para el cliente en otro hilo
	        }
		
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() throws IOException {
    	serverSocket.close();
	}
	
	private class OyenteCliente extends Thread{
		private Socket clienteSocket; // Socket del cliente
        private ObjectInputStream in; // Flujo de entrada
        private ObjectOutputStream out; // Flujo de salida
        private boolean conectado; // Variable para saber si el cliente esta conectado
        private Usuario id; // Usuario del cliente

		public OyenteCliente(Socket accept) throws IOException {
			this.clienteSocket = accept;
			out = new ObjectOutputStream(accept.getOutputStream());
			in = new ObjectInputStream(accept.getInputStream());
			this.conectado = true;
		}

		private void nuevoUsuario(Usuario u) throws InterruptedException, IOException {
			// Añadir el usuario a la lista de usuarios activos como son variables compartidas por varios hilos se usa un semaforo
			semUsuariosIds.agregarUsuario(u);
			monitorUsuariosOyentes.addUsuario(u,this); // Añadir el usuario a la lista de usuarios activos

			out.writeObject(new MensajeConfirmConnect(u.getName(), true));
            out.flush();

			System.out.println("Nuevo usuario conectado: " + u.getName());
		}

		@Override
		public void run() {
			Mensaje input;

			// Escuchar lo que recibe del cliente
			while(true) {
				try {
					input = (Mensaje) in.readObject();

					if(input.getTipo().equals(TipoMensaje.M_CONEXION)) {
						// Conexion de un nuevo usuario
						MensajeConexion msj = (MensajeConexion) input;
						this.id = msj.getUsuario();

						// Comprobar si el usuario ya existe
						if(semUsuariosIds.existeUsuario(id)) {
							out.writeObject(new MensajeConfirmConnect(id.getName(), false)); // Usuario ya existe
							out.flush();
							continue;
						}

						// Añadir el usuario a la lista de usuarios activos
						nuevoUsuario(id);
						continue;
					}
					if(input.getTipo().equals(TipoMensaje.M_LISTA)){
						// Peticion de lista de usuarios
						ArrayList<String> lista = monitorUsuariosOyentes.getListaUsuarios(input.getOrigen()); // Obtener la lista de usuarios activos

						out.writeObject(new MensajeConfLU(lista));
						out.flush();
						continue;
					}
					if(input.getTipo().equals(TipoMensaje.M_VER_ARCHIVOS)){
						// Peticion de lista de archivos

						ArrayList<String> lista = monitorUsuariosOyentes.getListaArchivos(input.getOrigen());

						out.writeObject(new M_verArchivosConf(lista));
						out.flush();
						continue;
					}
					if(input.getTipo().equals(TipoMensaje.M_PED_ARCHIVO)){
						//Petición de archivo
						MensajePeticionFichero msj = (MensajePeticionFichero) input;

						Usuario cliente1 = monitorUsuariosOyentes.buscarClienteFichero(msj); // Buscar un cliente que tenga el archivo
						
						if(cliente1!=null){
							String nombreArchivo = Constantes.PATH + cliente1.getName() + "/" + msj.getNombreFichero();
							//Enviar mensaje al cliente que tiene el archivo : Emisor		   Receptor 		 Fichero
							M_emitirArchivo mensaje = new M_emitirArchivo(cliente1.getName(), msj.getOrigen() , nombreArchivo);
							monitorUsuariosOyentes.enviarMensaje(cliente1, mensaje);
						}
						else{
							ErrorWindow.mostrar("El archivo no existe o no se encuentra disponible");
						}
						continue;
					}
					if(input.getTipo().equals(TipoMensaje.M_PREPARADO_C)){
						// El cliente esta preparado para enviar el archivo
						M_preparadoCliente msj = (M_preparadoCliente) input;

						// Sacamos el usuario que quiere el archivo mediante su nombre
						Usuario u = semUsuariosIds.getUsuario(input.getDestino());
						
						String rutaModificada = msj.getNombreArchivo().replace(msj.getOrigen(), msj.getDestino());
						M_preparadoServidor mensaje = new M_preparadoServidor(msj.getOrigen(), msj.getIp(), msj.getPuertoReceptor(), rutaModificada);

						// Mandar M_preparadoServidor con la info para la conexion al cliente que quiere el archivo
						monitorUsuariosOyentes.enviarMensaje(u, mensaje);

						continue;
					}
					if(input.getTipo().equals(TipoMensaje.M_CONF_RECEPCION)){
						// El cliente ha recibido el archivo

						//Sacamos el usuario que ha recibido el archivo mediante su nombre
						Usuario u = semUsuariosIds.getUsuario(input.getOrigen());
						monitorUsuariosOyentes.actualizarUsuario(u, this); // Añadir el archivo a la lista de archivos del usuario

						continue;
					}
					if(input.getTipo().equals(TipoMensaje.M_LOGOFF)) {
						// Desconectar cliente
						desconectarCliente(); 
						System.out.println("Usuario desconectado: " + id.getName());
						return;
					}
									
				} catch (IOException | ClassNotFoundException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void desconectarCliente() throws InterruptedException{
			try {
				this.conectado = false;

				// Eliminar el usuario de la lista de usuarios activos 
				semUsuariosIds.eliminarUsuario(this.id);
				monitorUsuariosOyentes.remove(this.id); 

				// Cerrar los flujos y el socket
				this.in.close();
				this.out.close();
				this.clienteSocket.close();
			} catch (IOException e) {
				ErrorWindow.mostrar("Error al desconectar el cliente\n" + e.getMessage());
			}
		}
	}
	
	private class MonitorUsuariosOyentes implements Controlador{
		//Implementación de un monitor basandose en el problema Lectores/Escritores
		//del libro Foundations of Multithreaded, Parallel and Distributed Programming

		private HashMap<Usuario, OyenteCliente> usuariosActivos = new HashMap<Usuario, OyenteCliente>(); // Lista de usuarios activos
		private Lock lockUsuarios; // Lock para controlar las variables de condición
		private volatile int leyendo, escribiendo; // Contadores de lectores y escritores
		private volatile Condition escribir, leer; // Variables de condición

		//Se debe cumplir: (leyendo == 0 || escribiendo == 0) && escribiendo <= 1

		public MonitorUsuariosOyentes(){
			leyendo = 0;
			escribiendo = 0;
			lockUsuarios = new ReentrantLock(true);
			escribir = lockUsuarios.newCondition();
			leer = lockUsuarios.newCondition();
		}

		// Métodos para solicitar y liberar el permiso de lectura y escritura y luego se tendrá que llamar a la operación correspondiente, 
		// ya que hay varias operaciones posibles sobre la tabla de usuarios activos
		@Override
		public void requestEscribir(){
			lockUsuarios.lock();
			while(leyendo > 0 || escribiendo > 0){ // si hay alguien leyendo o escribiendo, esperar
				try {
					escribir.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			escribiendo++;
			lockUsuarios.unlock();
		}

		@Override
		public void releaseEscribir(){
			lockUsuarios.lock();
			escribiendo--;
			escribir.signal();	// se despierta un hilo que este esperando para escribir
			leer.signalAll(); // se despiertan todos los hilos que esten esperando para leer
			lockUsuarios.unlock();
		}
	
		@Override
		public void requestLeer(){
			lockUsuarios.lock();
			while(escribiendo > 0){ // si hay alguien escribiendo, esperar
				try {
					leer.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			leyendo++;
			lockUsuarios.unlock();
		}
		
		@Override
		public void releaseLeer(){
			lockUsuarios.lock();
			leyendo--;
			if(leyendo == 0) 
			escribir.signal(); // si no hay nadie leyendo, 
							// se despierta un hilo que este esperando para escribir
			lockUsuarios.unlock();
		}

		public void addUsuario(Usuario usuario, OyenteCliente oyente) {
			requestEscribir();
			usuariosActivos.put(usuario, oyente);
			releaseEscribir();
		}

		public void remove(Usuario usuario) {
			requestEscribir();
			usuariosActivos.remove(usuario);
			releaseEscribir();
		}

		public void actualizarUsuario(Usuario us, OyenteCliente oyente) {
			// Actualizar la lista de archivos del usuario
			requestEscribir();
			this.remove(us);
			us.actualizarListaFicheros();
			this.addUsuario(us, oyente);
			releaseEscribir();
		}
		
		public ArrayList<String> getListaUsuarios(String origen) {
			// Devuelve la lista de usuarios activos
			ArrayList<String> lista = new ArrayList<String>();
			requestLeer();
			for(Usuario key : usuariosActivos.keySet()) {
				if(!key.getName().equals(origen) && usuariosActivos.get(key).conectado==true) { 
					lista.add(key.getName());
				}
			}
			releaseLeer();
			return lista;
		}

		public ArrayList<String> getListaArchivos(String origen) {
			// Devuelve la lista de archivos de los usuarios activos
			ArrayList <String> lista = new ArrayList<String>();
			requestLeer();
			for(Usuario key : usuariosActivos.keySet()) {
				if(!key.getName().equals(origen) && usuariosActivos.get(key).conectado==true){ 
					lista.addAll(key.getInfo());
				}
			}
			releaseLeer();
			return lista;
		}

		public Usuario buscarClienteFichero(MensajePeticionFichero msj) throws IOException {
			// Buscar un cliente que tenga el archivo
			String nombreFichero = msj.getNombreFichero();
			
			requestLeer();
			for(Usuario key : usuariosActivos.keySet()) {
				if(usuariosActivos.get(key).conectado==true){ 
					for(String s : key.getInfo()){
						if(s.equals(nombreFichero)){
							return key;
						}
					}
				}
			}
			releaseLeer();
			return null;
		}

		public void enviarMensaje(Usuario u, Mensaje mensaje) throws IOException{
			requestLeer();
			OyenteCliente oyente = usuariosActivos.get(u);
			oyente.out.writeObject(mensaje);
			oyente.out.flush();
			releaseLeer();
		}
	}

	private class SemaforoTablaID implements Controlador {

		//Basado en la implementación del libro Foundations of Multithreaded, Parallel, and Distributed Programming
		// Del tema 4 de semáforos
	
		private HashMap<String, Usuario> usuariosActivosIds = new HashMap<String, Usuario>(); // Lista de usuarios activos en el sistema
		private Semaphore acceso, //acceso a la seccion critica
						  escritura, //se usa para retrasar los escritores
						  lectura; //se usa para retrasar los lectores
		private volatile int leyendo, escribiendo; // Contadores de lectores, escritores
		private volatile int retLectores, retEscritores; //Numero de lectores y escritores que esperan
	
	
		public SemaforoTablaID(){
			acceso = new Semaphore(1);
			escritura = new Semaphore(0);
			lectura = new Semaphore(0);
			leyendo = 0;
			escribiendo = 0;
			retLectores = 0;
			retEscritores = 0;
		}
	
		//Se debe cumplir: (leyendo == 0 || escribiendo == 0) && escribiendo <= 1
		@Override
		public void requestEscribir() {
			while(leyendo > 0 || escribiendo > 0);
			try{
				acceso.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(leyendo > 0 || escribiendo > 0){
				retEscritores ++;
				acceso.release();
				try{
					escritura.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			escribiendo++;
			acceso.release();
		}
	
		@Override
		public void releaseEscribir() {
			try {
				acceso.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			escribiendo --;
			if(retEscritores > 0){
				retEscritores--;
				escritura.release();
			}
			else if(retLectores > 0){
				retLectores --;
				lectura.release();
			}
			else
				acceso.release();
		}
	
		@Override
		public void requestLeer() {
			while(escribiendo > 0);
			try{
				acceso.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(escribiendo > 0 || retEscritores > 0){
				retLectores++;
				acceso.release();
				try{
					lectura.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			leyendo++;
			if(retLectores > 0){
				retLectores--;
				lectura.release();
			}
			else
				acceso.release();
		}
	
		@Override
		public void releaseLeer() {
			try {
				acceso.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			leyendo --;
			if(leyendo == 0 && retEscritores > 0){
				retEscritores --;
				escritura.release();
			}
			else
				acceso.release();
		}
		
		public void agregarUsuario(Usuario usuario) {
			requestEscribir();
			usuariosActivosIds.put(usuario.getName(), usuario);
			releaseEscribir();
		}
	
		public void eliminarUsuario(Usuario usuario) {
			requestEscribir();
			usuariosActivosIds.remove(usuario.getName());
			releaseEscribir();
		}
	
		public Boolean existeUsuario(Usuario usuario) {
			requestLeer();
			Boolean existe = usuariosActivosIds.containsKey(usuario.getName());
			releaseLeer();
			return existe;
		}
	
		public Usuario getUsuario(String nombre) {
			requestLeer();
			Usuario usuario = usuariosActivosIds.get(nombre);
			releaseLeer();
			return usuario;
		}
		
	}
	
	public static void main(String[] args) {
		Server server = new Server();
        server.start(Constantes.PUERTO);
	}
	
}
