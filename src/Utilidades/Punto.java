package Utilidades;

/**
 * Posición de un objeto en un espacio medido en píxeles.
 */
public class Punto {

    private int posicionX;
    private int posicionY;

    /**
     * Constructor
     * @param posicionX Posición horizontal del objeto en píxeles.
     * @param posicionY Posición vertical del objeto en píxeles.
     */
    public Punto(int posicionX, int posicionY) {
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }

    // -----------------------
    // Getters
    // -----------------------

    /**
     * Devuelve la posición horizontal del elemento en píxeles
     * @return Posición horizontal en píxeles.
     */
    public int getPosicionX() {return posicionX;}

    /**
     * Devuelve la posición vertical del elemento en píxeles.
     * @return Posición vertical en píxeles.
     */
    public int getPosicionY() {return posicionY;}

    // -----------------------
    // Setters
    // -----------------------

    /**
     * Cambia la posición horizontal del elemento según el multiplicador.
     * @param  multiplicador Cantidad de píxeles que se mueve horizontalmente.
     *                       Si > 0 se moverá a la derecha.
     *                       Si < 0 se moverá a la izquierda.
     */
    public void moverPosicionX(int multiplicador) {this.posicionX += multiplicador;}

    /**
     * Cambia la posición vertical del elemento según el multiplicador.
     * @param  multiplicador Cantidad de píxeles que se mueve verticalmente.
     *                       Si > 0 se moverá abajo.
     *                       Si < 0 se moverá arriba.
     */
    public void moverPosicionY(int multiplicador) {this.posicionY += multiplicador;}

    @Override
    public String toString() {return posicionX + "," + posicionY;}
}
