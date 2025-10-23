package Modelo.Entidades;
import Modelo.Interfaces.Daniable;
import Modelo.Interfaces.Disparador;
import Utilidades.*;

/**
 * Nave enemiga que puede atacar a la Batería
 */
public class Nave extends Entidad implements Disparador, Daniable {

    int vida;

    /**
     * Constructor.
     * @param punto Punto de aparición de la Nave.
     */
    public Nave(Punto punto){
        super(punto, new Dimension(Tamanio.NAVE_ANCHO, Tamanio.NAVE_ALTO), TiposEntidades.NAVE);
        vida = 100;
    }

    // ----------------------
    // Métodos
    // ----------------------

    /**
     * Dispara un Proyectil hacia ABAJO.
     * @return Proyectil disparado.
     */
    @Override
    public Proyectil disparar(){
        return new Proyectil(
                new Punto(
                        this.getEsquinaSuperiorIzquierda().getPosicionX() + 18, this.getEsquinaSuperiorIzquierda().getPosicionY() + 50
                ), Direcciones.ABAJO
        );
    }

    @Override
    public int getVida(){return vida;}

    /**
     * Daña (y mata) a la Nave.
     */
    @Override
    public void serDaniado(){
        this.setInactivo(true);
        vida -= 100;
    }
}
