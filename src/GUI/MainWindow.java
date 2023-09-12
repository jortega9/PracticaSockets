package GUI;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Logica.Cliente;

public class MainWindow extends JFrame{

    private Cliente cliente;
    private PanelInicio panelInicio;

    public MainWindow(Cliente cliente) {
        this.cliente = cliente;
        this.setTitle("Descarga cosas");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.setContentPane(muestraInicioPanel(cliente));
    }

    private JPanel muestraInicioPanel(Cliente cliente) {
        panelInicio = new PanelInicio(cliente);
        return panelInicio;
    }

    public void muestraMenuPanel() {
        this.setContentPane(new PanelMenu(cliente));
        this.setTitle("Bienvenido " + cliente.getName());
        this.revalidate();
    }

    public void muestraListaPanel(ArrayList<String> lista) {
        this.setContentPane(new PanelLista(this, cliente, lista));
        this.revalidate();
    }

    public void muestraFicheroPanel(ArrayList<String> lista) {
        this.setContentPane(new PanelFicheros(cliente,this, lista));
        this.revalidate();
    }

    public String getNombreCliente() {
        return panelInicio.getNombreCliente();
    }    
}
