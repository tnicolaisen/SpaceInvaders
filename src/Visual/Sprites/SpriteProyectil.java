package Visual.Sprites;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.Tamanio;

/**
 * Representación gráfica del Proyectil.
 */
public final class SpriteProyectil extends Sprite {

    /**
     * Constructor.
     * @param punto Punto de aparición de la representación gráfica (Esquina superior izquierda).
     */
    public SpriteProyectil(Punto punto) {
        super(punto, new Dimension(Tamanio.PROYECTIL_ANCHO, Tamanio.PROYECTIL_ALTO), "/Imagenes/Partida/proyectil.png");
    }
}
