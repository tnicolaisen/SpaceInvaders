package Visual.Sprites;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.Tamanio;

/**
 * Representación gráfica de la Barra de movimiento de la Batería.
 */
public final class SpriteBarra extends Sprite{

    /**
     * Constructor.
     * @param punto Punto de aparición de la representación gráfica (Esquina superior izquierda).
     */
    public SpriteBarra(Punto punto) {
        super(punto, new Dimension(Tamanio.BARRA_ANCHO, Tamanio.BARRA_ALTO), "/Imagenes/barra.png");
    }
}
