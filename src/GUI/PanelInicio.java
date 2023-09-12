package GUI;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import Logica.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class PanelInicio extends JPanel {

    private JTextField login;
    private JButton cliente;
    private JLabel mensaje;
    private Cliente clienteC;
    private String nombreCliente;

    public PanelInicio(Cliente cliente) {
        this.clienteC = cliente;
        this.setLayout(new GridBagLayout());
        this.setOpaque(true);
        addComponents();
        addActions();
    }

    private void addComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets.top = 10;
        c.anchor = GridBagConstraints.CENTER;
        add(getMessage(), c);

        c.gridy++;
        c.insets.top = 20;
        add(getLogin(), c);

        c.gridy++;
        c.insets.top = 20;
        add(iniciar(), c);
    }

    private JLabel getMessage(){
        if (mensaje == null) {
            mensaje = new JLabel("Introduce un nombre de usuario");
        }
        return mensaje;
    }

    private JTextField getLogin() {
        if (login == null) {
            login = new JTextField();
            login.setColumns(15);
        }
        return login;
    }

    private JButton iniciar(){
        if (cliente == null) {
            cliente = new JButton("Iniciar");
            cliente.setPreferredSize(new Dimension(100, 30));
        }
        return cliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    private void addActions() {
        
        cliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nombreCliente = login.getText();
                if (nombreCliente != null && !nombreCliente.equals("")) {
                    try {
                        Window window = SwingUtilities.getWindowAncestor(PanelInicio.this);
                        clienteC.startConnection(6666, window);
                    } catch (Exception e1) {
                        ErrorWindow.mostrar("Error al conectar el cliente " + nombreCliente + " al servidor\n" + e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
}


