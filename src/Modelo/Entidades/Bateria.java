package Modelo.Entidades;
import Modelo.Interfaces.Daniable;
import Modelo.Interfaces.Disparador;
import Utilidades.*;

/**
 * Batería controlada por el jugador que puede disparar proyectiles
 */
public class Bateria extends Entidad implements Disparador, Daniable {

    private int vida;

    /**
     * Constructor.
     * @param punto Punto de aparición de la batería
     */
    public Bateria(Punto punto) {
        super(punto, new Dimension(Tamanio.BATERIA_ANCHO, Tamanio.BATERIA_ALTO), TiposEntidades.BATERIA);
        this.vida = 100;
    }

    // ----------------------
    // Getters
    // ----------------------

    /**
     * Devuelve la vida de la batería.
     * @return Vida de la batería.
     */
    public int getVida() {return this.vida;}

    // ----------------------
    // Métodos
    // ----------------------

    @Override
    /**
     * Dispara un Proyectil hacia ARRIBA.
     * @return Proyectil disparado.
     */
    public Proyectil disparar() {
        return new Proyectil(
                new Punto(
                        this.getEsquinaSuperiorIzquierda().getPosicionX() + 18,
                        this.getEsquinaSuperiorIzquierda().getPosicionY() - 15
                ), Direcciones.ARRIBA
        );
    }

    /**
     * Reduce la vida de la batería en 10 unidades.
     */
    @Override
    public void serDaniado() {
        this.vida -= 10;
        if (vida <= 0) {
            this.setInactivo(true);
        }
    }
}
