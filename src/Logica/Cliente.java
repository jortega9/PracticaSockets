package Logica;

import java.awt.Window;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import GUI.*;
import Mensajes.*;

import java.io.ObjectOutputStream;

public class Cliente {

    private Socket clienteSocket; // Socket del cliente que usara para la conexion con el server
    private int puerto; // Puerto del servidor que se le pasa por parametro
    private int id; //id del proceso cliente que accederá al lock, en este caso el hilo principal del cliente
                    //por otro lado tendremos el id del oyenteServidor para este cliente y por tanto son dos hilos como máximo 
                    //los que acceden al lock

    private ObjectOutputStream out; // Stream de salida
    private ObjectInputStream in; // Stream de entrada
    private Usuario usuario; // Usuario del cliente
    private Thread escuchar; // Hilo que escucha al servidor
    private Boolean conectado; // Controlamos el acceso a la variable conectado desde el hilo principal y el hilo de escucha por tanto usamos un lock
    private LockEmpate lockConectado; // Semaforo para controlar el acceso a la variable conectado
    
    private MainWindow mainWindow; // Ventana principal

    public void startConnection(int port, Window window) throws Exception {
        this.mainWindow = (MainWindow) window;
        this.puerto = port;
        this.id = 0;

        // Inicializamos el usuario
        createUser();

        // Inicializamos el socket
        clienteSocket = new Socket(this.usuario.getIp(), port);

        // Inicializamos los streams
        out = new ObjectOutputStream(clienteSocket.getOutputStream());
        in = new ObjectInputStream(clienteSocket.getInputStream());

        // escribir mensaje conexion
        out.writeObject(new MensajeConexion(this.usuario));
        out.flush();
        conectado = true;
        lockConectado = new LockEmpate();

        // Crear hilo para escuchar al servidor
        OyenteServidor oyente = new OyenteServidor(clienteSocket, this, in, out, lockConectado);
        escuchar = new Thread(oyente);

        // Iniciamos el hilo de escucha 
        escuchar.start();
    }

