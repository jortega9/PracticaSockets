package Mensajes;

public class MensajePeticionFichero extends Mensaje{
    
    private String nombreFichero;

    // Peticion de Fichero
    
    public MensajePeticionFichero(String origen, String nombreFichero) {
        this.tipo = TipoMensaje.M_PED_ARCHIVO;
        this.origen = origen;
        this.nombreFichero = nombreFichero;
    }
    
    public String getNombreFichero() {
        return nombreFichero;
    }
    
    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }
}
