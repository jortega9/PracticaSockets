package Mensajes;

public class M_conf_recepcion extends Mensaje{

    public M_conf_recepcion(String origen) {
        this.tipo = TipoMensaje.M_CONF_RECEPCION;
        this.origen = origen;
    }
}
