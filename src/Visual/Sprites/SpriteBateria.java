package Visual.Sprites;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.Tamanio;

/**
 * Representación gráfica de la Batería.
 */
public final class SpriteBateria extends Sprite {

    /**
     * Constructor.
     * @param punto Punto de aparición de la representación gráfica (Esquina superior izquierda).
     */
    public SpriteBateria(Punto punto) {
        super(punto, new Dimension(Tamanio.BATERIA_ANCHO, Tamanio.BATERIA_ALTO), "/Imagenes/Partida/bateria.png");
    }
}
