package GUI;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExitoWindow extends JFrame{

    public static void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}
