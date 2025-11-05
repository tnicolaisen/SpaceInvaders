package Modelo.Entidades;
import Utilidades.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Excepciones.SinNavesException;
import Excepciones.IndiceNaveFueraDeRangoException;

/**
 * Oleada de las Naves enemigas
 */
public class Oleada extends Entidad {
    private List<List<Nave>> matrizNaves = new ArrayList<List<Nave>>();
    private Random random;

    /**
     * Constructor.
     * @param punto Punto de aparición de la Oleada.
     */
    public Oleada(Punto punto) {
        super(punto, new Dimension(Tamanio.OLEADA_ANCHO, Tamanio.OLEADA_ALTO), TiposEntidades.NAVE);
        random = new Random();

        // Se llena la matriz de la nave, calculando sus posiciones con un margen de 20x20 pixeles entre ellas.
        for (int i = 0; i < 3; i++){
            List<Nave> fila = new ArrayList<>();
            for (int j = 0; j < 5; j++){
                Punto puntoNave = new Punto(
                        j * 70 + this.getEsquinaSuperiorIzquierda().getPosicionX(),
                        i * 70 + this.getEsquinaSuperiorIzquierda().getPosicionY()
                );
                fila.add(new Nave(puntoNave));
            }
            matrizNaves.add(fila);
        }
    }

    // -----------------------
    // Métodos
    // -----------------------

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
     * Devuelve la cantidad de naves que todavía están vivas.
     * @return Cantidad de naves que todavía están vivas.
     */
    public int getCantidadDeNavesVivas(){
        int cantidadNaves = 0;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 5; j++){
                Nave n = obtenerNave(i, j);
                if (!n.getInactivo()){
                    cantidadNaves++;
                }
            }
        }
        return cantidadNaves;
    }

    /**
     * Devuelve la lista de todas las naves en la matriz (vivas y muertas).
     * @return Lista de todas las naves de la partida.
     */
    public List<Nave> devolverNaves(){
        List<Nave> naves = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 5; j++){
                Nave n = obtenerNave(i, j);
                naves.add(n);
            }
        }
        return naves;
    }

    /**
     * Hace que se genere un nuevo Proyectil desde una Nave aleatoria del borde inferior de la Oleada.
     * Si no hay naves activas en la fila inferior se lanza SinNavesException.
     * @return Proyectil disparado.
     */
    public Proyectil dispararNaveAleatoria(){
        List<Nave> filaInferior = new ArrayList<>();
        // construir la lista de candidatos desde la fila 2 (índice 2)
        for (int col = 0; col < 5; col++) {
            Nave n = obtenerNave(2, col); // puede lanzar IndiceNaveFueraDeRangoException si estructura dañada
            if (!n.getInactivo()) filaInferior.add(n);
        }
        if (filaInferior.isEmpty()) {
            throw new SinNavesException("No hay naves activas en la fila inferior para disparar.");
        }
        Nave naveAleatoria = filaInferior.get(random.nextInt(filaInferior.size()));
        return naveAleatoria.disparar();
    }

    /**
     * Permite que se actualicen las posiciones de las Naves según la posición de la Oleada.
     */
    public void actualizarPosicionNaves(){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 5; j++){
                Nave n = obtenerNave(i, j);
                n.setPunto(
                        new Punto(
                                j * 70 + this.getEsquinaSuperiorIzquierda().getPosicionX(),
                                i * 70 + this.getEsquinaSuperiorIzquierda().getPosicionY()
                        )
                );
            }
        }
    }
}