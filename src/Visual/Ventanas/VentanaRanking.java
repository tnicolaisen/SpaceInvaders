package Visual.Ventanas;
import Modelo.Jugador;
import Modelo.Ranking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Diálogo modal que muestra el ranking completo de jugadores.
 */
public class VentanaRanking {

    /**
     * Muestra un diálogo modal con la lista de jugadores ordenada por puntaje.
     * @param padre Componente padre sobre el cual centrar el diálogo.
     */
    public static void mostrarDialogo(Component padre) {

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(padre));
        dialogo.setModal(true);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setLayout(null);

        int ancho = 600;
        int alto = 600;
        dialogo.setSize(ancho, alto);
        dialogo.setLocationRelativeTo(padre);

        JPanel fondo = new JPanel(null);
        fondo.setBounds(0, 0, ancho, alto);
        fondo.setBackground(Color.BLACK);

        JLabel marcoFondo = new JLabel();
        marcoFondo.setBounds(0, 0, ancho, alto);
        java.net.URL marcoUrl = VentanaRanking.class.getResource("/Imagenes/MenuPrincipal/dificultad-fondo.png");
        if (marcoUrl != null) {
            marcoFondo.setIcon(new ImageIcon(marcoUrl));
        }
        fondo.add(marcoFondo);

        JLabel titulo = new JLabel();
        titulo.setVisible(true);
        titulo.setBounds(192, 46, 216, 28);
        java.net.URL tituloImgUrl = padre.getClass().getResource("/Imagenes/MenuPrincipal/ranking.png");
        titulo.setIcon(new ImageIcon(tituloImgUrl));
        fondo.add(titulo);

        DefaultListModel<String> modeloLista = new DefaultListModel<String>();
        JList<String> lista = new JList<String>(modeloLista);
        lista.setFont(new Font("Monospaced", Font.PLAIN, 18));
        lista.setBackground(Color.BLACK);
        lista.setForeground(new Color(0xFFD300));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBounds(60, 110, 480, 360);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xFFD300), 4));
        fondo.add(scroll);

        JButton btnOk = new JButton();
        btnOk.setBounds(254, 500, 92, 60);
        btnOk.setFocusPainted(false);
        btnOk.setBackground(Color.BLACK);
        btnOk.setForeground(new Color(0xFFD300));
        btnOk.setBorderPainted(false);
        btnOk.setOpaque(true);
        btnOk.setIcon(new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkIdle.png")));

        btnOk.addMouseListener(new MouseAdapter() {

            ImageIcon idle = new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkIdle.png"));
            ImageIcon hover = new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkHover.png"));
            ImageIcon clicked = new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkClicked.png"));

            @Override public void mouseEntered(MouseEvent e) { btnOk.setIcon(hover); }
            @Override public void mouseExited(MouseEvent e) { btnOk.setIcon(idle); }
            @Override public void mousePressed(MouseEvent e) { btnOk.setIcon(clicked); }

            @Override public void mouseClicked(MouseEvent e) {
                dialogo.dispose();
            }
        });

        fondo.add(btnOk);
        dialogo.getContentPane().add(fondo);

        dialogo.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override public void windowGainedFocus(java.awt.event.WindowEvent e) {
                btnOk.requestFocusInWindow();
            }
        });

        // Cargar datos del ranking
        modeloLista.clear();
        List<Jugador> listaJugadores = Ranking.obtenerTodos();
        if (listaJugadores.isEmpty()) {
            modeloLista.addElement("Sin entradas en el ranking");
        } else {
            int pos = 1;
            for (Jugador j : listaJugadores) {
                String linea = String.format("%02d. %s - %d", pos, j.getNombre(), j.getPuntaje());
                modeloLista.addElement(linea);
                pos++;
            }
        }

        dialogo.setVisible(true);
    }
}