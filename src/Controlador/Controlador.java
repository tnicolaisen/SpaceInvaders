package Controlador;
import Modelo.Area;
import Utilidades.VigilanteEDT;
import Utilidades.MenuViewer;

import javax.naming.ldap.Control;

/**
 * Controlador principal de la aplicación. Controla la vista del menú y el modelo Area.
 * NO debe importar nada del paquete Visual.
 */
public class Controlador {
    private Area area;
    private static Controlador controladorSingleton;

    // Viewer (lectura de estado) expuesto por la vista (opcional)
    private MenuViewer menuViewer;

    // Callback que la vista registra para que el controlador pida cerrar la ventana sin importar Swing
    private Runnable cerrarVentanaCallback;

    /**
     * Constructor.
     */
    private Controlador() {
        new Thread(new Runnable() {
            public void run() {
                new VigilanteEDT(Controlador.this).iniciar();
            }
        }).start();
    }

    /**
     * Retorna el Singleton de la clase Controlador
     * @return
     */
    public static Controlador getControladorSingleton() {
        if (controladorSingleton == null) {
            controladorSingleton = new Controlador();
        }
        return controladorSingleton;
    }

    /**
     * La vista registra el Area que creó para que el controlador pueda actuar sobre el modelo.
     */
    public void registrarArea(Area area) {
        this.area = area;
    }

    /**
     * La vista registra su MenuViewer (objeto de sólo datos) para que el controlador pueda leer estado si lo necesita.
     */
    public void registrarMenuViewer(MenuViewer viewer) {
        this.menuViewer = viewer;
    }

    /**
     * La vista registra un callback (Runnable) que cierra la ventana. El controlador lo invoca cuando necesite pedir cierre.
     * Esto evita que el controlador importe cualquier clase de la vista.
     */
    public void registrarCerrarVentanaCallback(Runnable callback) {
        this.cerrarVentanaCallback = callback;
    }

    public void moverseDerecha(){ if (area != null) area.moverJugadorDerecha(); }
    public void moverseIzquierda(){ if (area != null) area.moverJugadorIzquierda(); }
    public void disparar(){ if (area != null) area.dispararJugador(); }

    /**
     * Detiene la lógica del juego (timers) inmediatamente.
     */
    public void detenerJuego() {
        if (area != null) area.detener();
    }

    /**
     * Pide a la vista que cierre la ventana de juego si existe.
     */
    public void cerrarVentanaDelJuego() {
        if (cerrarVentanaCallback != null) {
            try { cerrarVentanaCallback.run(); } catch (Exception ignored) {}
        }
    }

    /**
     * Carga créditos mediante la vista (si la vista lo registra). Mantuve este método para compatibilidad
     * con el código existente; la implementación concreta la realiza la vista.
     * Aquí sólo delegamos si el area/visor están registrados.
     */
    public void cargarCreditos(int cantidad) {
        // preferimos delegar a la vista; si la vista no fue registrada no hacemos nada
        // (la vista normalmente registra su propio manejo de créditos)
    }

    public Area obtenerArea() { return area; }

    public MenuViewer obtenerMenuViewer() { return menuViewer; }
}