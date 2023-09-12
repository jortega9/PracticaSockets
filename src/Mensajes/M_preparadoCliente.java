package Mensajes;

import java.net.InetAddress;

public class M_preparadoCliente extends Mensaje{

    private InetAddress inetAddress;
    private String nombreArchivo;
    private int puertoReceptor;

    // Cliente que va a emitir el archivo (origen) con su ip y puerto para que el otro cliente se conecte
    public M_preparadoCliente(String origen, String destino, InetAddress inetAddress, int puertoReceptor, String nombreArchivo) {
        this.tipo = TipoMensaje.M_PREPARADO_C;
        this.origen = origen;
        this.destino = destino;
        this.inetAddress = inetAddress;
        this.nombreArchivo = nombreArchivo;
        this.puertoReceptor = puertoReceptor;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public InetAddress getIp() {
        return inetAddress;
    }

    public int getPuertoReceptor() {
        return puertoReceptor;
    }
}
