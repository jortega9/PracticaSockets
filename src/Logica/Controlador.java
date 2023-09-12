package Logica;

public interface Controlador {

    //Interfaz controlador para el monitor y para el semaforo de ambas tablas.

    public void requestEscribir();

    public void releaseEscribir();

    public void requestLeer();

    public void releaseLeer();
}
