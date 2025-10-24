package Modelo.Entidades;
import Utilidades.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
     * Devuelve la cantidad de naves que todavía están vivas.
     * @return Cantidad de naves que todavía están vivas.
     */
    public int getCantidadDeNavesVivas(){
        int cantidadNaves = 0;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 5; j++){
                if (!matrizNaves.get(i).get(j).getInactivo()){
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
                naves.add(matrizNaves.get(i).get(j));
            }
        }
        return naves;
    }

    public Proyectil dispararNaveAleatoria(){
        Nave naveAleatoria = matrizNaves.get(2).get(random.nextInt(4));
        while (naveAleatoria.getInactivo()){
            naveAleatoria = matrizNaves.get(2).get(random.nextInt(4));
        }
        return naveAleatoria.disparar();
    }

    public void actualizarPosicionNaves(){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 5; j++){
                matrizNaves.get(i).get(j).setPunto(
                        new Punto(
                                j * 70 + this.getEsquinaSuperiorIzquierda().getPosicionX(),
                                i * 70 + this.getEsquinaSuperiorIzquierda().getPosicionY()
                        )
                );
            }
        }
    }
}