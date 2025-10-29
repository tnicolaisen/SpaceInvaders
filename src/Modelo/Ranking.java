package Modelo;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Gestión sencilla del ranking en memoria.
 */
public class Ranking {

    private static final List<Jugador> jugadores = new ArrayList<Jugador>();

    /**
     * Agrega un jugador al ranking y ordena el listado por puntaje descendente.
     * @param jugador Jugador a agregar.
     */
    public static void agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
        jugadores.sort(new Comparator<Jugador>() {
            @Override
            public int compare(Jugador a, Jugador b) {
                return Integer.compare(b.getPuntaje(), a.getPuntaje());
            }
        });
    }

    /**
     * Devuelve las primeras n posiciones del ranking.
     * @param cantidad Cantidad de posiciones a devolver.
     * @return Lista de jugadores.
     */
    public static List<Jugador> obtenerTop(int cantidad) {
        List<Jugador> resultado = new ArrayList<Jugador>();
        int max = Math.min(cantidad, jugadores.size());
        for (int i = 0; i < max; i++) resultado.add(jugadores.get(i));
        return resultado;
    }
}