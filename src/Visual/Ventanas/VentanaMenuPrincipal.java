package Visual.Ventanas;
import javax.swing.*;

import Controlador.Controlador;
import Utilidades.Punto;
import Utilidades.Dimension;
import Visual.Contenedores.EspacioMenuPrincipal;

import java.awt.*;

public class VentanaMenuPrincipal extends JFrame {
    private Punto punto;
    private Dimension dimension;
    JPanel espacio;

    public VentanaMenuPrincipal(EspacioMenuPrincipal espacio) {

        configurarVentana();

        // crear y añadir el panel antes de mostrar la ventana
        this.espacio = espacio;
        this.setContentPane(espacio);

        // forzar validación y repintado antes de mostrar
        this.validate();
        this.repaint();

        // mostrar al final, después de haber agregado contenidos
        this.setVisible(true);
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
        this.setFocusable(true);
        this.setUndecorated(true);
    }

    // ------------------------------------
    // Listeners de las teclas presionables
    // ------------------------------------
}