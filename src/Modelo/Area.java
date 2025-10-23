package Modelo;
import Modelo.Entidades.*;
import Modelo.Interfaces.Observador;
import Utilidades.Dimension;
import Utilidades.Direcciones;
import Utilidades.Punto;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Área en donde se ejecuta lógicamente el juego.
 */
public class Area {
    // Dificultad
    private int dificultad;

    // Entidades
    private int contadorEntidades;
    private Map<Integer, Entidad> entidades;
    private Oleada oleada;
    private Direcciones direccionOleada;

    // Dimensiones de la ventana
    private Dimension dimension;

    // Márgenes en los cuales la Oleada cambia de dirección, y el límite hasta donde la batería se puede mover.
    private int margenIzquierda;
    private int margenDerecha;
    private int margenAbajo;

    // Observador
    private Observador observador;
    private boolean partidaPerdida;

    // Timers
    Timer timerEjecucion;
    TimerTask taskEjecucion;
    Timer timerOleada;
    TimerTask taskOleada;

    /**
     * Registra el elemento pasado como observador.
     * @param observador Observador a registrar.
     */
    private void registrarObservador(Observador observador) {this.observador = observador;}

    /**
     * Constructor.
     */
    public Area(Observador observador, int dificultad) {

        // Propiedades del Area misma
        dimension = new Dimension(1280, 1024);
        margenIzquierda = 50;
        margenDerecha = 750;
        margenAbajo = 680;
        partidaPerdida = false;

        // Generación de las entidades
        registrarObservador(observador);
        generarEntidades();

        // Timers
        configurarTimers();
    }

    // ------------------------------------
    // Métodos públicos
    // ------------------------------------

    /**
     * Permite mover al jugador (representado por la Batería) a la derecha.
     */
    public void moverJugadorDerecha(){
        if (entidades.get(7).getEsquinaSuperiorDerecha().getPosicionX() < margenDerecha){
            entidades.get(7).moverseDerecha(5);
            System.out.println("Se movió el jugador a la derecha. Posición: " + entidades.get(7).getPunto());
        }
    }

    /**
     * Permite mover al jugador (representado por la Batería) a la izquierda.
     */
    public void moverJugadorIzquierda(){
        if (entidades.get(7).getEsquinaInferiorIzquierda().getPosicionX() > margenIzquierda){
            entidades.get(7).moverseIzquierda(5);
            System.out.println("Se movió el jugador a la izquierda. Posición: " + entidades.get(7).getPunto());
        }
    }

    /**
     * Permite mover hacer que la Batería que representa al jugador dispare un proyectil.
     */
    public void dispararJugador(){
        entidades.put(contadorEntidades, ((Bateria) entidades.get(7)).disparar());
        contadorEntidades++;
        System.out.println("Se disparó un proyectil. Entidades: " + entidades.size());
    }

    // ------------------------------------
    // Métodos privados
    // ------------------------------------

    /**
     * Genera las entidades en el Area de juego.
     */
    private void generarEntidades(){
        entidades = new HashMap<>();

        // -------------------------- Muros

        entidades.put(0, new Muro(new Punto(125, 700)));
        entidades.put(1, new Muro(new Punto(225, 700)));
        entidades.put(2, new Muro(new Punto(325, 700)));
        entidades.put(3, new Muro(new Punto(425, 700)));
        entidades.put(4, new Muro(new Punto(525, 700)));
        entidades.put(5, new Muro(new Punto(625, 700)));

        // -------------------------- Barra
        entidades.put(6, new Barra(new Punto(50, 830)));

        // -------------------------- Batería
        entidades.put(7, new Bateria(new Punto(375, 808)));

        // -------------------------- Oleada

        oleada = new Oleada(new Punto(235, 90));
        direccionOleada = Direcciones.DERECHA;
        contadorEntidades = 8;
        for (Entidad nave : oleada.devolverNaves()){
            entidades.put(contadorEntidades, nave);
            contadorEntidades++;
        }
    }

