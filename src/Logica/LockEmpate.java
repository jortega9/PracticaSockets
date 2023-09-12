package Logica;

public class LockEmpate {
    //TODO ver si es mejor hacerlo para M o para 2.

    //Usaremos el algoritmo rompe empate para 2 hilos, lo usaremos para controlar la variable connected del 
    //cliente, a la que acceden el hilo principal del cliente y el hilo OyenteServidor.
    //Por tanto usaremos la implementación de la clase Lock para 2 hilos ya que generalizarlo y hacerlo para N 
    //sabiendo que solo hay 2 hilos es innecesario y gastaría más memoria.

    //Hemos elegido este algoritmo ya que funciona muy bien para 2 hilos, y en nuestro caso es lo que necesitamos.

    volatile Boolean in0, in1; //in0 para el cliente e in1 para el oyenteServidor
    volatile int last; //last = 0 para el cliente y last = 1 para el oyenteServidor

    public LockEmpate() {
        in0 = false;
        in1 = false;
        last = 0;
    }

    public void lock(int id) {
        if (id == 0) { //Cliente
            last = 0; in0 = true;  //El cliente entra en la sección crítica
            while (in1) { // Mientras el oyenteServidor esté en la sección crítica
                if (last == 1) {
                    in0 = false;
                    while (last == 1) {
                    }
                    in0 = true;
                }
            }
        } else { //OyenteServidor
            last = 1; in1 = true; 
            while (in0) { // Mientras el cliente esté en la sección crítica
                if (last == 0) {
                    in1 = false;
                    while (last == 0) {
                    }
                    in1 = true;
                }
            }
        }
    }

    public void unlock(int id) {
        if (id == 0) { //Cliente
            in0 = false;
        } else { //OyenteServidor
            in1 = false;
        }
    }
}
