package GUI;

import java.util.ArrayList;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;

import Logica.Cliente;

public class PanelFicheros extends JPanel {

    public PanelFicheros(Cliente cliente, MainWindow window, ArrayList<String> listaFicheros) {
        this.setLayout(new BorderLayout());

        // Titulo
        this.add(new JLabel("Lista de ficheros disponibles"), BorderLayout.NORTH);

        // Lista
        if (listaFicheros == null || listaFicheros.isEmpty()) {
            JLabel aviso = new JLabel("No hay ficheros disponibles");
            this.add(aviso, BorderLayout.CENTER);
        }
        else{
            JScrollPane scrollPane = mostrarLista(listaFicheros, cliente);
            this.add(scrollPane, BorderLayout.CENTER);
        }

        // Boton volver
        JButton atras = botonVolver(window);
        this.add(atras, BorderLayout.SOUTH);

        this.setOpaque(true);
    }

    private JScrollPane mostrarLista(ArrayList<String> listaFicheros, Cliente cliente){
        JPanel lista = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(lista);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.weighty = 0.0;
        
        int i = 0;
        for (String u : listaFicheros) {
            JButton boton = new JButton(u);
            boton.setPreferredSize(new Dimension(200, 30));
            c.gridx = 0;
            c.gridy = i;
            
            boton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cliente.descargarFichero(u);
                }
            });
            lista.add(boton, c);
            i++;
        }

        return scrollPane;
    }

    private JButton botonVolver(MainWindow window){
        JButton atras = new JButton("Volver");
        atras.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.muestraMenuPanel();
            }
        });
        return atras;
    }
}

