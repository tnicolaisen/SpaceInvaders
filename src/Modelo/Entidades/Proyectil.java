package Modelo.Entidades;
import Modelo.Interfaces.Daniable;
import Utilidades.*;

/**
 * Munición disparada por las naves y la batería.
 */
public class Proyectil extends Entidad implements Daniable {
    private int velocidad;
    private int vida;
    private Direcciones direccion;

    /**
     * Constructor.
     * @param punto Punto de aparición.
     * @param direccion Dirección en la se dispara el Proyectil.
     */
    public Proyectil(Punto punto, Direcciones direccion) {
        super(punto, new Dimension(Tamanio.PROYECTIL_ANCHO, Tamanio.PROYECTIL_ALTO), TiposEntidades.PROYECTIL);
        this.direccion = direccion;
        this.velocidad = 25;
        this.vida = 100;
    }

    /**
     * Calcula automáticamente la trayectoria del proyectil al siguiente ciclo.
     */
    public void continuarTrayectoria(){
        if (direccion == Direcciones.ARRIBA){
            this.moverseArriba(velocidad);
        } else if (direccion == Direcciones.ABAJO){
            this.moverseAbajo(velocidad);
        } else {
            throw new IllegalStateException("El proyectil sólo debe moverse hacia ARRIBA o ABAJO.");
        }
    };

    public void serDaniado(){
        vida -= 100;
        this.setInactivo(true);
    }

    @Override
    public int getVida() {return vida;}
}
