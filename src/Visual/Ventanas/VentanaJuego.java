package Visual.Ventanas;
import javax.swing.*;
import Utilidades.Punto;
import Utilidades.Dimension;
import java.awt.*;

public class VentanaJuego extends JFrame {
    private Punto punto;
    private Dimension dimension;
    JPanel espacio;

    /**
     * Constructor. Genera una ventana en donde se ejecutará Space Invaders.
     * @param espacio
     */
    public VentanaJuego(JPanel espacio) {
        this.espacio = espacio;
        this.setContentPane(espacio);
        configurarVentana();


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
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setFocusable(true);
        this.setVisible(true);
    }

    // ------------------------------------
    // Listeners de las teclas presionables
    // ------------------------------------
}
