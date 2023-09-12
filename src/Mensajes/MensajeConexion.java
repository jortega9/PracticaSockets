package Mensajes;

import Logica.Usuario;

public class MensajeConexion extends Mensaje{

    private Usuario usuario;

    public MensajeConexion(Usuario usuario) {
        this.tipo = TipoMensaje.M_CONEXION;
        this.usuario = new Usuario(usuario.getName(), usuario.getIp());
    }
    
    public Usuario getUsuario() {
        return usuario;
    }

    public String getOrigen() {
        return usuario.getName();
    }
}
