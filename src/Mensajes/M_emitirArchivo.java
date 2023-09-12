package Mensajes;

public class M_emitirArchivo extends Mensaje{

    private String nombreArchivo;
    private String destino;
    
    public M_emitirArchivo(String origen, String destino, String nombreArchivo) {
        this.tipo = TipoMensaje.M_EMITIR_ARCHIVO;
        this.origen = origen;
        this.destino = destino;
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getDestino() {
        return destino;
    }
    
}
