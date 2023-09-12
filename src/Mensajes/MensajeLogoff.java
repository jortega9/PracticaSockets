package Mensajes;

public class MensajeLogoff extends Mensaje{

    public MensajeLogoff(String origen) {
        this.tipo = TipoMensaje.M_LOGOFF;
        this.origen = origen;
    }
    
}
