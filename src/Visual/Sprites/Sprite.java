package Visual.Sprites;
import Modelo.Interfaces.Observador;
import Utilidades.Dimension;
import Utilidades.Punto;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

/**
 * Representación gráfica en pantalla de una entidad.
 */
public abstract class Sprite extends JLabel{
    private Image imagen;
    private Dimension dimension;

    /**
     * Constructor
     * @param punto Punto de dibujado del elemento. Esquina superior izquierda.
     * @param dimension Acho y alto del elemento renderizado.
     * @param rutaImagen Imágen representativa del elemento.
     */
    public Sprite(Punto punto, Dimension dimension, String rutaImagen) {
        this.dimension = dimension;
        this.setBounds(
                punto.getPosicionX(),
                punto.getPosicionY(),
                dimension.getAncho(),
                dimension.getAlto()
        );
        URL url = getClass().getResource(rutaImagen);
        this.imagen = new ImageIcon(url).getImage();
        this.setIcon(new ImageIcon(imagen));
    }

    /**
     * Hace a los elementos invisibles. Usar para cuando se mueran las naves / destruyan los muros
     */
    public void hacerInvisible(){this.setVisible(false);}
}
