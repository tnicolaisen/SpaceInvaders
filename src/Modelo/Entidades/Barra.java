package Modelo.Entidades;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.Tamanio;
import Utilidades.TiposEntidades;

/**
 * Ayuda visual para determinar el área de movimiento de la Batería
 */
public class Barra extends Entidad {

    /**
     * Constructor.
     * @param punto Punto de aparición de la Barra.
     */
    public Barra(Punto punto) {
        super(punto, new Dimension(Tamanio.BARRA_ANCHO, Tamanio.BARRA_ALTO), TiposEntidades.BARRA);
    }
}
