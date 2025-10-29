package Visual.Ventanas;
import javax.swing.*;
import Utilidades.Punto;
import Utilidades.Dimension;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Visual.Contenedores.EspacioMenuPrincipal;

public class VentanaJuego extends JFrame {
    private Punto punto;
    private Dimension dimension;
    JPanel espacio;
    private EspacioMenuPrincipal padre;

    /**
     * Constructor. Genera una ventana en donde se ejecutará Space Invaders.
     * @param espacio
     */
    public VentanaJuego(JPanel espacio, EspacioMenuPrincipal padre) {
        this.espacio = espacio;
        this.padre = padre;
        this.setContentPane(espacio);
        configurarVentana();

        // Añado un listener para detectar cuando la ventana se cierra y notificar a la ventana padre
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Llamo al método del padre para reiniciar el Area
                if (VentanaJuego.this.padre != null) {
                    VentanaJuego.this.padre.reiniciarArea();
                }
            }
        });
    }

    /**
     * Configura la ventana a sus valores por defecto.
     */
    private void configurarVentana(){

        // ----------------------------------
        // Posicionamiento y dimensionamiento
        // ----------------------------------
        dimension = new Dimension(
                800,
                900
        );
        // NOTA:
        // No lo vimos en clase. Lo uso para poder saber la resolución de la pantalla del usuario.
        // Funcionamiento similar a mi propia clase Dimension
        java.awt.Dimension resolucionPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        punto = new Punto(
                resolucionPantalla.width / 2 - dimension.getAncho() / 2,
                resolucionPantalla.height / 2 - dimension.getAlto() / 2
        );
        this.setBounds(punto.getPosicionX(), punto.getPosicionY(), dimension.getAncho(), dimension.getAlto());

        // -----------
        // Propiedades
        // -----------
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // NO sale del programa al cerrar esta ventana
        this.setUndecorated(true);
        this.setFocusable(true);
        this.setVisible(true);
    }

    // ------------------------------------
    // Listeners de las teclas presionables
    // ------------------------------------
}