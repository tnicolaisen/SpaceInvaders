package Modelo;

import Modelo.Entidades.*;
import Modelo.Interfaces.Daniable;
import Modelo.Interfaces.Observador;
import Utilidades.Dimension;
import Utilidades.Direcciones;
import Utilidades.Punto;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Área en donde se ejecuta lógicamente el juego.
 * Implementación sin locks exclusivos para 'entidades' (usa ConcurrentHashMap)
 * para evitar deadlocks entre Timer-threads y el EDT.
 */
public class Area {

    // Entidades (concurrente)
    private AtomicInteger contadorEntidades = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, Entidad> entidades = new ConcurrentHashMap<Integer, Entidad>();
    private Oleada oleada;
    private Direcciones direccionOleada;

    // Dimensiones de la ventana
    private Dimension dimension;

    // Márgenes
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

    // Dificultad / intervalo
    private Dificultad dificultad = Dificultad.MASTER;
    private long movimientoIntervalMillis = 13;

    // Estado iniciado
    private volatile boolean iniciado = false;

    private int cantidadNavesInicial = 0;

    // Scoring / niveles / vidas
    private int puntajeTotal = 0;
    private int nivel = 1;
    private int vidasJugador = 3;
    private int siguienteVidaExtra = 500;
    private final int puntosPorNave = 10;
    private final int puntosPorCambioNivel = 200;

    /**
     * Registra el elemento pasado como observador.
     * @param observador Observador a registrar.
     */
    private void registrarObservador(Observador observador) { this.observador = observador; }

    /**
     * Constructor.
     * @param observador Elemento que funcionará como observador. El objeto debe implementar la interfaz Observador.
     */
    public Area(Observador observador) {
        dimension = new Dimension(800, 900);
        margenIzquierda = 50;
        margenDerecha = 750;
        margenAbajo = 680;
        estadoPartida = EstadoPartida.EN_CURSO;

        registrarObservador(observador);
        generarEntidades();
    }

    // -------- Getters de estado del juego ----------
    public int getPuntajeTotal() { return puntajeTotal; }
    public int getVidasJugador() { return vidasJugador; }

    /**
     * Ajusta la dificultad de la partida. Reprograma timer de movimiento si corresponde.
     * @param dificultad dificultad seleccionada
     */
    public void setDificultad(Dificultad dificultad) {
        if (dificultad == null) return;
        this.dificultad = dificultad;
        switch (dificultad) {
            case CADETE -> movimientoIntervalMillis = 40;
            case GUERRERO -> movimientoIntervalMillis = 20;
            case MASTER -> movimientoIntervalMillis = 13;
        }
        if (timerMovimientoOleada != null) {
            reprogramarTimerMovimientoOleada();
        }
    }

    /**
     * Inicia timers de la partida.
     */
    public void iniciar() {
        if (!iniciado) {
            ejecutarTimers();
            iniciado = true;
        }
    }

    /**
     * Detiene los timers de la partida.
     */
    public void detener() {
        Timer te = timerEjecucion;
        Timer tm = timerMovimientoOleada;
        Timer td = timerDisparoOleada;

        timerEjecucion = null;
        taskEjecucion = null;
        timerMovimientoOleada = null;
        taskMovimientoOleada = null;
        timerDisparoOleada = null;
        taskDisparoOleada = null;
        iniciado = false;

        if (te != null) te.cancel();
        if (tm != null) tm.cancel();
        if (td != null) td.cancel();
    }

    /**
     * Reinicia el Area (detiene, regenera y resetea estado).
     */
    public void reiniciar() {
        detener();
        generarEntidades();
        estadoPartida = EstadoPartida.EN_CURSO;
        puntajeTotal = 0;
        nivel = 1;
        vidasJugador = 3;
        siguienteVidaExtra = 500;
    }

    private void reprogramarTimerMovimientoOleada() {
        if (timerMovimientoOleada != null) {
            try { timerMovimientoOleada.cancel(); } catch (Exception ignored) {}
            timerMovimientoOleada = null;
            taskMovimientoOleada = null;
        }
        scheduleMovimientoOleada();
    }

