package GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.*;

import Logica.*;
import Mensajes.*;

public class PanelMenu extends JPanel {

    private Cliente cliente;
    private JButton lista, fichero, salir;

    public PanelMenu(Cliente cliente) {
        this.cliente = cliente;
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
        add(opcionLista(), c);

        c.gridy++;
        c.insets.top = 20;
        add(pedirFichero(), c);

        c.gridy++;
        c.insets.top = 20;
        add(salir(), c);
    }

    private JButton opcionLista() {
        if (lista == null) {
            lista = new JButton("Lista de usuarios");
            lista.setPreferredSize(new Dimension(200, 30));
        }
        return lista;
    }

    private JButton pedirFichero() {
        if (fichero == null) {
            fichero = new JButton("Ver ficheros disponibles");
            fichero.setPreferredSize(new Dimension(200, 30));
        }
        return fichero;
    }

    private JButton salir() {
        if (salir == null) {
            salir = new JButton("Salir");
            salir.setPreferredSize(new Dimension(200, 30));
        }
        return salir;
    }

    private void addActions() {
        lista.addActionListener(e -> {
            cliente.elegirOpcion(new MensajeLista(cliente.getName()));
        });
        fichero.addActionListener(e -> {
            cliente.elegirOpcion(new M_verArchivos(cliente.getName()));
        });
        salir.addActionListener(e -> {
            cliente.elegirOpcion(new MensajeLogoff(cliente.getName()));
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }
}
