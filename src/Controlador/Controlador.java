package Controlador;
import Modelo.Area;
import Utilidades.VigilanteEDT;
import Utilidades.MenuViewer;

/**
 * Controlador principal de la aplicación.
 * Maneja la interacción entre la vista y el modelo Area.
 * NO debo importar nada del paquete Visual.
 */
public class Controlador {
    private Area area;
    private static Controlador controladorSingleton;

    // Viewer (lectura de estado) expuesto por la vista (opcional)
    private MenuViewer menuViewer;

    // Callback que la vista registra para que el controlador pida cerrar la ventana sin importar Swing
    private Runnable cerrarVentanaCallback;

    /**
     * Constructor privado.
     * Inicio el vigilante del EDT en un hilo de fondo.
     */
    private Controlador() {
        new Thread(new Runnable() {
            public void run() {
                new VigilanteEDT(Controlador.this).iniciar();
            }
        }).start();
    }

    /**
     * Retorno el singleton del controlador.
     * @return instancia única de Controlador
     */
    public static synchronized Controlador getControladorSingleton() {
        if (controladorSingleton == null) {
            controladorSingleton = new Controlador();
        }
        return controladorSingleton;
    }

    /**
     * Registro el Area que creó la vista para que pueda actuar sobre el modelo.
     */
    public void registrarArea(Area area) {
        this.area = area;
    }

    /**
     * Registro el MenuViewer (objeto de sólo datos) que expone la vista.
     */
    public void registrarMenuViewer(MenuViewer viewer) {
        this.menuViewer = viewer;
    }

    /**
     * Registro un callback (Runnable) que cierra la ventana. Lo invoco cuando necesito pedir cierre.
     */
    public void registrarCerrarVentanaCallback(Runnable callback) {
        this.cerrarVentanaCallback = callback;
    }

    // Delego acciones del jugador al Area cuando esté registrado
    public void moverseDerecha(){ if (area != null) area.moverJugadorDerecha(); }
    public void moverseIzquierda(){ if (area != null) area.moverJugadorIzquierda(); }
    public void disparar(){ if (area != null) area.dispararJugador(); }

    /**
     * Detengo la lógica del juego (timers) inmediatamente.
     */
    public void detenerJuego() {
        if (area != null) area.detener();
    }

    /**
     * Pido a la vista que cierre la ventana de juego si existe.
     */
    public void cerrarVentanaDelJuego() {
        if (cerrarVentanaCallback != null) {
            try { cerrarVentanaCallback.run(); } catch (Exception ignored) {}
        }
    }

    /**
     * Método de compatibilidad para que la vista reciba créditos.
     * (La vista implementa la lógica concreta de mostrar/actualizar créditos.)
     */
    public void cargarCreditos(int cantidad) {
        // delegación intencional: la vista lleva el manejo de UI
    }

    public Area obtenerArea() { return area; }

    public MenuViewer obtenerMenuViewer() { return menuViewer; }
}