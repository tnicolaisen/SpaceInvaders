package Controlador;
import Modelo.Area;
import Visual.Contenedores.EspacioMenuPrincipal;
import Visual.Ventanas.VentanaMenuPrincipal;

public class Controlador {
    private EspacioMenuPrincipal espacioMenuPrincipal;
    private VentanaMenuPrincipal ventanaMenuPrincipal;
    private Area area;

    public Controlador() {
        espacioMenuPrincipal = new EspacioMenuPrincipal(this);
        area = espacioMenuPrincipal.getArea();
        ventanaMenuPrincipal = new VentanaMenuPrincipal(espacioMenuPrincipal);
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
}
