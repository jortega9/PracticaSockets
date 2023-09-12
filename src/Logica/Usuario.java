package Logica;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class Usuario implements Serializable {

    private String name; // Nombre del usuario
    private InetAddress ip; // IP del usuario
    private ArrayList<String> info; // Lista de ficheros del usuario

    public Usuario(String name, InetAddress ip) {
        this.name = name;
        this.ip = ip;
        try {
            info = sacarArchivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> sacarArchivos() throws IOException {
        ArrayList<String> aux = new ArrayList<>();

        File archivo = new File(Constantes.PATH + this.name);
        //Si no es la primera vez que se conecta el usuario
        if (archivo.exists()) {
            String[] listado = archivo.list();
            for (int i = 0; i < listado.length; i++) {
                aux.add(listado[i]);
            }
        } else { //Si es la primera vez que se conecta el usuario creamos el directorio
            if (archivo.mkdirs())
                System.out.println("Directorio creado para usuario: " + this.name);
            else
                System.out.println("Error al crear directorio para usuario: " + this.name);
        }

        return aux;
    }

    public String getName() {
        return name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public ArrayList<String> getInfo() {
        return info;
    }

    public void actualizarListaFicheros() {
        try {
            this.info = sacarArchivos(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
