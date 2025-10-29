package Visual.Ventanas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;

/**
 * Diálogo modal para solicitar el nombre del jugador al finalizar la partida.
 */
public class VentanaFinPartida {

    /**
     * Muestra un diálogo modal pidiendo el nombre del jugador.
     * @param padre Componente padre sobre el cual centrar el diálogo.
     * @param mensaje Mensaje a mostrar (por ejemplo "¡GANASTE!" o "PERDISTE").
     * @return Nombre ingresado en mayúsculas (máx 3 caracteres), o null si canceló.
     */
    public static String mostrarDialogo(Component padre, String mensaje) {

        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(padre));
        dialogo.setModal(true);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setLayout(null);

        // -------------------------
        // Dimensiones y fondo
        // -------------------------

        int ancho = 600;
        int alto = 600;
        dialogo.setSize(ancho, alto);
        dialogo.setLocationRelativeTo(padre);

        // Panel
        JPanel fondo = new JPanel(null);
        fondo.setBounds(0, 0, ancho, alto);
        fondo.setBackground(Color.BLACK);

        // Marco de fondo
        JLabel marcoFondo = new JLabel();
        marcoFondo.setBounds(0, 0, ancho, alto);
        java.net.URL marcoUrl = VentanaFinPartida.class.getResource("/Imagenes/MenuPrincipal/dificultad-fondo.png");
        if (marcoUrl != null) {
            marcoFondo.setIcon(new ImageIcon(marcoUrl));
        }
        fondo.add(marcoFondo);

        // --------------------------------
        // Elementos visibles
        // --------------------------------

        // Titulo
        JLabel titulo = new JLabel();
        titulo.setVisible(true);
        if (mensaje == "Ganaste") {
            titulo.setBounds(166, 150, 268, 32);
            java.net.URL tituloImgUrl = padre.getClass().getResource("/Imagenes/Partida/ganaste.png");
            titulo.setIcon(new ImageIcon(tituloImgUrl));
        }
        else {
            titulo.setBounds(160, 150, 280, 32);
            java.net.URL tituloImgUrl = padre.getClass().getResource("/Imagenes/Partida/perdiste.png");
            titulo.setIcon(new ImageIcon(tituloImgUrl));
        }
        fondo.add(titulo);

        // texto Creditos
        JLabel creditosTexto = new JLabel();
        creditosTexto.setVisible(true);
        creditosTexto.setBounds(114, 238, 256, 40);
        java.net.URL creditosTextoImgUrl = padre.getClass().getResource("/Imagenes/Partida/creditos.png");
        creditosTexto.setIcon(new ImageIcon(creditosTextoImgUrl));
        fondo.add(creditosTexto);

        // texto Puntos
        JLabel puntosTexto = new JLabel();
        puntosTexto.setVisible(true);
        puntosTexto.setBounds(114, 330, 208, 28);
        java.net.URL puntosTextoImgUrl = padre.getClass().getResource("/Imagenes/Partida/puntos.png");
        puntosTexto.setIcon(new ImageIcon(puntosTextoImgUrl));
        fondo.add(puntosTexto);

        // texto Jugador
        JLabel jugadorTexto = new JLabel();
        jugadorTexto.setVisible(true);
        jugadorTexto.setBounds(114, 410, 236, 28);
        java.net.URL jugadorTextoImgUrl = padre.getClass().getResource("/Imagenes/Partida/jugador.png");
        jugadorTexto.setIcon(new ImageIcon(jugadorTextoImgUrl));
        fondo.add(jugadorTexto);

        // TextField del nombre
        JTextField campoNombre = new JTextField();
        campoNombre.setBounds(374, 410, 112, 40);
        campoNombre.setFont(new Font("Monospaced", Font.BOLD, 22));
        campoNombre.setBackground(Color.BLACK);
        campoNombre.setForeground(new Color(0xFFD300));
        campoNombre.setBorder(BorderFactory.createLineBorder(new Color(0xFFD300), 4));
        fondo.add(campoNombre);

        JButton btnOk = new JButton();
        btnOk.setBounds(254, 489, 92, 60);
        btnOk.setFocusPainted(false);
        btnOk.setBackground(Color.BLACK);
        btnOk.setForeground(new Color(0xFFD300));
        btnOk.setBorderPainted(false);
        btnOk.setOpaque(true);
        btnOk.setIcon(new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkIdle.png")));

        final String[] resultado = new String[1];
        resultado[0] = null;

        btnOk.addMouseListener(new MouseAdapter() {

            ImageIcon idle = new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkIdle.png"));
            ImageIcon hover = new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkHover.png"));
            ImageIcon clicked = new ImageIcon(VentanaDificultad.class.getResource("/Imagenes/Partida/botonOkClicked.png"));

            @Override public void mouseEntered(MouseEvent e) { btnOk.setIcon(hover); }
            @Override public void mouseExited(MouseEvent e) { btnOk.setIcon(idle); }
            @Override public void mousePressed(MouseEvent e) { btnOk.setIcon(clicked); }

            @Override public void mouseClicked(MouseEvent e) {
                String nombre = campoNombre.getText();
                if (nombre != null) nombre = nombre.trim().toUpperCase();
                if (nombre != null && nombre.length() > 0) {
                    if (nombre.length() > 3) nombre = nombre.substring(0,3);
                    resultado[0] = nombre;
                    dialogo.dispose();
                }
            }
        });

        fondo.add(btnOk);
        dialogo.getContentPane().add(fondo);

        dialogo.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override public void windowGainedFocus(java.awt.event.WindowEvent e) {
                campoNombre.requestFocusInWindow();
            }
        });

        dialogo.setVisible(true);

        return resultado[0];
    }
}