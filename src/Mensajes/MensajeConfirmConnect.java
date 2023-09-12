package Mensajes;

public class MensajeConfirmConnect extends Mensaje {

    private Boolean exito;
    
    public MensajeConfirmConnect(String origen, boolean b) {
        this.tipo = TipoMensaje.CONFIRM_CONNECT;
        this.origen = origen;
        this.exito = b;
    }

    public Boolean getExito() {
        return exito;
    }
    
}
