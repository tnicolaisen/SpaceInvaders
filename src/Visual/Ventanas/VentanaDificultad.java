package Visual.Ventanas;
import Modelo.Dificultad;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaDificultad {

    public static Dificultad mostrarDialogo(Component padre) {

        // Se usa para determinar la dificultad. Por defecto es 1 (cadete)
        Dificultad[] dificultadSeleccionada = new Dificultad[1];

        // Creo un JDialog en blanco para customizarlo al mismo estilo que el menu principal
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(padre));
        dialogo.setModal(true); // Me permite bloquear la ventana padre hasta que se cierre esta ventana.
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setLayout(null);

        // -------------------------
        // Dimensiones y fondo
        // -------------------------
        int anchoDialogo = 600;
        int altoDialogo = 600;
        dialogo.setSize(anchoDialogo, altoDialogo);
        dialogo.setLocationRelativeTo(padre);

        // Panel
        JPanel fondo = new JPanel(null);
        fondo.setBounds(0, 0, anchoDialogo, altoDialogo);
        fondo.setBackground(Color.BLACK);

        // Marco de fondo
        JLabel marcoFondo = new JLabel();
        marcoFondo.setBounds(0, 0, 600, 600);
        java.net.URL marcoImgUrl = padre.getClass().getResource("/Imagenes/MenuPrincipal/dificultad-fondo.png");
        marcoFondo.setIcon(new ImageIcon(marcoImgUrl));
        fondo.add(marcoFondo);

        // Título
        JLabel titulo = new JLabel();
        titulo.setVisible(true);
        titulo.setBounds(136, 110, 328, 60);
        java.net.URL tituloImgUrl = padre.getClass().getResource("/Imagenes/MenuPrincipal/seleccionarDificultad.png");
        titulo.setIcon(new ImageIcon(tituloImgUrl));
        fondo.add(titulo);

        // --------------------------
        // Botones
        // --------------------------

        // ----------------------- Ruta de las IMG de los btn
        String rutaCadeteIdle = "/Imagenes/MenuPrincipal/Dificultad/botonCadeteIdle.png";
        String rutaCadeteHover = "/Imagenes/MenuPrincipal/Dificultad/botonCadeteHover.png";
        String rutaCadeteClicked = "/Imagenes/MenuPrincipal/Dificultad/botonCadeteClicked.png";
        String rutaGuerreroIdle = "/Imagenes/MenuPrincipal/Dificultad/botonGuerreroIdle.png";
        String rutaGuerreroHover = "/Imagenes/MenuPrincipal/Dificultad/botonGuerreroHover.png";
        String rutaGuerreroClicked = "/Imagenes/MenuPrincipal/Dificultad/botonGuerreroClicked.png";
        String rutaMasterIdle = "/Imagenes/MenuPrincipal/Dificultad/botonMasterIdle.png";
        String rutaMasterHover = "/Imagenes/MenuPrincipal/Dificultad/botonMasterHover.png";
        String rutaMasterClicked = "/Imagenes/MenuPrincipal/Dificultad/botonMasterClicked.png";

        // Dimensiones y posiciones
        int btnAncho = 300;
        int btnAlto = 60;

        // ----------------- Cadete
        JButton btnCadete = crearBotonConSprite(rutaCadeteIdle, rutaCadeteHover, rutaCadeteClicked);
        btnCadete.setBounds(150, 250, btnAncho, btnAlto);
        btnCadete.addActionListener(e -> {
            dificultadSeleccionada[0] = Dificultad.CADETE;
            dialogo.dispose();
        });
        fondo.add(btnCadete);

        // ----------------- Guerrero
        JButton btnGuerrero = crearBotonConSprite(rutaGuerreroIdle, rutaGuerreroHover, rutaGuerreroClicked);
        btnGuerrero.setBounds(150, 340, btnAncho, btnAlto);
        btnGuerrero.addActionListener(e -> {
            dificultadSeleccionada[0] = Dificultad.GUERRERO;
            dialogo.dispose();
        });
        fondo.add(btnGuerrero);

        // ----------------- Master
        JButton btnMaster = crearBotonConSprite(rutaMasterIdle, rutaMasterHover, rutaMasterClicked);
        btnMaster.setBounds(150, 430, btnAncho, btnAlto);
        btnMaster.addActionListener(e -> {
            dificultadSeleccionada[0] = Dificultad.MASTER;
            dialogo.dispose();
        });
        fondo.add(btnMaster);

        dialogo.getContentPane().add(fondo);
        // Aseguro foco en el diálogo
        dialogo.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                btnCadete.requestFocusInWindow();
            }
        });

        // Mostrar modalmente
        dialogo.setVisible(true);

        return dificultadSeleccionada[0];
    }

    /**
     * Crea un JButton que utiliza sprites para los estados Idle y Hover.
     * Si no encuentra las imágenes, vuelve a un botón textual simple.
     */
    private static JButton crearBotonConSprite(String rutaIdle, String rutaHover, String rutaClicked) {
        JButton btn = new JButton();
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        try {
            ImageIcon idle = new ImageIcon(VentanaDificultad.class.getResource(rutaIdle));
            ImageIcon hover = new ImageIcon(VentanaDificultad.class.getResource(rutaHover));
            ImageIcon clicked = new ImageIcon(VentanaDificultad.class.getResource(rutaClicked));
            btn.setIcon(idle);
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setIcon(hover); }
                @Override public void mouseExited(MouseEvent e) { btn.setIcon(idle); }
                @Override public void mousePressed(MouseEvent e) { btn.setIcon(clicked); }
            });
        } catch (Exception ex) {
            // Si no encuentra las imágenes muestra un texto por defecto
            btn.setText(rutaIdle.contains("Cadete") ? "CADETE" :
                    rutaIdle.contains("Guerrero") ? "GUERRERO" : "MASTER");
            btn.setBackground(Color.BLACK);
            btn.setForeground(new Color(0xFFD300));
            btn.setOpaque(true);
        }
        return btn;
    }
}
