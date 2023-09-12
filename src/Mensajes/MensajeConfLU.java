package Mensajes;

import java.util.ArrayList;

public class MensajeConfLU extends Mensaje{

    //Mensaje que se envia al cliente para confirmar la lista de usuarios
    
    private ArrayList<String> usuarios;

    public MensajeConfLU(ArrayList <String> usuarios) {
        this.tipo = TipoMensaje.LISTA_USUARIOS_CONFIRM;
        this.usuarios = new ArrayList<>(usuarios);
    }

    public ArrayList<String> getUsuarios() {
        return usuarios;
    }
    
}
