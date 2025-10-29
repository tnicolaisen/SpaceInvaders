package Visual.Contenedores;

import Controlador.Controlador;
import Modelo.Interfaces.Observador;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.TiposEntidades;
import Visual.Sprites.*;
import Modelo.EstadoPartida;
import Modelo.Jugador;
import Modelo.Ranking;
import Visual.Ventanas.VentanaFinPartida;

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
 * actualizaciones del modelo.
 */
public class EspacioJuego extends JPanel implements Observador {
    Controlador controlador;
    Map<Integer, Sprite> sprites;

    private final List<Actualizacion> colaActualizaciones;
    private boolean procesando;

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

    private void configurarEspacioJuego() {
        this.setBackground(Color.BLACK);
        this.setLayout(null);
    }

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

    @Override
    public void partidaFinalizada(EstadoPartida estado, int puntaje) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> partidaFinalizada(estado, puntaje));
            return;
        }

        String mensaje = estado == EstadoPartida.GANADA ? "Ganaste" : "Perdiste";
        int creditosGanados = (estado == EstadoPartida.GANADA) ? 1 : 0;

        String nombre = VentanaFinPartida.mostrarDialogo(this, mensaje, puntaje, creditosGanados);
        if (nombre == null) return;

        Jugador jugador = new Jugador(nombre);
        jugador.sumarPuntos(puntaje);

        Ranking.agregarJugador(jugador);

        if (estado == EstadoPartida.GANADA) {
            if (controlador != null) controlador.cargarCreditos(creditosGanados);
        }

        List<Jugador> top = Ranking.obtenerTop(10);
        System.out.println("Ranking actualizado:");
        for (Jugador j : top) {
            System.out.println(j.getNombre() + " " + j.getPuntaje());
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
    }

    private void procesarCola() {
        for (;;) {
            Actualizacion actualizacion;
            synchronized (colaActualizaciones) {
                if (colaActualizaciones.isEmpty()) {
                    procesando = false;
                    break;
                }
                actualizacion = colaActualizaciones.remove(0);
            }

            if (actualizacion.accion == Accion.LIMPIAR) {
                for (Sprite s : new java.util.ArrayList<>(sprites.values())) {
                    this.remove(s);
                }
                sprites.clear();
                this.revalidate();
                this.repaint();
                continue;
            }

            if (actualizacion.accion == Accion.ELIMINAR) {
                Sprite s = sprites.remove(actualizacion.id);
                if (s != null) {
                    this.remove(s);
                    this.revalidate();
                    this.repaint();
                }
                continue;
            }

            if (actualizacion.accion == Accion.ACTUALIZAR) {
                if (sprites.containsKey(actualizacion.id)) {
                    Sprite s = sprites.get(actualizacion.id);
                    s.setBounds(actualizacion.punto.getPosicionX(), actualizacion.punto.getPosicionY(), actualizacion.dimension.getAncho(), actualizacion.dimension.getAlto());
                    if (actualizacion.inactivo) s.setVisible(false); else s.setVisible(true);
                    s.repaint();
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