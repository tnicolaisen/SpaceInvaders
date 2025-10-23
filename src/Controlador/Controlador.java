package Controlador;
import Modelo.Area;
import Visual.Ventanas.EspacioJuego;
import Visual.Ventanas.Ventana;

public class Controlador {
    private EspacioJuego espacioJuego;
    private Ventana ventana;
    private Area area;

    public Controlador(){
        espacioJuego = new EspacioJuego(this);
        area = new Area(espacioJuego, 1);
        ventana = new Ventana(espacioJuego);
    }

    public void moverseDerecha(){area.moverJugadorDerecha();}
    public void moverseIzquierda(){area.moverJugadorIzquierda();}
    public void disparar(){area.dispararJugador();}
}
