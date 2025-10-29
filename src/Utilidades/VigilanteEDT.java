package Utilidades;
import Controlador.Controlador;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 * Vigilante del Event Dispatch Thread (EDT).
 * Detecta cuando el EDT deja de ejecutar Runnables y solicita una recuperación.
 */
public class VigilanteEDT implements Runnable {
    private final Controlador controlador;
    private volatile long ultimoLatido = 0;
    private volatile boolean ejecutando = true;

    /**
     * Constructor.
     * @param controlador Controlador para invocar acciones de recuperación.
     */
    public VigilanteEDT(Controlador controlador) {
        this.controlador = controlador;
    }

    /**
     * Inicia el vigilante en un hilo separado.
     */
    public void iniciar() {
        Thread hilo = new Thread(this);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Detiene el vigilante.
     */
    public void detener() {
        ejecutando = false;
    }

    /**
     * Loop principal del vigilante.
     */
    public void run() {
        while (ejecutando) {
            final long ahora = System.currentTimeMillis();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ultimoLatido = ahora;
                }
            });

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {}

            long ahora2 = System.currentTimeMillis();
            if (ahora2 - ultimoLatido > 2000) {
                System.out.println("VigilanteEDT: EDT no responde, volcando estado de hilos...");
                volcarEstadoHilos();
                System.out.println("VigilanteEDT: solicitando detener la lógica del juego y cerrar ventana.");
                controlador.detenerJuego();
                controlador.cerrarVentanaDelJuego();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}

                long ahora3 = System.currentTimeMillis();
                if (ahora3 - ultimoLatido > 2000) {
                    System.out.println("VigilanteEDT: EDT sigue sin responder tras intento de recuperación. Se volcará estado de hilos final y se saldrá.");
                    volcarEstadoHilos();
                    System.exit(1);
                } else {
                    System.out.println("VigilanteEDT: EDT recuperado tras intento de recuperación.");
                }
            }
        }
    }

    private void volcarEstadoHilos() {
        ThreadMXBean threadMx = ManagementFactory.getThreadMXBean();
        long[] deadlocked = threadMx.findDeadlockedThreads();
        if (deadlocked != null && deadlocked.length > 0) {
            System.out.println("VigilanteEDT: DEADLOCK detectado en hilos:");
            for (long id : deadlocked) {
                ThreadInfo ti = threadMx.getThreadInfo(id, Integer.MAX_VALUE);
                System.out.println(ti.toString());
            }
        } else {
            System.out.println("VigilanteEDT: no se detectaron deadlocks.");
        }

        Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> e : all.entrySet()) {
            Thread t = e.getKey();
            StackTraceElement[] stack = e.getValue();
            System.out.println("----- HILO: " + t.getName() + " (id=" + t.getId() + ", daemon=" + t.isDaemon() + ", state=" + t.getState() + ") -----");
            if (stack != null) {
                for (StackTraceElement st : stack) {
                    System.out.println("    at " + st.toString());
                }
            }
        }
        System.out.println("----- fin volcado hilos -----");
    }
}