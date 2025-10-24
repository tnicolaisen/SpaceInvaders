package Controlador;
import Modelo.Area;
import Visual.Ventanas.EspacioJuego;
import Visual.Ventanas.VentanaJuego;

public class Controlador {
    private EspacioJuego espacioJuego;
    private VentanaJuego ventana;
    private Area area;

    /**
     * Constructor. Genera un controlador de Space Invaders.
     */
    public Controlador(){
        espacioJuego = new EspacioJuego(this);
        area = new Area(espacioJuego, 1);
        ventana = new VentanaJuego(espacioJuego);
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
