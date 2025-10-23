package Utilidades;

/**
 * Dimension horizontal y vertical de un objeto medida en píxeles.
 * No es modificable.
 */
public class Dimension {
    private int ancho;
    private int alto;

    /**
     * Constructor.
     * @param ancho Ancho en píxeles del objeto.
     * @param alto Alto en píxles del objeto.
     */
    public Dimension(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
    }

    /**
     * Devuelve el ancho del objeto en píxeles.
     * @return Ancho del objeto en píxeles.
     */
    public int getAncho() {return ancho;}

    /**
     * Devuelve el alto del objeto en píxeles.
     * @return Alto del objeto en píxeles.
     */
    public int getAlto() {return alto;}
}
