package Utilidades;

/**
 * Viewer que contiene datos del estado del menú. Contiene atributos públicos.
 * Pasa información entre la vista y otras capas sin acoplar a Swing.
 */
public class MenuViewer {
    // Créditos disponibles en el menú
    public int creditos = 0;

    // Indica si la ventana del juego está abierta
    public boolean ventanaJuegoAbierta = false;

    // Constructor
    public MenuViewer() { }
}