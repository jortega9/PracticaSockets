package Mensajes;

public class MensajeLista extends Mensaje{

    public MensajeLista(String origen) {
        this.tipo = TipoMensaje.M_LISTA;
        this.origen = origen;
    }
}