    // ---------- Acciones del jugador ----------
    public void moverJugadorDerecha(){
        Entidad bateria = entidades.get(7);
        if (bateria != null && bateria.getEsquinaSuperiorDerecha().getPosicionX() < margenDerecha){
            bateria.moverseDerecha(20);
        }
    }

    public void moverJugadorIzquierda(){
        Entidad bateria = entidades.get(7);
        if (bateria != null && bateria.getEsquinaInferiorIzquierda().getPosicionX() > margenIzquierda){
            bateria.moverseIzquierda(20);
        }
    }

    public void dispararJugador(){
        Entidad bateria = entidades.get(7);
        if (bateria instanceof Bateria) {
            Proyectil p = ((Bateria) bateria).disparar();
            int id = contadorEntidades.getAndIncrement();
            entidades.put(id, p);
            System.out.println("Area: dispararJugador -> proyectil id=" + id);
        }
    }

    // ---------- Generación inicial ----------
    private void generarEntidades(){
        ConcurrentHashMap<Integer, Entidad> nuevas = new ConcurrentHashMap<>();

        nuevas.put(0, new Muro(new Punto(125, 700)));
        nuevas.put(1, new Muro(new Punto(225, 700)));
        nuevas.put(2, new Muro(new Punto(325, 700)));
        nuevas.put(3, new Muro(new Punto(425, 700)));
        nuevas.put(4, new Muro(new Punto(525, 700)));
        nuevas.put(5, new Muro(new Punto(625, 700)));

        nuevas.put(6, new Barra(new Punto(50, 830)));

        nuevas.put(7, new Bateria(new Punto(375, 808)));

        oleada = new Oleada(new Punto(235, 90));
        direccionOleada = Direcciones.DERECHA;
        int startId = 8;
        for (Entidad nave : oleada.devolverNaves()){
            nuevas.put(startId++, nave);
        }
        contadorEntidades.set(startId);

        entidades.clear();
        entidades.putAll(nuevas);
        cantidadNavesInicial = oleada.devolverNaves().size();
    }

