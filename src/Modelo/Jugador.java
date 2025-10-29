package Modelo;

/**
 * Objeto que representa a el usuario. Posee un puntaje y un nombre (de tres letras)
 */
public class Jugador {
    private static int idJugador = 0;
    private String nombre;
    private int puntaje;

    public Jugador(String nombre) {
        idJugador = idJugador++;
        this.nombre = nombre;
        puntaje = 0;
    }

    // -------------- Getters

    public String getNombre() {return nombre;}
    public int getPuntaje() {return puntaje;}

    // -------------- Métodos

    /**
     * Se suma la cantidad de puntos pasada por parámetro al puntaje del jugador.
     * @param cantidad Cantidad de puntos a sumar.
     */
    public void sumarPuntos(int cantidad){puntaje += cantidad;}
}
