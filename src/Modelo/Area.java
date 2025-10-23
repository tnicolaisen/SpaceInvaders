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
    private Muro muro1;
    private Muro muro2;
    private Muro muro3;
    private Muro muro4;
    private Muro muro5;
    private Muro muro6;
    private Barra barra;
    private Bateria bateria;

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

    /**
     * Devuelve la batería controlable por el jugador.
     * @return Batería que representa al jugador.
     */
    public Entidad obtenerJugador(){return bateria;}

    public void moverJugadorDerecha(){
        if (bateria.getEsquinaSuperiorDerecha().getPosicionX() < margenDerecha){
            bateria.moverseDerecha(5);
            System.out.println("Se movió el jugador a la derecha. Posición: " + bateria.getPunto());
        }
    }

    public void moverJugadorIzquierda(){
        if (bateria.getEsquinaInferiorIzquierda().getPosicionX() > margenIzquierda){
            bateria.moverseIzquierda(5);
            System.out.println("Se movió el jugador a la izquierda. Posición: " + bateria.getPunto());
        }
    }

    public void dispararJugador(){
        entidades.put(contadorEntidades, bateria.disparar());
        contadorEntidades++;
        System.out.println("Se disparó un proyectil. Entidades: " + entidades.size());
    }

    /**
     * Genera las entidades en el Area de juego.
     */
    private void generarEntidades(){
        entidades = new HashMap<>();

        // --------------------------
        // Muros
        // --------------------------
        muro1 = new Muro(new Punto(125, 700));
        entidades.put(0, muro1);
        muro2 = new Muro(new Punto(225, 700));
        entidades.put(1, muro2);
        muro3 = new Muro(new Punto(325, 700));
        entidades.put(2, muro3);
        muro4 = new Muro(new Punto(425, 700));
        entidades.put(3, muro4);
        muro5 = new Muro(new Punto(525, 700));
        entidades.put(4, muro5);
        muro6 = new Muro(new Punto(625, 700));
        entidades.put(5, muro6);

        // --------------------------
        // Barra
        // --------------------------
        barra = new Barra(new Punto(50, 830));
        entidades.put(6, barra);

        // --------------------------
        // Bateria
        // --------------------------
        bateria = new Bateria(new Punto(375, 808));
        entidades.put(7, bateria);

        // --------------------------
        // Oleada
        // --------------------------
        oleada = new Oleada(new Punto(235, 90));
        direccionOleada = Direcciones.DERECHA;
        contadorEntidades = 8;
        for (Entidad nave : oleada.devolverNaves()){
            entidades.put(contadorEntidades, nave);
            contadorEntidades++;
        }
    }

    private void configurarTimers(){
        timerEjecucion = new Timer();
        taskEjecucion = new TimerTask() {
            @Override
            public void run() {
                if (!partidaPerdida){ejecutarCiclo();}
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
    private void ejecutarCiclo(){

        // Movimiento de los proyectiles
        for (Entidad entidad : entidades.values()){
            if (entidad instanceof Proyectil){ // Similar al casteo. Aquí simplemente especifico la entidad.
                Proyectil proyectil = (Proyectil) entidad;
                proyectil.continuarTrayectoria();
            }
        }

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

        // Notificación a la vista por medio del obsevador
        for (int i = 0; i < entidades.size(); i++) {
            if (entidades.get(i) != null) {
                observador.actualizarPosiciones(i, entidades.get(i).getPunto(), entidades.get(i).getDimension(), entidades.get(i).getTipoEntidad(), entidades.get(i).getInactivo());
            }
        }
    }
}
