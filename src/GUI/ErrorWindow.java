package GUI;

import javax.swing.*;

public class ErrorWindow extends JFrame{

    public static void mostrar(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
}
