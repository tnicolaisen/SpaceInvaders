package Visual.Contenedores;
import Controlador.Controlador;
import Modelo.Interfaces.Observador;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.TiposEntidades;
import Visual.Sprites.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel donde se renderiza la vista del juego. Implementa Observador para recibir
 * actualizaciones del modelo. Las actualizaciones se encolan y se procesan en lote
 * en el Event Dispatch Thread para evitar saturar la cola del EDT.
 */
public class EspacioJuego extends JPanel implements Observador {
    Controlador controlador;
    Map<Integer, Sprite> sprites;

    private final List<Actualizacion> colaActualizaciones;
    private boolean procesando;

    /**
     * Constructor. Crea un espacio en donde se renderizará Space Invaders.
     * @param controlador Controlador que administrará al EspacioJuego.
     */
    public EspacioJuego(Controlador controlador) {
        this.controlador = controlador;
        this.setFocusable(true);
        this.colaActualizaciones = new ArrayList<Actualizacion>();
        this.procesando = false;
        configurarListener();
        this.sprites = new HashMap<Integer, Sprite>();
        configurarEspacioJuego();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.requestFocusInWindow();
    }

    /**
     * Configura el fondo y el layout del JPanel.
     */
    private void configurarEspacioJuego() {
        this.setBackground(Color.BLACK);
        this.setLayout(null);
    }

    /**
     * Genera los Listeners que permite al usuario interactuar con el juego.
     */
    private void configurarListener(){
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    controlador.moverseDerecha();
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    controlador.moverseIzquierda();
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    controlador.disparar();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    Window window = SwingUtilities.getWindowAncestor(EspacioJuego.this);
                    if (window != null) {
                        if (controlador != null) {
                            try {
                                Method detener = controlador.getClass().getMethod("detenerJuego");
                                if (detener != null) {
                                    detener.invoke(controlador);
                                }
                            } catch (Exception ignored) {}
                        }
                        window.dispose();
                    }
                }
            }
        });
    }

    /**
     * Limpia todos los sprites de la vista. Se encola la acción y se procesa en el EDT.
     */
    public void limpiarSprites() {
        Actualizacion actualizacion = new Actualizacion();
        actualizacion.accion = Accion.LIMPIAR;
        synchronized (colaActualizaciones) {
            colaActualizaciones.add(actualizacion);
            if (!procesando) {
                procesando = true;
                SwingUtilities.invokeLater(this::procesarCola);
            }
        }
    }

    /**
     * Indica a la vista que elimine el sprite asociado al id (por ejemplo cuando la entidad
     * fue removida del modelo). Esta llamada encola la eliminación y la procesa en el EDT.
     * @param id ID de la entidad eliminada.
     */
    @Override
    public void eliminarEntidad(int id) {
        Actualizacion actualizacion = new Actualizacion();
        actualizacion.accion = Accion.ELIMINAR;
        actualizacion.id = id;
        synchronized (colaActualizaciones) {
            colaActualizaciones.add(actualizacion);
            if (!procesando) {
                procesando = true;
                SwingUtilities.invokeLater(this::procesarCola);
            }
        }
    }

    /**
     * Recibe la actualización de posición/dimensión/tipo/inactivo del modelo.
     * Encola la actualización para su procesamiento en lote en el EDT.
     * @param id ID del objeto a cambiar.
     * @param punto Posición en el espacio del objeto a cambiar.
     * @param dimension Ancho y alto del objeto a cambiar.
     * @param tipo Tipo de objeto a cambiar.
     * @param inactivo Si el objeto está inactivo o no.
     */
    @Override
    public void actualizarPosiciones(int id, Punto punto, Dimension dimension, TiposEntidades tipo, boolean inactivo) {
        Actualizacion actualizacion = new Actualizacion();
        actualizacion.accion = Accion.ACTUALIZAR;
        actualizacion.id = id;
        actualizacion.punto = punto;
        actualizacion.dimension = dimension;
        actualizacion.tipo = tipo;
        actualizacion.inactivo = inactivo;
        synchronized (colaActualizaciones) {
            colaActualizaciones.add(actualizacion);
            if (!procesando) {
                procesando = true;
                SwingUtilities.invokeLater(this::procesarCola);
            }
        }
    }

    /**
     * Procesa la cola de actualizaciones en el EDT aplicando los cambios visuales
     * (ADD/UPDATE/REMOVE/CLEAR) sobre los sprites del panel.
     */
    private void procesarCola() {
        while (true) {
            Actualizacion actualizacion;
            synchronized (colaActualizaciones) {
                if (colaActualizaciones.isEmpty()) {
                    procesando = false;
                    break;
                }
                actualizacion = colaActualizaciones.remove(0);
            }

            if (actualizacion.accion == Accion.LIMPIAR) {
                for (Sprite sprite : new java.util.ArrayList<>(sprites.values())) {
                    this.remove(sprite);
                }
                sprites.clear();
                this.revalidate();
                this.repaint();
                continue;
            }

            if (actualizacion.accion == Accion.ELIMINAR) {
                Sprite sprite = sprites.remove(actualizacion.id);
                if (sprite != null) {
                    this.remove(sprite);
                    this.revalidate();
                    this.repaint();
                }
                continue;
            }

            if (actualizacion.accion == Accion.ACTUALIZAR) {
                if (sprites.containsKey(actualizacion.id)) {
                    Sprite sprite = sprites.get(actualizacion.id);
                    sprite.setBounds(actualizacion.punto.getPosicionX(), actualizacion.punto.getPosicionY(), actualizacion.dimension.getAncho(), actualizacion.dimension.getAlto());
                    if (actualizacion.inactivo) sprite.setVisible(false); else sprite.setVisible(true);
                    sprite.repaint();
                } else {
                    Sprite nuevoSprite = null;
                    switch (actualizacion.tipo) {
                        case NAVE -> nuevoSprite = new SpriteNave(actualizacion.punto);
                        case PROYECTIL -> nuevoSprite = new SpriteProyectil(actualizacion.punto);
                        case MURO -> nuevoSprite = new SpriteMuro(actualizacion.punto);
                        case BATERIA -> nuevoSprite = new SpriteBateria(actualizacion.punto);
                        case BARRA -> nuevoSprite = new SpriteBarra(actualizacion.punto);
                    }
                    if (nuevoSprite != null) {
                        nuevoSprite.setBounds(actualizacion.punto.getPosicionX(), actualizacion.punto.getPosicionY(), actualizacion.dimension.getAncho(), actualizacion.dimension.getAlto());
                        sprites.put(actualizacion.id, nuevoSprite);
                        this.add(nuevoSprite);
                        this.revalidate();
                        this.repaint();
                    }
                }
            }
        }
    }

    private static class Actualizacion {
        Accion accion;
        int id;
        Punto punto;
        Dimension dimension;
        TiposEntidades tipo;
        boolean inactivo;
    }

    private enum Accion { ACTUALIZAR, ELIMINAR, LIMPIAR }
}