package Modelo;
import Modelo.Entidades.*;
import Modelo.Interfaces.Daniable;
import Modelo.Interfaces.Observador;
import Utilidades.Dimension;
import Utilidades.Direcciones;
import Utilidades.Punto;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

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
    private EstadoPartida estadoPartida;

    // Timers
    Timer timerEjecucion;
    TimerTask taskEjecucion;
    Timer timerMovimientoOleada;
    TimerTask taskMovimientoOleada;
    Timer timerDisparoOleada;
    TimerTask taskDisparoOleada;

    /**
     * Registra el elemento pasado como observador.
     * @param observador Observador a registrar.
     */
    private void registrarObservador(Observador observador) {this.observador = observador;}

    /**
     * Constructor.
     * @param observador Elemento que funcionará como observador. El objeto debe implementar la interfaz Observador.
     * @param dificultad Dificultad en la que se juega.
     */
    public Area(Observador observador, int dificultad) {

        // Propiedades del Area misma
        dimension = new Dimension(1280, 1024);
        margenIzquierda = 50;
        margenDerecha = 750;
        margenAbajo = 680;
        estadoPartida = EstadoPartida.EN_CURSO;
        this.dificultad = dificultad;

        // Generación de las entidades
        registrarObservador(observador);
        generarEntidades();

        // Timers
        ejecutarTimers();
    }

    // ------------------------------------
    // Métodos públicos
    // ------------------------------------

    /**
     * Permite mover al jugador (representado por la Batería) a la derecha.
     */
    public void moverJugadorDerecha(){
        if (entidades.get(7).getEsquinaSuperiorDerecha().getPosicionX() < margenDerecha){
            entidades.get(7).moverseDerecha(20);

        }
    }

    /**
     * Permite mover al jugador (representado por la Batería) a la izquierda.
     */
    public void moverJugadorIzquierda(){
        if (entidades.get(7).getEsquinaInferiorIzquierda().getPosicionX() > margenIzquierda){
            entidades.get(7).moverseIzquierda(20);
        }
    }

    /**
     * Permite mover hacer que la Batería que representa al jugador dispare un proyectil.
     */
    public void dispararJugador(){
        entidades.put(contadorEntidades, ((Bateria) entidades.get(7)).disparar());
        contadorEntidades++;
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
     * Ejecuta los timers.
     */
    private void ejecutarTimers(){
        // Ejecución lógica del Area.
        timerEjecucion = new Timer();
        taskEjecucion = new TimerTask() {
            @Override
            public void run() {
                if (estadoPartida == EstadoPartida.EN_CURSO){
                    moverProyectiles();
                    verificarColisionProyectiles();
                    notificarVisual();
                    eliminarInactivos();

                    if (oleada.getCantidadDeNavesVivas() == 0){
                        estadoPartida = EstadoPartida.GANADA;
                        System.out.println("Victoria: ¡Partida ganada!");
                    }
                }
            }
        };
        timerEjecucion.scheduleAtFixedRate(taskEjecucion, 0, 100);

        // Movimiento de la oleada
        timerMovimientoOleada = new Timer();
        taskMovimientoOleada = new TimerTask() {
            @Override
            public void run() {
                if (estadoPartida == EstadoPartida.EN_CURSO) {
                    moverOleada();
                }
            }
        };
        timerMovimientoOleada.scheduleAtFixedRate(taskMovimientoOleada, 0, 13);

        // Disparo de la oleada
        timerDisparoOleada = new Timer();
        taskDisparoOleada = new TimerTask() {
            @Override
            public void run() {
                if (estadoPartida == EstadoPartida.EN_CURSO) {
                    dispararNave();
                }
            }
        };
        timerDisparoOleada.scheduleAtFixedRate(taskDisparoOleada, 0, 3000);
    }

    /**
     * Ejecuta la lógica de movimiento de la Oleada (de naves).
     */
    private void moverOleada(){
        // Utilizo el potenciador para poder incrementar la velocidad de movimiento de la Oleada cuando queden 10 y 5 naves.
        int potenciador = 1;
        if (oleada.getCantidadDeNavesVivas() <= 10 && oleada.getCantidadDeNavesVivas() > 5){potenciador = 2;}
        else if (oleada.getCantidadDeNavesVivas() <= 5) {potenciador = 3;}

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
                    oleada.moverseIzquierda(potenciador * dificultad);
                } else if (direccionOleada == Direcciones.DERECHA){
                    oleada.moverseDerecha(potenciador * dificultad);
                }
            }
        } else {
            System.out.println("¡Fallaste!: Partida perdida");
            estadoPartida = EstadoPartida.PERDIDA;
        }
        oleada.actualizarPosicionNaves();
    }

    /**
     * Llama por primera vez al observador, a la espera del jugador
     */
    private void notificarVisual(){

        // Notificación a la vista por medio del obsevador
        for (Map.Entry<Integer, Entidad> entry : new ArrayList<>(entidades.entrySet())) { // Ahora se pasa una copia en vez de trabajar sobre el mismo diccionario para evitar ConcurrentModificationException
            Integer id = entry.getKey();
            Entidad entidad = entry.getValue();
            observador.actualizarPosiciones(id, entidad.getPunto(), entidad.getDimension(), entidad.getTipoEntidad(), entidad.getInactivo());
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
                if (proyectil.getPunto().getPosicionY() < 0 || proyectil.getPunto().getPosicionY() > this.dimension.getAlto()){
                    proyectil.serDaniado();
                }
            }
        }
    }

    /**
     * Verifica si un proyectil colisionó con una Nave, un Muro o la Batería.
     */
    private void verificarColisionProyectiles(){

        // Se hace una copia del diccionario de entidades para evitar ConcurrentModification
        List<Map.Entry<Integer, Entidad>> proyectilesSnapshot = new ArrayList<>(entidades.entrySet());

        // La recorro en busca de proyectiles
        for (Map.Entry<Integer, Entidad> posibleProyectil : proyectilesSnapshot) {
            if (posibleProyectil.getValue() instanceof Proyectil) {

                // Datos del proyectil
                int idProyectil = posibleProyectil.getKey();
                Proyectil proyectil = (Proyectil) posibleProyectil.getValue();

                // Para comprobar objetivos, también trabajamos sobre una copia (evita ConcurrentModification si se añaden/producen cambios)
                List<Map.Entry<Integer, Entidad>> objetivosSnapshot = new ArrayList<>(entidades.entrySet());
                for (Map.Entry<Integer, Entidad> posibleObjetivo : objetivosSnapshot) {
                    Entidad posible = posibleObjetivo.getValue();

                    if (!(posible instanceof Proyectil) && posible instanceof Daniable) {

                        int idObjetivo = posibleObjetivo.getKey();
                        Entidad objetivo = posible;

                        if (proyectil.colisionoCon(objetivo)) {
                            ((Daniable) proyectil).serDaniado();
                            ((Daniable) objetivo).serDaniado();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Elimina todas las Entidades inactivas del diccionario de Entidades.
     */
    private void eliminarInactivos(){
        // Creo una lista para guardar los IDs de los eliminados
        List<Integer> inactivos = new ArrayList<Integer>();

        for (Map.Entry entidad: entidades.entrySet()){
            if (((Entidad) entidad.getValue()).getInactivo()){
                inactivos.add((Integer) entidad.getKey());
            }
        }

        for (Integer inactivo : inactivos){
            entidades.remove(inactivo);
        }
    }

    /**
     * Hace que una de las naves de la tercer fila dispare, en caso de existir.
     */
    private void dispararNave(){
        entidades.put(contadorEntidades,oleada.dispararNaveAleatoria());
        contadorEntidades++;
    }
}
