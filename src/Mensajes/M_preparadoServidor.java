package Mensajes;

import java.net.InetAddress;

public class M_preparadoServidor extends Mensaje{

    private InetAddress ip;
    private int puerto;
    private String nombreArchivo;

    public M_preparadoServidor(String origen, InetAddress inetAddress, int puerto, String nombreArchivo) {
        this.tipo = TipoMensaje.M_PREPARADO_S;
        this.origen = origen;
        this.ip = inetAddress;
        this.puerto = puerto;
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }
}