    private void createUser() {
        // Lee el nombre del cliente desde la ventana
        String nombre = mainWindow.getNombreCliente();

        // Crea el usuario
        try {
            this.usuario = new Usuario(nombre, InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void mostrarVentanaMenu() {
        mainWindow.muestraMenuPanel();
    }

    public void stopConnection() throws InterruptedException {
        try {
            // Ponemos la variable conectado a false para que el hilo de escucha termine
            lockConectado.lock(this.id);; // Bloqueamos el acceso a la variable conectado para que el hilo de escucha no pueda acceder a ella mientras la modificamos
            conectado = false;
            lockConectado.unlock(this.id);

            // Esperamos a que el hilo de escucha termine 
            escuchar.join(); 

            // Cerramos los streams y el socket
            clienteSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Error al desconectar el cliente " + usuario.getName() + ": " + e.getMessage());
        }
    }

    public void elegirOpcion(Mensaje mensaje) {
        // Recibe el mensaje de la ventana de menu y lo envia al servidor
        try {
            out.writeObject(mensaje);
            out.flush();
            if(mensaje.getTipo().equals(TipoMensaje.M_LOGOFF)){ //Desconectamos al cliente
                stopConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void descargarFichero(String u) {
        // Recibe el nombre del fichero a descargar y lo envia al servidor
        try {
            out.writeObject(new MensajePeticionFichero(this.getName(), u));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void verListaUsuarios(ArrayList<String> lista) {
        mainWindow.muestraListaPanel(lista);
    }

    public void verListaArchivos(ArrayList<String> lista) {
        mainWindow.muestraFicheroPanel(lista);
    }

    public String getName() {
        return this.usuario.getName();
    }

    public void actualizarListaFicheros(){
        this.usuario.actualizarListaFicheros();
        System.out.print(this.usuario.getInfo());
    }

    public InetAddress getIp() {
        return this.usuario.getIp();
    }

    public int getPort() {
        return this.puerto;
    }

    public boolean estaConectado() {
        return conectado;
    }

    private static class OyenteServidor implements Runnable {

        private final Socket clienteSocket; // Socket del cliente que usara para la conexion con el server que se le pasa como parametro
        private ObjectInputStream in; // Stream de entrada
        private ObjectOutputStream out; // Stream de salida
        private Cliente cliente; // Cliente que se le pasa como parametro
        private LockEmpate lockConectado; // Semaforo para controlar el acceso a la variable conectado
        private int id; // id del proceso oyente cliente que accederá al lock, en este caso el hilo de escucha del cliente

        public OyenteServidor(Socket socket, Cliente cliente, ObjectInputStream in, ObjectOutputStream out, LockEmpate lockConectado) {
            this.clienteSocket = socket;
            this.cliente = cliente;
            this.in = in;
            this.out = out;
            this.lockConectado = lockConectado;
            this.id = 1;
        }

        @Override
        public void run() {
            //comprobamos si el cliente esta conectado y leemos los mensajes que nos llegan
            lockConectado.lock(this.id); // Bloqueamos el acceso a la variable conectado para que el hilo principal no pueda modificarla
                                                   // mientras la leemos
            while (cliente.estaConectado()) {
                try {
                    lockConectado.unlock(this.id);

                    Mensaje mensaje = (Mensaje) in.readObject();

                    // Comprobamos el tipo de mensaje que nos llega
                    // Mensaje de confirmacion de conexion al servidor
                    if (mensaje.getTipo().equals(TipoMensaje.CONFIRM_CONNECT)) {
                        MensajeConfirmConnect mensajeConfirm = (MensajeConfirmConnect) mensaje;
                        if (mensajeConfirm.getExito()){
                            cliente.mostrarVentanaMenu();
                            System.out.println("Conectado al servidor");
                        } else {
                            ErrorWindow.mostrar("Nombre de usuario en uso");
                        }
                        continue;
                    }
                    // Mensaje de confirmacion de llegada de lista de usuarios
                    if (mensaje.getTipo().equals(TipoMensaje.LISTA_USUARIOS_CONFIRM)) {
                        MensajeConfLU mensajeConfLU = (MensajeConfLU) mensaje;
                        ArrayList<String> lista = new ArrayList<>(mensajeConfLU.getUsuarios());
                        cliente.verListaUsuarios(lista);
                        System.out.println("Lista de usuarios recibida"); 
                        continue;
                    }
                    // Mensaje de confirmacion de llegada de lista de archivos
                    if (mensaje.getTipo().equals(TipoMensaje.M_VER_ARCHIVOS_CONF)) {
                        M_verArchivosConf mensajeArcConf = (M_verArchivosConf) mensaje;
                        ArrayList<String> lista = new ArrayList<>(mensajeArcConf.getArchivos());
                        cliente.verListaArchivos(lista);
                        System.out.println("Lista de archivos recibida"); 
                        continue;
                    }
                    // Mensaje de peticion de enviar un archivo a otro cliente
                    if (mensaje.getTipo().equals(TipoMensaje.M_EMITIR_ARCHIVO)) {
                        // Peticion de emision de archivo
                        M_emitirArchivo mensajeEmitir = (M_emitirArchivo) mensaje;
                        System.out.println("Mensaje para emitir"); 

                        emisorArchivo(mensajeEmitir);

                        continue;
                    }
                    // Mensaje de confirmacion cliente1 listo para enviar archivo
                    if (mensaje.getTipo().equals(TipoMensaje.M_PREPARADO_S)) {
                        System.out.println("Cliente preparado para emitir archivo"); 
                        M_preparadoServidor mensajePreparado = (M_preparadoServidor) mensaje;
                        receptorArchivo(mensajePreparado);
                        cliente.actualizarListaFicheros();
                        ExitoWindow.mostrarExito("Archivo descargado");
                        
                        // Enviamos confirmacion de recepcion para que el servidor pueda actualizar su lista de archivos
                        out.writeObject(new M_conf_recepcion(cliente.getName()));
                        out.flush();
                        continue;
                    }

                    // Cogemos el lock para poder acceder a la variable conectado
                    lockConectado.lock(this.id);

                } catch (IOException e) {
                    // Si se lanza esta excepción es porque se ha desconetacto el socket
                    // Comprobamos si se debe a un error o si es porque el cliente se ha desconectado
                    lockConectado.lock(this.id);
                    Boolean conectado = cliente.estaConectado();
                    lockConectado.unlock(this.id);

                    if(!conectado) break; // Si el cliente no sigue conectado, salimos del bucle y no ha ocurrido ningun error

                    // Si el cliente sigue conectado y el socket se ha cerrado es porque ha habido un error
                    if (clienteSocket.isClosed() || !clienteSocket.isConnected()) {
                        ErrorWindow.mostrar("Socket cerrado");
                        break;
                    }
                } catch (Exception e) {
                    ErrorWindow.mostrar("Error al recibir mensaje del servidor\n" + e.getMessage());
                }
            }
            // Si el cliente se ha desconectado
            System.out.println("Desconectado del servidor");
        }

        private void emisorArchivo(M_emitirArchivo mensajeEmitir) throws IOException, InterruptedException{
            // Creamos el receptor (socket y streams)
            ServerSocket serSocket = new ServerSocket(0); // 0 para que el SO elija el puerto
            int puertoReceptor = serSocket.getLocalPort();

            //Direccion IP
            InetAddress ip = InetAddress.getLocalHost();

            // Enviamos el mensaje de confirmacion al servidor
                                                    //Emisor          Destino                 IP Emisor  Puerto    Nombre Archivo
            out.writeObject(new M_preparadoCliente(cliente.getName(), mensajeEmitir.getDestino(), ip, puertoReceptor, mensajeEmitir.getNombreArchivo()));
            out.flush();

            // Esperamos a que el servidor se conecte
            Socket socketReceptor = serSocket.accept();
            System.out.println("Conectado con receptor"); 

            // Creamos los streams
            ObjectOutputStream out = new ObjectOutputStream(socketReceptor.getOutputStream());
            
            // Creamos el hilo para enviar el archivo
            Thread hiloEmisor = new Thread(new EmisorArchivo(mensajeEmitir.getNombreArchivo(), out));

            hiloEmisor.start();
            hiloEmisor.join();
            serSocket.close();
            socketReceptor.close();
        }

        private void receptorArchivo(M_preparadoServidor mensajePreparado) throws IOException, InterruptedException{
            // Conectamos con el emisor
            Socket clienteSocket = new Socket(mensajePreparado.getIp(), mensajePreparado.getPuerto());
            System.out.println("Conectado con emisor");

            ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());

            // Creamos el hilo para recibir el archivo
            Thread hiloReceptor = new Thread(new Receptor(mensajePreparado.getNombreArchivo(), in));

            hiloReceptor.start();
            hiloReceptor.join();
            clienteSocket.close();
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        try {
            MainWindow ventana = new MainWindow(cliente);
            ventana.setVisible(true);
        } catch (Exception e) {
            ErrorWindow.mostrar("Error al iniciar el cliente\n" + e.getMessage());
            e.printStackTrace();
        }
    }

}
