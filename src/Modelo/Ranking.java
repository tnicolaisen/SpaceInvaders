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
     * Agrega un jugador al ranking o actualiza su puntaje si ya existe (por nombre).
     * Ordena el listado por puntaje descendente.
     */
    public static void agregarJugador(Jugador jugador) {
        if (jugador == null) return;
        Jugador existente = buscarPorNombre(jugador.getNombre());
        if (existente != null) {
            existente.sumarPuntos(jugador.getPuntaje());
        } else {
            jugadores.add(jugador);
        }
        jugadores.sort(new Comparator<Jugador>() {
            @Override
            public int compare(Jugador a, Jugador b) {
                return Integer.compare(b.getPuntaje(), a.getPuntaje());
            }
        });
    }

    public static Jugador buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        for (Jugador j : jugadores) {
            if (nombre.equalsIgnoreCase(j.getNombre())) return j;
        }
        return null;
    }

    public static List<Jugador> obtenerTop(int cantidad) {
        List<Jugador> resultado = new ArrayList<Jugador>();
        int max = Math.min(cantidad, jugadores.size());
        for (int i = 0; i < max; i++) resultado.add(jugadores.get(i));
        return resultado;
    }

    public static List<Jugador> obtenerTodos() {
        return new ArrayList<Jugador>(jugadores);
    }
}