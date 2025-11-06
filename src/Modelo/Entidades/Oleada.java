package Modelo.Entidades;
import Utilidades.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Excepciones.SinNavesException;
import Excepciones.IndiceNaveFueraDeRangoException;

/**
 * Oleada de las Naves enemigas.
 * Mantengo una "caja" dinámica que refleja las filas/columnas con naves activas.
 */
public class Oleada extends Entidad {
    private List<List<Nave>> matrizNaves = new ArrayList<List<Nave>>();
    private Random random;

    // Índices de la caja activa. Si no hay naves activas, maxCol < minCol.
    private int minFil = 0;
    private int maxFil = 2;
    private int minCol = 0;
    private int maxCol = 4;

    // Espaciado entre naves en píxeles
    private static final int ESPACIO = 20;
    // Ancho/alto de cada nave
    private static final int NAVE_ANCHO = Tamanio.NAVE_ANCHO;
    private static final int NAVE_ALTO = Tamanio.NAVE_ALTO;

    /**
     * Constructor.
     * @param punto Punto de aparición de la Oleada.
     */
    public Oleada(Punto punto) {
        super(punto, new Dimension(Tamanio.OLEADA_ANCHO, Tamanio.OLEADA_ALTO), TiposEntidades.NAVE);
        random = new Random();

        // Creo la matriz inicial de naves (3 filas x 5 columnas)
        for (int i = 0; i < 3; i++){
            List<Nave> fila = new ArrayList<>();
            for (int j = 0; j < 5; j++){
                Punto puntoNave = new Punto(
                        j * (NAVE_ANCHO + ESPACIO) + this.getEsquinaSuperiorIzquierda().getPosicionX(),
                        i * (NAVE_ALTO + ESPACIO) + this.getEsquinaSuperiorIzquierda().getPosicionY()
                );
                fila.add(new Nave(puntoNave));
            }
            matrizNaves.add(fila);
        }

        // aseguro valores iniciales de la caja
        recalcularCaja();
    }

    // -------------------------------
    // Redimensionamiento de la oleada
    // -------------------------------

