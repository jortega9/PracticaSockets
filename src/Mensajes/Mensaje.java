package Mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {

	String tipo;
	String origen, destino;
	
	public String getTipo() {
		return tipo;
	}
	
	public String getOrigen() {
		return origen;
	}
	
	public String getDestino() {
		return destino;
	}
}
