package Visual.Sprites;
import Utilidades.Dimension;
import Utilidades.Tamanio;
import Utilidades.Punto;

/**
 * Representación gráfica del Muro.
 */
public final class SpriteMuro extends Sprite {

    /**
     * Constructor.
     * @param punto Punto de aparición de la representación gráfica (Esquina superior izquierda).
     */
    public SpriteMuro(Punto punto) {
        super(punto, new Dimension(Tamanio.MURO_ANCHO, Tamanio.MURO_ALTO), "/Imagenes/Partida/muro100.png");
    }
}
