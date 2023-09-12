package GUI;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.event.*;


import Logica.Cliente;

public class PanelLista extends JPanel{

    private JList<String> jlu;

    public PanelLista(MainWindow window, Cliente cliente, ArrayList<String> listaUsuarios) {
        this.setLayout(new BorderLayout());

        //Titulo
        this.add(new JLabel("Lista de usuarios conectados"), BorderLayout.NORTH);

        //Lista
        if(listaUsuarios == null || listaUsuarios.isEmpty()) {
            listaUsuarios = new ArrayList<>();
            listaUsuarios.add("No hay usuarios conectados");
        }

        DefaultListModel<String> modelo = new DefaultListModel<>();

        for(String u : listaUsuarios) {
            modelo.addElement(u);
        }
        jlu = new JList<>(modelo);
        JScrollPane scrollPane = new JScrollPane(jlu);
        this.add(scrollPane, BorderLayout.CENTER);

        //Boton volver 
        JButton atras = new JButton("Volver");
        atras.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.muestraMenuPanel();
            }
        });
        this.add(atras, BorderLayout.SOUTH);


        this.setOpaque(true);
    }

}