    /**
     * Configura los dos timers que utiliza el Area.
     */
    private void configurarTimers(){
        timerEjecucion = new Timer();
        taskEjecucion = new TimerTask() {
            @Override
            public void run() {
                if (!partidaPerdida){
                    moverProyectiles();
                    verificarColisionProyectiles();
                    notificarVisual();}
            }
        };
        timerEjecucion.scheduleAtFixedRate(taskEjecucion, 0, 10);

        timerOleada = new Timer();
        taskOleada = new TimerTask() {
            @Override
            public void run() {
                if (!partidaPerdida) {moverOleada();}
            }
        };
        timerOleada.scheduleAtFixedRate(taskOleada, 0, 300);
    }

    /**
     * Ejecuta la lógica de movimiento de la Oleada (de naves).
     */
    private void moverOleada(){
        // ------------------------ Movimiento de la oleada
        if (oleada.getEsquinaInferiorIzquierda().getPosicionY() < margenAbajo || oleada.getEsquinaInferiorDerecha().getPosicionX() < margenDerecha){
            // -------- Cuando llega a los bordes
            if (oleada.getEsquinaSuperiorIzquierda().getPosicionX() < margenIzquierda && direccionOleada == Direcciones.IZQUIERDA){
                direccionOleada = Direcciones.DERECHA;
                oleada.moverseAbajo(50);
            } else if (oleada.getEsquinaSuperiorDerecha().getPosicionX() > margenDerecha && direccionOleada == Direcciones.DERECHA){
                direccionOleada = Direcciones.IZQUIERDA;
                oleada.moverseAbajo(50);
            } else {
                // -------- Movimiento horizontal
                if (direccionOleada == Direcciones.IZQUIERDA){
                    oleada.moverseIzquierda(50);
                } else if (direccionOleada == Direcciones.DERECHA){
                    oleada.moverseDerecha(50);
                }
            }
        } else {
            System.out.println("Fin de la partida");
            partidaPerdida = true;
        }

        oleada.actualizarPosicionNaves();
        System.out.println("La oleada se movio a :" + oleada.getPunto());
    }

    /**
     * Llama por primera vez al observador, a la espera del jugador
     */
    private void notificarVisual(){

        // Notificación a la vista por medio del obsevador
        for (int i = 0; i < entidades.size(); i++) {
            if (entidades.get(i) != null) {
                observador.actualizarPosiciones(i, entidades.get(i).getPunto(), entidades.get(i).getDimension(), entidades.get(i).getTipoEntidad(), entidades.get(i).getInactivo());
            }
        }
    }

    /**
     * Ejecuta el movimiento correspondiente de los proyectiles que estén en el área.
     */
    private void moverProyectiles(){
        // Movimiento de los proyectiles
        for (Entidad entidad : entidades.values()){
            if (entidad instanceof Proyectil){ // Similar al casteo. Aquí simplemente especifico la entidad.
                Proyectil proyectil = (Proyectil) entidad;
                proyectil.continuarTrayectoria();
            }
        }
    }

    /**
     * Verifica si un proyectil colisionó con una Nave, un Muro o la Batería.
     */
    private void verificarColisionProyectiles(){
        // Colisión de los proyectiles
        for (Map.Entry<Integer, Entidad> proyectil : entidades.entrySet()){
            if (proyectil.getValue() instanceof Proyectil){
                for (Entidad objetivo : entidades.values()){
                    if (objetivo instanceof Nave && proyectil.getValue().colisionoCon(objetivo) && !proyectil.getValue().getInactivo() && !objetivo.getInactivo()){
                        ((Nave) objetivo).serDaniado();
                        ((Proyectil) proyectil.getValue()).serDaniado();
                        System.out.println("Una nave ha sido dañada.");
                    } else if (objetivo instanceof Muro && proyectil.getValue().colisionoCon(objetivo) && !proyectil.getValue().getInactivo() && !objetivo.getInactivo()){
                        ((Muro) objetivo).serDaniado();
                        ((Proyectil) proyectil.getValue()).serDaniado();
                        System.out.println("Un muro ha sido dañado.");
                    } else if (objetivo instanceof Bateria && proyectil.getValue().colisionoCon(objetivo) && !proyectil.getValue().getInactivo() && !objetivo.getInactivo()){
                        ((Bateria) objetivo).serDaniado();
                        ((Proyectil) proyectil.getValue()).serDaniado();
                        System.out.println("Una nave ha sido dañada.");
                    }
                }
            }
        }
    }
}
