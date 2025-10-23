package Modelo.Entidades;
import Modelo.Interfaces.Daniable;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.Tamanio;
import Utilidades.TiposEntidades;

/**
 * Muro que protege a la Bateria de las Naves.
 */
public class Muro extends Entidad implements Daniable {
    private int vida;

    /**
     * Constructor.
     * @param punto Punto de aparición del Muro.
     */
    public Muro(Punto punto) {
        super(punto, new Dimension(Tamanio.MURO_ANCHO, Tamanio.MURO_ALTO), TiposEntidades.MURO);
        this.vida = 100;
    }

    public int getVida() {
        return vida;
    }

    // -----------------------
    // Métodos
    // -----------------------

    /**
     * Reduce la vida del muro en 25 unidades.
     */
    @Override
    public void serDaniado() {
        vida -= 5;
        if (vida <= 0) {
            this.setInactivo(true);
        }
    };
}