    /**
     * Recalculo min/max de filas y columnas que contienen al menos una nave activa.
     * - Recorro la matriz buscando la primer y ultima columna/fila con naves activas.
     */
    public void recalcularCaja() {
        int filas = matrizNaves.size();
        if (filas == 0) {
            minFil = 0; maxFil = -1;
            minCol = 0; maxCol = -1;
            return;
        }
        int cols = matrizNaves.get(0).size();

        int minimaFila = Integer.MAX_VALUE;
        int maximaFila = Integer.MIN_VALUE;
        int minimaColumna = Integer.MAX_VALUE;
        int maximaColumna = Integer.MIN_VALUE;

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                Nave n = matrizNaves.get(i).get(j);
                if (n != null && !n.getInactivo()) {
                    if (i < minimaFila) minimaFila = i;
                    if (i > maximaFila) maximaFila = i;
                    if (j < minimaColumna) minimaColumna = j;
                    if (j > maximaColumna) maximaColumna = j;
                }
            }
        }

        if (maximaFila == Integer.MIN_VALUE) {
            // no encontré naves activas
            minFil = 0; maxFil = -1;
            minCol = 0; maxCol = -1;
        } else {
            minFil = minimaFila;
            maxFil = maximaFila;
            minCol = minimaColumna;
            maxCol = maximaColumna;
        }
    }

    /**
     * Obtiene una nave validando índices. Lanza IndiceNaveFueraDeRangoException
     * si fila/columna están fuera del rango actual de la matriz.
     */
    private Nave obtenerNave(int fila, int columna) {
        if (fila < 0 || fila >= matrizNaves.size()) {
            throw new IndiceNaveFueraDeRangoException("Fila fuera de rango: " + fila);
        }
        List<Nave> filaLista = matrizNaves.get(fila);
        if (columna < 0 || columna >= filaLista.size()) {
            throw new IndiceNaveFueraDeRangoException("Columna fuera de rango: " + columna + " en fila " + fila);
        }
        return filaLista.get(columna);
    }

    /**
     * Devuelvo la cantidad de naves que todavía están vivas.
     * @return Cantidad de naves que todavía están vivas.
     */
    public int getCantidadDeNavesVivas(){
        int cantidadNaves = 0;
        for (int i = 0; i < matrizNaves.size(); i++){
            for (int j = 0; j < matrizNaves.get(i).size(); j++){
                Nave n = obtenerNave(i, j);
                if (!n.getInactivo()){
                    cantidadNaves++;
                }
            }
        }
        return cantidadNaves;
    }

    /**
     * Devuelvo la lista de todas las naves en la matriz (vivas y muertas).
     * @return Lista de todas las naves de la partida.
     */
    public List<Nave> devolverNaves(){
        List<Nave> naves = new ArrayList<>();
        for (int i = 0; i < matrizNaves.size(); i++){
            for (int j = 0; j < matrizNaves.get(i).size(); j++){
                Nave n = obtenerNave(i, j);
                naves.add(n);
            }
        }
        return naves;
    }

    /**
     * Hago que se genere un nuevo Proyectil desde una Nave aleatoria del borde inferior de la Oleada.
     * Si no hay naves activas en la fila inferior se lanza SinNavesException.
     * @return Proyectil disparado.
     */
    public Proyectil dispararNaveAleatoria(){
        List<Nave> filaInferior = new ArrayList<>();
        int filas = matrizNaves.size();
        int cols = matrizNaves.get(0).size();
        // busco en la última fila lógica (fila que contiene naves por índice)
        for (int col = 0; col < cols; col++) {
            Nave n = obtenerNave(filas - 1, col);
            if (!n.getInactivo()) filaInferior.add(n);
        }
        if (filaInferior.isEmpty()) {
            throw new SinNavesException("No hay naves activas en la fila inferior para disparar.");
        }
        Nave naveAleatoria = filaInferior.get(random.nextInt(filaInferior.size()));
        return naveAleatoria.disparar();
    }

    /**
     * Actualizo las posiciones de las Naves según la posición de la Oleada.
     * Coloco cada nave relativa a la caja actual (uso offsets basados en minRow/minCol).
     */
    public void actualizarPosicionNaves(){
        // si la caja está vacía no hago nada
        if (maxCol < minCol || maxFil < minFil) return;

        int filas = matrizNaves.size();
        int cols = matrizNaves.get(0).size();

        // Posición superior izquierda de la caja activa (no la de toda la matriz)
        int cajaX = this.getEsquinaSuperiorIzquierda().getPosicionX();
        int cajaY = this.getEsquinaSuperiorIzquierda().getPosicionY();

        for (int i = 0; i < filas; i++){
            for (int j = 0; j < cols; j++){
                Nave n = obtenerNave(i, j);
                // calculo offsets relativos a la caja
                int offsetFila = i - minFil;
                int offsetCol = j - minCol;
                // coloco la nave en su posición relativa dentro de la caja
                n.setPunto(new Punto(
                        cajaX + offsetCol * (NAVE_ANCHO + ESPACIO),
                        cajaY + offsetFila * (NAVE_ALTO + ESPACIO)
                ));
            }
        }
    }

    // --------------------
    // Caja / bounding helpers (polimórficos)
    // --------------------

    /**
     * Devuelvo la dimensión actual de la caja (ancho/alto) en píxeles.
     * Si la caja está vacía devuelvo 0x0.
     */
    @Override
    public Dimension getDimension() {
        if (maxCol < minCol || maxFil < minFil) {
            return new Dimension(0, 0);
        }
        int columnas = maxCol - minCol + 1;
        int filas = maxFil - minFil + 1;
        int ancho = columnas * NAVE_ANCHO + Math.max(0, columnas - 1) * ESPACIO;
        int alto = filas * NAVE_ALTO + Math.max(0, filas - 1) * ESPACIO;
        return new Dimension(ancho, alto);
    }

    /**
     * Devuelvo la esquina superior izquierda de la caja activa.
     * Calculo a partir de la esquina superior izquierda del Entidad (matriz original) más el desplazamiento de columnas/filas.
     */
    @Override
    public Punto getEsquinaSuperiorIzquierda() {
        // calculo la esquina superior izquierda de la caja activa en píxeles
        int xOffset = minCol * (NAVE_ANCHO + ESPACIO);
        int yOffset = minFil * (NAVE_ALTO + ESPACIO);
        Punto base = super.getEsquinaSuperiorIzquierda();
        return new Punto(base.getPosicionX() + xOffset, base.getPosicionY() + yOffset);
    }

    @Override
    public Punto getEsquinaSuperiorDerecha() {
        Punto sup = getEsquinaSuperiorIzquierda();
        return new Punto(sup.getPosicionX() + getDimension().getAncho(), sup.getPosicionY());
    }

    @Override
    public Punto getEsquinaInferiorIzquierda() {
        Punto sup = getEsquinaSuperiorIzquierda();
        return new Punto(sup.getPosicionX(), sup.getPosicionY() + getDimension().getAlto());
    }

    @Override
    public Punto getEsquinaInferiorDerecha() {
        Punto sup = getEsquinaSuperiorIzquierda();
        return new Punto(sup.getPosicionX() + getDimension().getAncho(), sup.getPosicionY() + getDimension().getAlto());
    }
}