    // ---------- Timers y ejecución ----------
    private void ejecutarTimers(){
        timerEjecucion = new Timer();
        taskEjecucion = new TimerTask() {
            @Override
            public void run() {
                if (estadoPartida == EstadoPartida.EN_CURSO){
                    moverProyectiles();
                    verificarColisionProyectiles();
                    notificarVisual();
                    eliminarInactivos();

                    // Si no quedan naves vivas -> PARTIDA GANADA
                    if (oleada.getCantidadDeNavesVivas() == 0){
                        estadoPartida = EstadoPartida.GANADA;
                        // detener lógica antes de notificar
                        detener();
                        if (observador != null) observador.partidaFinalizada(estadoPartida, puntajeTotal);
                    }
                }
            }
        };
        timerEjecucion.scheduleAtFixedRate(taskEjecucion, 0, 100);

        scheduleMovimientoOleada();

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

    private void moverOleada(){
        int potenciador = 1;
        int vivas = oleada.getCantidadDeNavesVivas();
        if (vivas <= 10 && vivas > 5) potenciador = 2;
        else if (vivas <= 5) potenciador = 3;

        if (oleada.getEsquinaInferiorIzquierda().getPosicionY() < margenAbajo || oleada.getEsquinaInferiorDerecha().getPosicionX() < margenDerecha){
            if (oleada.getEsquinaSuperiorIzquierda().getPosicionX() < margenIzquierda && direccionOleada == Direcciones.IZQUIERDA){
                direccionOleada = Direcciones.DERECHA;
                oleada.moverseAbajo(50);
            } else if (oleada.getEsquinaSuperiorDerecha().getPosicionX() > margenDerecha && direccionOleada == Direcciones.DERECHA){
                direccionOleada = Direcciones.IZQUIERDA;
                oleada.moverseAbajo(50);
            } else {
                if (direccionOleada == Direcciones.IZQUIERDA){
                    oleada.moverseIzquierda(potenciador);
                } else if (direccionOleada == Direcciones.DERECHA){
                    oleada.moverseDerecha(potenciador);
                }
            }
        } else {
            estadoPartida = EstadoPartida.PERDIDA;
            if (observador != null) observador.partidaFinalizada(estadoPartida, puntajeTotal);
        }
        oleada.actualizarPosicionNaves();
    }

    // ---------- Niveles / vidas ----------
    private void revisarYOtorgarVidaExtra() {
        while (puntajeTotal >= siguienteVidaExtra) {
            vidasJugador++;
            siguienteVidaExtra += 500;
        }
    }

    // ---------- Visualización ----------
    private void notificarVisual(){
        // snapshot sin bloquear
        List<Map.Entry<Integer, Entidad>> snapshot = new ArrayList<>(entidades.entrySet());
        for (Map.Entry<Integer, Entidad> entry : snapshot) {
            Integer id = entry.getKey();
            Entidad entidad = entry.getValue();
            if (observador != null) observador.actualizarPosiciones(id, entidad.getPunto(), entidad.getDimension(), entidad.getTipoEntidad(), entidad.getInactivo());
        }
    }

    // ---------- Movimiento proyectiles ----------
    private void moverProyectiles(){
        List<Entidad> snapshot = new ArrayList<>(entidades.values());
        for (Entidad entidad : snapshot){
            if (entidad instanceof Proyectil){
                Proyectil proyectil = (Proyectil) entidad;
                proyectil.continuarTrayectoria();
                if (proyectil.getPunto().getPosicionY() < 0 || proyectil.getPunto().getPosicionY() > this.dimension.getAlto()){
                    proyectil.serDaniado();
                }
            }
        }
    }

    // ---------- Colisiones ----------
    private void verificarColisionProyectiles(){
        List<Map.Entry<Integer, Entidad>> proyectilesSnapshot = new ArrayList<>(entidades.entrySet());

        for (Map.Entry<Integer, Entidad> posibleProyectil : proyectilesSnapshot) {
            if (posibleProyectil.getValue() instanceof Proyectil) {

                int idProyectil = posibleProyectil.getKey();
                Proyectil proyectil = (Proyectil) posibleProyectil.getValue();

                List<Map.Entry<Integer, Entidad>> objetivosSnapshot = new ArrayList<>(entidades.entrySet());

                for (Map.Entry<Integer, Entidad> posibleObjetivo : objetivosSnapshot) {
                    Entidad posible = posibleObjetivo.getValue();

                    if (!(posible instanceof Proyectil) && posible instanceof Daniable) {

                        int idObjetivo = posibleObjetivo.getKey();
                        Entidad objetivo = posible;

                        if (proyectil.colisionoCon(objetivo)) {
                            if (objetivo instanceof Muro) {
                                if (proyectil.getDireccion() == Direcciones.ARRIBA) {
                                    ((Muro) objetivo).serDaniadoPorJugador();
                                } else {
                                    ((Muro) objetivo).serDaniado();
                                }
                                proyectil.serDaniado();
                            } else {
                                proyectil.serDaniado();
                                ((Daniable) objetivo).serDaniado();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    // ---------- Limpieza de inactivos ----------
    private void eliminarInactivos(){
        List<Integer> inactivos = new ArrayList<Integer>();

        for (Map.Entry<Integer, Entidad> entry : new ArrayList<>(entidades.entrySet())){
            if (entry.getValue().getInactivo()){
                inactivos.add(entry.getKey());
                if (entry.getValue().getTipoEntidad() == Utilidades.TiposEntidades.NAVE) {
                    puntajeTotal += puntosPorNave;
                    revisarYOtorgarVidaExtra();
                }
            }
        }

        for (Integer inactivo : inactivos){
            entidades.remove(inactivo);
        }
    }

    private void dispararNave(){
        Proyectil p = oleada.dispararNaveAleatoria();
        int id = contadorEntidades.getAndIncrement();
        entidades.put(id, p);
    }
}