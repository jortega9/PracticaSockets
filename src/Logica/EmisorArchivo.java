package Logica;

import java.io.*;

public class EmisorArchivo implements Runnable {
    //Clase EmisorArchivo que se encarga de enviar el archivo al cliente2

    private String nombreArchivo; // Nombre del archivo a enviar
    private ObjectOutputStream out; // Flujo de salida

    public EmisorArchivo(String nombreArchivo,  ObjectOutputStream out) {
        this.nombreArchivo = nombreArchivo;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            // Se abre el archivo a enviar
            File archivo = new File(nombreArchivo);
            FileInputStream fis = new FileInputStream(archivo);
            BufferedInputStream bis = new BufferedInputStream(fis);

            // Se envían los datos del archivo
            byte[] buffer = new byte[4096];
            int leidos;
            while ((leidos = bis.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }

            out.flush();
            bis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
