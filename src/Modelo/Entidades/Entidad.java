package Modelo.Entidades;
import Utilidades.Punto;
import Utilidades.Dimension;
import Utilidades.TiposEntidades;

/**
 * Entidad existente en un Area.
 */
public abstract class Entidad {

    // NOTA:
    // Aquí a propósito el estado está privado y no protegido para evitar errores al manipularlo, usando
    // sólo los métodos de posicionamiento y movimiento.
    private TiposEntidades tipo;
    private Punto punto;
    private Dimension dimension;
    private boolean inactivo;

    /**
     * Constructor.
     * @param punto Posición en el Area de la Entidad.
     * @param dimension Tamaño de la superficie de la Entidad.
     */
    public Entidad(Punto punto, Dimension dimension, TiposEntidades tipo) {
        this.punto = punto;
        this.dimension = dimension;
        this.tipo = tipo;
        this.inactivo = false;
    }

    /**
     * Devuelve el tipo de la entidad.
     * @return Tipo de la entidad.
     */
    public TiposEntidades getTipoEntidad(){return tipo;}

    public Dimension getDimension(){return dimension;}

    public Punto getPunto(){return punto;}

    public boolean getInactivo(){return inactivo;}

    protected void setInactivo(boolean inactivo){this.inactivo = inactivo;}

    protected void setPunto(Punto punto){this.punto = punto;}

    // --------------------
    // Posicionamiento
    // --------------------

    // Esquinas

    /**
     * Devuelve el Punto de la posición de la esquina superior izquierda de la Entidad.
     * @return Punto de la posición de la esquina superior izquierda.
     */
    public Punto getEsquinaSuperiorIzquierda() {return new Punto(this.punto.getPosicionX(), this.punto.getPosicionY());}

    /**
     * Devuelve el Punto de la posición de la esquina superior derecha de la Entidad.
     * @return Punto de la posición de la esquina superior derecha.
     */
    public Punto getEsquinaSuperiorDerecha() {return new Punto(this.punto.getPosicionX() + dimension.getAncho(), this.punto.getPosicionY());}

    /**
     * Devuelve el Punto de la posición de la esquina inferior izquierda de la Entidad.
     * @return Punto de la posición de la esquina inferior izquierda.
     */
    public Punto getEsquinaInferiorIzquierda() {return new Punto(this.punto.getPosicionX(), this.punto.getPosicionY() + dimension.getAlto());}

    /**
     * Devuelve el Punto de la posición de la esquina inferior derecha de la Entidad.
     * @return Punto de la posición de la esquina inferior derecha.
     */
    public Punto getEsquinaInferiorDerecha() {return new Punto(this.punto.getPosicionX() + dimension.getAncho(), this.punto.getPosicionY() + dimension.getAlto());}

    // Bordes

    /**
     * Devuelve la coordenada en Y del borde superior del objeto.
     * @return Coordenada en Y del borde superior del objeto.
     */
    public int getBordeArriba(){return this.punto.getPosicionY();}

    /**
     * Devuelve la coordenada en Y del borde inferior del objeto.
     * @return Coordenada en Y del borde inferior del objeto.
     */
    public int getBordeAbajo(){return this.punto.getPosicionY() + dimension.getAlto();}

    /**
     * Devuelve la coordenada en X del borde derecho del objeto.
     * @return Coordenada en X del borde derecho del objeto.
     */
    public int getBordeDerecha(){return this.punto.getPosicionX() + dimension.getAncho();}

    /**
     * Devuelve la coordenada en X del borde izquierdo del objeto.
     * @return Coordenada en X del borde izquierdo del objeto.
     */
    public int getBordeIzquierda(){return this.punto.getPosicionX();}

    // --------------------
    // Movimiento
    // --------------------

    /**
     * Mueve el elemento hacia la izquierda según la distancia en píxeles especificada.
     * @param pixeles Distancia en píxeles.
     */
    public void moverseIzquierda(int pixeles) {this.punto.moverPosicionX(pixeles * (-1));}

    /**
     * Mueve el elemento hacia la derecha según la distancia en píxeles especificada.
     * @param pixeles Distancia en píxeles.
     */
    public void moverseDerecha(int pixeles) {this.punto.moverPosicionX(pixeles);}

    /**
     * Mueve el elemento hacia arriba según la distancia en píxeles especificada.
     * @param pixeles Distancia en píxeles.
     */
    public void moverseArriba(int pixeles) {this.punto.moverPosicionY(pixeles * (-1));}

    /**
     * Mueve el elemento hacia abajo según la distancia en píxeles especificada.
     * @param pixeles Distancia en píxeles.
     */
    public void moverseAbajo(int pixeles) {this.punto.moverPosicionY(pixeles);}

    // --------------------
    // Colisión
    // --------------------

    /**
     * Verifica si la Entidad solicitada colisiona con esta Entidad.
     * @param entidad Entidad a la que evaluar con esta Entidad.
     * @return Valor booleano que verifica si ambas Entidades colisionan.
     */
    public boolean colisionoCon(Entidad entidad){
        // No colisionan si uno está completamente a la izquierda/derecha/arriba/abajo del otro.
        if (this.getBordeDerecha() <= entidad.getBordeIzquierda()) return false;
        if (this.getBordeIzquierda() >= entidad.getBordeDerecha()) return false;
        if (this.getBordeAbajo() <= entidad.getBordeArriba()) return false;
        if (this.getBordeArriba() >= entidad.getBordeAbajo()) return false;

        // En cualquier otro caso, las entidades se colisionan
        return true;
    }
}
