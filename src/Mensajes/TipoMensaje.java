package Mensajes;

public class TipoMensaje {

    public static final String M_CONEXION = "MensajeConexion";
    public static final String M_LOGOFF = "MensajeLogoff";
    public static final String M_PED_ARCHIVO = "MensajePeticionArchivo";
    public static final String M_LISTA = "MensajeLista";
    public static final String CONFIRM_CONNECT = "ConfirmConnect";
    public static final String LISTA_USUARIOS_CONFIRM = "ListaUsuariosConfirm";
    public static final String M_EMITIR_ARCHIVO = "MEmitirArchivo";
    public static final String M_PREPARADO_C = "MPreparadoC";
    public static final String M_PREPARADO_S = "MPreparadoS";
    public static final String M_VER_ARCHIVOS = "MensajeVerArchivos";
    public static final String M_VER_ARCHIVOS_CONF = "MensajeVerArchivosConfirm";
    public static final String M_CONF_RECEPCION = "MConfRecepcion";

    public String getTipo(TipoMensaje tipo) {
        return tipo.toString();
    }
    
}
