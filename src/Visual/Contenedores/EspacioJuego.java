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
import java.util.HashMap;
import java.util.Map;

public class EspacioJuego extends JPanel implements Observador {
    Controlador controlador;
    Map<Integer, Sprite> sprites;

    /**
     * Constructor. Crea un espacio en donde se renderizará Space Invaders.
     * @param controlador Controlador que administrará al EspacioJuego.
     */
    public EspacioJuego(Controlador controlador) {
        this.controlador = controlador;

        // Nota
        // permite que el panel reciba foco de teclado
        this.setFocusable(true);

        configurarListener();

        this.sprites = new HashMap<Integer, Sprite>();
        configurarEspacioJuego();
    }

    // Pide el foco cuando el panel se hace visible. Sin esto no podía hacer andar el input del teclado.
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
                        // Si querés detener la lógica del juego (timers) antes de cerrar,
                        // añade un método en el controlador, por ejemplo controlador.detenerJuego();
                        // y descomenta la siguiente línea:
                        // if (controlador != null) controlador.detenerJuego();

                        window.dispose();
                    }
                }
            }
        });
    }

    /**
     * Actualiza la posición del objeto pasado según los paráemtros.
     * @param id ID del objeto a cambiar.
     * @param punto Posición en el espacio del objeto a cambiar.
     * @param dimension Ancho y alto del objeto a cambiar.
     * @param tipo Tipo de objeto a cambiar.
     * @param inactivo Si el objeto está inactivo o no.
     */
    @Override
    public void actualizarPosiciones(int id, Punto punto, Dimension dimension, TiposEntidades tipo, boolean inactivo) {
        if (sprites.containsKey(id)) {
            sprites.get(id).setBounds(punto.getPosicionX(), punto.getPosicionY(), dimension.getAncho(), dimension.getAlto());
            if (inactivo) {
                sprites.get(id).setVisible(false);
            }
            sprites.get(id).repaint();
        } else {
            Sprite nuevoSprite = null;
            switch (tipo) {
                case NAVE -> nuevoSprite = new SpriteNave(punto);
                case PROYECTIL -> nuevoSprite = new SpriteProyectil(punto);
                case MURO -> nuevoSprite = new SpriteMuro(punto);
                case BATERIA -> nuevoSprite = new SpriteBateria(punto);
                case BARRA -> nuevoSprite = new SpriteBarra(punto);
            }
            if (nuevoSprite != null) {
                nuevoSprite.setBounds(punto.getPosicionX(), punto.getPosicionY(), dimension.getAncho(), dimension.getAlto());
                sprites.put(id, nuevoSprite);
                this.add(nuevoSprite);
                this.revalidate();
                this.repaint();
            }
        }
    }
}