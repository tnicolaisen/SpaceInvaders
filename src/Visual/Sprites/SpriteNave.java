package Visual.Sprites;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.Tamanio;

/**
 * Representación gráfica de la Nave.
 */
public final class SpriteNave extends Sprite{

    /**
     * Constructor.
     * @param punto Punto de aparición de la representación gráfica (Esquina superior izquierda).
     */
    public SpriteNave(Punto punto) {
        super(punto, new Dimension(Tamanio.NAVE_ANCHO, Tamanio.NAVE_ALTO), "/Imagenes/nave.png");
    }
}
