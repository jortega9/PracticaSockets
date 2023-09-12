package Mensajes;

import java.util.ArrayList;

public class M_verArchivosConf extends Mensaje{

    //Mensaje que se envia al cliente para confirmar la lista de ficheros
    private ArrayList<String> archivos;

    public M_verArchivosConf(ArrayList <String> archivos) {
        this.tipo = TipoMensaje.M_VER_ARCHIVOS_CONF;
        this.archivos = new ArrayList<>(archivos);
    }

    public ArrayList<String> getArchivos() {
        return archivos;
    }
    
}
