package Controlador;
import Modelo.Area;
import Utilidades.VigilanteEDT;
import Visual.Contenedores.EspacioMenuPrincipal;
import Visual.Ventanas.VentanaMenuPrincipal;
import Utilidades.Punto;

/**
 * Controlador principal de la aplicación. Orquesta la vista del menú y el modelo Area.
 */
public class Controlador {
    private EspacioMenuPrincipal espacioMenuPrincipal;
    private VentanaMenuPrincipal ventanaMenuPrincipal;
    private Area area;

    /**
     * Constructor.
     */
    public Controlador() {
        espacioMenuPrincipal = new EspacioMenuPrincipal(this);
        area = espacioMenuPrincipal.getArea();
        ventanaMenuPrincipal = new VentanaMenuPrincipal(espacioMenuPrincipal);
        new Thread(new Runnable() {
            public void run() {
                // iniciar vigilante en background
                new VigilanteEDT(Controlador.this).iniciar();
            }
        }).start();
    }

    /**
     * Hace que el jugador se mueva hacia la derecha.
     */
    public void moverseDerecha(){area.moverJugadorDerecha();}

    /**
     * Hace que el jugador se mueva hacia la izquierda.
     */
    public void moverseIzquierda(){area.moverJugadorIzquierda();}

    /**
     * Hace que el jugador dispare.
     */
    public void disparar(){area.dispararJugador();}

    /**
     * Detiene la lógica del juego (timers) inmediatamente.
     */
    public void detenerJuego() {
        if (area != null) area.detener();
    }

    /**
     * Pide al EspacioMenuPrincipal que cierre (dispose) la ventana de juego si existe.
     */
    public void cerrarVentanaDelJuego() {
        if (espacioMenuPrincipal != null) espacioMenuPrincipal.cerrarVentanaJuego();
    }
}