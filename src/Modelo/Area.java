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

    // Dificultad / intervalo de movimiento de la oleada (ms)
    private Dificultad dificultad = Dificultad.MASTER;
    private long movimientoIntervalMillis = 13; // valor por defecto (comportamiento previo)

    // Estado de iniciado (si los timers están corriendo)
    private boolean iniciado = false;

    // Lock para proteger accesos concurrentes a 'entidades' y a los timers relacionados
    private final Object entidadesLock = new Object();

    /**
     * Registra el elemento pasado como observador.
     * @param observador Observador a registrar.
     */
    private void registrarObservador(Observador observador) {this.observador = observador;}

    /**
     * Constructor.
     * @param observador Elemento que funcionará como observador. El objeto debe implementar la interfaz Observador.
     */
    public Area(Observador observador) {

        // Propiedades del Area misma
        dimension = new Dimension(800, 900);
        margenIzquierda = 50;
        margenDerecha = 750;
        margenAbajo = 680;
        estadoPartida = EstadoPartida.EN_CURSO;

        // Generación de las entidades
        registrarObservador(observador);
        generarEntidades();
    }

    // ---------------------------------------------------------
    // Nuevos métodos: Dificultad / inicio / detener / reiniciar
    // ---------------------------------------------------------
    /**
     * Ajusta la dificultad de la partida. Esto cambia la velocidad de movimiento
     * de la oleada (se reprograma el timer de movimiento sólo si ya estaba creado).
     * @param dificultad dificultad seleccionada
     */
    public void setDificultad(Dificultad dificultad) {
        if (dificultad == null) return;
        this.dificultad = dificultad;
        switch (dificultad) {
            case CADETE:
                movimientoIntervalMillis = 40; // más lento
                break;
            case GUERRERO:
                movimientoIntervalMillis = 20; // medio
                break;
            case MASTER:
            default:
                movimientoIntervalMillis = 13; // rápido / por defecto anterior
                break;
        }
        // Si el timer de movimiento ya existe, reprogramarlo con el nuevo intervalo.
        synchronized (entidadesLock) {
            if (timerMovimientoOleada != null) {
                reprogramarTimerMovimientoOleada();
            }
        }
    }

    /**
     * Inicia (lanza) los timers de la partida. Llamar una vez cuando el jugador
     * presione JUGAR tras elegir la dificultad.
     */
    public void iniciar() {
        if (!iniciado) {
            ejecutarTimers();
            iniciado = true;
        }
    }

    /**
     * Detiene los timers de la partida. No cambia el estado de las entidades,
     * solo detiene la ejecución lógica.
     */
    public void detener() {
        synchronized (entidadesLock) {
                if (timerEjecucion != null) {
                    timerEjecucion.cancel();
                    timerEjecucion = null;
                    taskEjecucion = null;
                }
                if (timerMovimientoOleada != null) {
                    timerMovimientoOleada.cancel();
                    timerMovimientoOleada = null;
                    taskMovimientoOleada = null;
                }
                if (timerDisparoOleada != null) {
                    timerDisparoOleada.cancel();
                    timerDisparoOleada = null;
                    taskDisparoOleada = null;
                }
            iniciado = false;
        }
    }

    /**
     * Reinicia el Area a su estado inicial: detiene timers, regenera entidades y
     * vuelve a estado EN_CURSO (no arranca timers automáticamente).
     */
    public void reiniciar() {
        // Detener timers si estaban corriendo
        detener();

        // Regenerar entidades y estado inicial
        generarEntidades();
        estadoPartida = EstadoPartida.EN_CURSO;
        // NOTA: no arrancamos timers aquí; iniciar() debe llamarse desde quien inicie la partida.
    }

    /**
     * Reprograma (cancela y crea) el timer de movimiento de la oleada para usar el intervalo actual.
     */
    private void reprogramarTimerMovimientoOleada() {
        // Cancelar timer previo si existe
        if (timerMovimientoOleada != null) {
            try {
                timerMovimientoOleada.cancel();
            } catch (Exception ignored) {}
            timerMovimientoOleada = null;
            taskMovimientoOleada = null;
        }
        // Crear uno nuevo
        scheduleMovimientoOleada();
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
        synchronized (entidadesLock) {
            entidades.put(contadorEntidades, ((Bateria) entidades.get(7)).disparar());
            contadorEntidades++;
        }
    }

    // ------------------------------------
    // Métodos privados
    // ------------------------------------

    /**
     * Genera las entidades en el Area de juego.
     */
    private void generarEntidades(){
        Map<Integer, Entidad> nuevas = new HashMap<>();

        // -------------------------- Muros
        nuevas.put(0, new Muro(new Punto(125, 700)));
        nuevas.put(1, new Muro(new Punto(225, 700)));
        nuevas.put(2, new Muro(new Punto(325, 700)));
        nuevas.put(3, new Muro(new Punto(425, 700)));
        nuevas.put(4, new Muro(new Punto(525, 700)));
        nuevas.put(5, new Muro(new Punto(625, 700)));

        // -------------------------- Barra
        nuevas.put(6, new Barra(new Punto(50, 830)));

        // -------------------------- Batería
        nuevas.put(7, new Bateria(new Punto(375, 808)));

        // -------------------------- Oleada
        oleada = new Oleada(new Punto(235, 90));
        direccionOleada = Direcciones.DERECHA;
        contadorEntidades = 8;
        for (Entidad nave : oleada.devolverNaves()){
            nuevas.put(contadorEntidades, nave);
            contadorEntidades++;
        }

        // Asigno el mapa de forma atómica bajo lock
        synchronized (entidadesLock) {
            entidades = nuevas;
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

        // Movimiento de la oleada (usamos el método que programa el timer con el intervalo configurado)
        scheduleMovimientoOleada();

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
     * Programa el timer de movimiento de la oleada según movimientoIntervalMillis.
     */
    private void scheduleMovimientoOleada() {
        timerMovimientoOleada = new Timer();
        taskMovimientoOleada = new TimerTask() {
            @Override
            public void run() {
                if (estadoPartida == EstadoPartida.EN_CURSO) {
                    moverOleada();
                }
            }
        };
        timerMovimientoOleada.scheduleAtFixedRate(taskMovimientoOleada, 0, movimientoIntervalMillis);
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
                    oleada.moverseIzquierda(potenciador);
                } else if (direccionOleada == Direcciones.DERECHA){
                    oleada.moverseDerecha(potenciador);
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

        // Tomo snapshot atómico de las entradas bajo lock para evitar concurrencia
        List<Map.Entry<Integer, Entidad>> snapshot;
        synchronized (entidadesLock) {
            snapshot = new ArrayList<>(entidades.entrySet());
        }

        // Notificación a la vista por medio del observador (iteramos sobre snapshot)
        for (Map.Entry<Integer, Entidad> entry : snapshot) {
            Integer id = entry.getKey();
            Entidad entidad = entry.getValue();
            observador.actualizarPosiciones(id, entidad.getPunto(), entidad.getDimension(), entidad.getTipoEntidad(), entidad.getInactivo());
        }
    }

    /**
     * Ejecuta el movimiento correspondiente de los proyectiles que estén en el área.
     */
    private void moverProyectiles(){
        // Tomo snapshot atómico de las entidades bajo lock
        List<Entidad> snapshot;
        synchronized (entidadesLock) {
            snapshot = new ArrayList<>(entidades.values());
        }

        // Movimiento de los proyectiles (trabajamos con snapshot para evitar ConcurrentModification)
        for (Entidad entidad : snapshot){
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

        // Tomo snapshot atómico de las entradas bajo lock
        List<Map.Entry<Integer, Entidad>> proyectilesSnapshot;
        synchronized (entidadesLock) {
            proyectilesSnapshot = new ArrayList<>(entidades.entrySet());
        }

        // La recorro en busca de proyectiles (trabajo sobre snapshot para evitar ConcurrentModification)
        for (Map.Entry<Integer, Entidad> posibleProyectil : proyectilesSnapshot) {
            if (posibleProyectil.getValue() instanceof Proyectil) {

                // Datos del proyectil
                int idProyectil = posibleProyectil.getKey();
                Proyectil proyectil = (Proyectil) posibleProyectil.getValue();

                // Para comprobar objetivos, tomo otro snapshot local y atómico
                List<Map.Entry<Integer, Entidad>> objetivosSnapshot;
                synchronized (entidadesLock) {
                    objetivosSnapshot = new ArrayList<>(entidades.entrySet());
                }

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
        List<Integer> inactivos = new ArrayList<Integer>();

        // Tomo snapshot y genero la lista de inactivos bajo lock
        synchronized (entidadesLock) {
            for (Map.Entry<Integer, Entidad> entry : new ArrayList<>(entidades.entrySet())){
                if (entry.getValue().getInactivo()){
                    inactivos.add(entry.getKey());
                }
            }

            // Remuevo los inactivos del Map dentro del lock
            for (Integer inactivo : inactivos){
                entidades.remove(inactivo);
            }
        }
    }

    /**
     * Hace que una de las naves de la tercer fila dispare, en caso de existir.
     */
    private void dispararNave(){
        synchronized (entidadesLock) {
            entidades.put(contadorEntidades,oleada.dispararNaveAleatoria());
            contadorEntidades++;
        }
    }
}