package Mensajes;

public class M_verArchivos extends Mensaje{

    public M_verArchivos(String origen) {
        this.tipo = TipoMensaje.M_VER_ARCHIVOS;
        this.origen = origen;
    }
    
}
