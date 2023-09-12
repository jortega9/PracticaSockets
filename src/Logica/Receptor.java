package Logica;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import GUI.ErrorWindow;

public class Receptor implements Runnable{
    //Clase Receptor que se encarga de recibir el archivo que envia el cliente1
    
    private String nombreArchivo; // Nombre del archivo a recibir ya con el path
    private ObjectInputStream in; // Flujo de entrada

    public Receptor(String nombreArchivo, ObjectInputStream in) {
        this.nombreArchivo = nombreArchivo;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            // Recibimos el archivo
            byte[] buffer = new byte[4096];
            int leidos = 0;
            FileOutputStream fos = new FileOutputStream(nombreArchivo);
            while ((leidos = in.read(buffer)) != -1) {
                fos.write(buffer, 0, leidos);
            }
            fos.close();
            in.close();
            System.out.println("Archivo recibido"); // Imprimimos por consola que el archivo se ha recibido
        } catch (IOException e) {
            ErrorWindow.mostrar("Error al recibir el archivo\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
