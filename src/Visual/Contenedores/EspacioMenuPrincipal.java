package Visual.Contenedores;
import Controlador.Controlador;
import Modelo.Area;
import Modelo.Dificultad;
import Visual.Ventanas.VentanaDificultad;
import Visual.Ventanas.VentanaJuego;
import Visual.Ventanas.VentanaRanking;
import Utilidades.MenuViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel del menú principal que contiene la referencia a la ventana del juego.
 */
public class EspacioMenuPrincipal extends JPanel {
    private EspacioJuego espacioJuego;
    private Area area;
    private VentanaJuego ventanaJuego;
    private Controlador controlador;

    private JLabel lblNave;
    private JLabel lblBateria;
    private JLabel lblLogo;
    private JLabel lblSubtitulo;
    private JLabel lblCreditos;
    private JLabel lblCreditosDisplay;

    private JButton btnJugar;
    private JButton btnRanking;

    ImageIcon imgBtnJugarIdle;
    ImageIcon imgBtnJugarClicked;
    ImageIcon imgBtnJugarHover;
    ImageIcon imgBtnRankingIdle;
    ImageIcon imgBtnRankingHover;
    ImageIcon imgBtnRankingClicked;

    private int creditos = 0;

    // Viewer que expone el estado del menú (sólo datos)
    private final MenuViewer viewer;

    /**
     * Constructor.
     * @param controlador controlador principal
     */
    public EspacioMenuPrincipal(Controlador controlador) {
        this.controlador = controlador;
        this.viewer = new MenuViewer();
        configurarEspacio();
        cargarElementos();
        cargarImagenesBotones();
        espacioJuego = new EspacioJuego(controlador);
        area = new Area(espacioJuego);

        // Registramos en el controlador el Area, el viewer y un callback para cerrar la ventana.
        // Así Controlador no necesita importar nada de Visual.
        if (this.controlador != null) {
            this.controlador.registrarArea(this.area);
            this.controlador.registrarMenuViewer(this.viewer);
            this.controlador.registrarCerrarVentanaCallback(new Runnable() {
                @Override
                public void run() {
                    if (SwingUtilities.isEventDispatchThread()) {
                        cerrarVentanaJuego();
                    } else {
                        SwingUtilities.invokeLater(() -> cerrarVentanaJuego());
                    }
                }
            });
        }

        configurarListeners();
        actualizarDisplayCreditos();
    }

    public Area getArea() {
        return area;
    }

    /**
     * Permite obtener el viewer del menú para lectura por otras capas.
     * @return MenuViewer con estado actual del menú.
     */
    public MenuViewer getMenuViewer() {
        return viewer;
    }

    /**
     * Carga créditos (usa para integrar la UI de carga de créditos).
     * @param cantidad cantidad de créditos a sumar (entero >= 0)
     */
    public void cargarCreditos(int cantidad) {
        if (cantidad <= 0) return;
        creditos += cantidad;
        // mantener viewer en sincronía (simple asignación)
        viewer.creditos = creditos;
        actualizarDisplayCreditos();
    }

    /**
     * Consume un crédito para iniciar una partida. Devuelve true si había crédito y se consumió.
     * @return true si se consumió un crédito.
     */
    public boolean consumirCreditoParaPartida() {
        if (creditos <= 0) return false;
        creditos--;
        viewer.creditos = creditos;
        actualizarDisplayCreditos();
        return true;
    }

    /**
     * Devuelve creditos al saldo (por ejemplo si se decide reintegrar).
     * @param cantidad cantidad a devolver.
     */
    public void devolverCreditos(int cantidad) {
        if (cantidad <= 0) return;
        creditos += cantidad;
        viewer.creditos = creditos;
        actualizarDisplayCreditos();
    }

    /**
     * Retorna la cantidad de creditos actualmente cargados.
     * @return creditos
     */
    public int obtenerCreditos() { return creditos; }

    /**
     * Reinicia el Area a su estado inicial (se usa cuando se cierra la ventana del juego).
     */
    public void reiniciarArea() {
        if (espacioJuego != null) {
            espacioJuego.limpiarSprites();
        }
        if (area != null) {
            new Thread(() -> area.reiniciar()).start();
        }
        // cuando se reinicia el área se asume que la ventana del juego ya está cerrada
        viewer.ventanaJuegoAbierta = false;
    }

    /**
     * Cierra (dispose) la ventana del juego si está abierta.
     */
    public void cerrarVentanaJuego() {
        if (ventanaJuego != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                ventanaJuego.dispose();
                ventanaJuego = null;
                viewer.ventanaJuegoAbierta = false;
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ventanaJuego.dispose();
                        ventanaJuego = null;
                        viewer.ventanaJuegoAbierta = false;
                    }
                });
            }
        }
    }

    private void actualizarDisplayCreditos() {
        String texto = String.valueOf(creditos);
        lblCreditosDisplay.setText(texto);
    }

    private void cargarElementos(){
        lblLogo = new JLabel();
        lblLogo.setVisible(true);
        lblLogo.setBounds(278, 150, 244, 85);
        java.net.URL logoImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/logo.png");
        lblLogo.setIcon(new ImageIcon(logoImgUrl));
        this.add(lblLogo);

        lblNave = new JLabel();
        lblNave.setVisible(true);
        lblNave.setBounds(490, 130, 50, 50);
        java.net.URL naveImgUrl = getClass().getResource("/Imagenes/Partida/Nave.png");
        lblNave.setIcon(new ImageIcon(naveImgUrl));
        this.add(lblNave);

        lblBateria = new JLabel();
        lblBateria.setVisible(true);
        lblBateria.setBounds(250, 200, 50, 38);
        java.net.URL bateriaImgUrl = getClass().getResource("/Imagenes/Partida/Bateria.png");
        lblBateria.setIcon(new ImageIcon(bateriaImgUrl));
        this.add(lblBateria);

        lblSubtitulo = new JLabel();
        lblSubtitulo.setVisible(true);
        lblSubtitulo.setBounds(260, 260, 282, 16);
        java.net.URL subtituloImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/subtitulo.png");
        lblSubtitulo.setIcon(new ImageIcon(subtituloImgUrl));
        this.add(lblSubtitulo);

        lblCreditos = new JLabel();
        lblCreditos.setVisible(true);
        lblCreditos.setBounds(233, 580, 256, 40);
        java.net.URL creditosImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/creditos.png");
        lblCreditos.setIcon(new ImageIcon(creditosImgUrl));
        this.add(lblCreditos);

        lblCreditosDisplay = new JLabel("0", SwingConstants.CENTER);
        lblCreditosDisplay.setVisible(true);
        lblCreditosDisplay.setBounds(513, 592, 54, 28);
        lblCreditosDisplay.setForeground(new Color(0xFFD300));
        lblCreditosDisplay.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblCreditosDisplay.setOpaque(false);
        this.add(lblCreditosDisplay);

        btnJugar = new JButton();
        btnJugar.setVisible(true);
        btnJugar.setBounds(120, 690, 236, 60);
        java.net.URL jugarImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/botonJugarIdle.png");
        btnJugar.setIcon(new ImageIcon(jugarImgUrl));
        btnJugar.setBackground(Color.black);
        btnJugar.setBorderPainted(false);
        this.add(btnJugar);

        btnRanking = new JButton();
        btnRanking.setVisible(true);
        btnRanking.setBounds(444, 690, 236, 60);
        java.net.URL rankingImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/botonRankingIdle.png");
        btnRanking.setIcon(new ImageIcon(rankingImgUrl));
        btnRanking.setBackground(Color.black);
        btnRanking.setBorderPainted(false);
        this.add(btnRanking);
    }

    private void cargarImagenesBotones(){
        imgBtnJugarIdle = new ImageIcon(getClass().getResource("/Imagenes/MenuPrincipal/botonJugarIdle.png"));
        imgBtnJugarClicked = new ImageIcon(getClass().getResource(("/Imagenes/MenuPrincipal/botonJugarClicked.png")));
        imgBtnJugarHover = new ImageIcon(getClass().getResource("/Imagenes/MenuPrincipal/botonJugarHover.png"));

        imgBtnRankingIdle = new ImageIcon(getClass().getResource("/Imagenes/MenuPrincipal/botonRankingIdle.png"));
        imgBtnRankingHover = new ImageIcon(getClass().getResource("/Imagenes/MenuPrincipal/botonRankingClicked.png"));
        imgBtnRankingClicked = new ImageIcon(getClass().getResource("/Imagenes/MenuPrincipal/botonRankingHover.png"));
    }

    private void configurarEspacio(){
        this.setBounds(0, 0, 800, 900);
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
        this.setLayout(null);
    }

    private void configurarListeners(){
        btnJugar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btnJugar.setIcon(imgBtnJugarHover);
            }
            @Override public void mouseExited(MouseEvent e) {
                btnJugar.setIcon(imgBtnJugarIdle);
            }
            @Override public void mousePressed(MouseEvent e) {
                btnJugar.setIcon(imgBtnJugarClicked);
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (btnJugar.contains(e.getPoint())){
                    btnJugar.setIcon(imgBtnJugarHover);
                }
                else {
                    btnJugar.setIcon(imgBtnJugarClicked);
                }
                Dificultad seleccionada = VentanaDificultad.mostrarDialogo(EspacioMenuPrincipal.this);
                if (seleccionada != null) {
                    if (!consumirCreditoParaPartida()) {
                        JOptionPane.showMessageDialog(EspacioMenuPrincipal.this, "No hay créditos. Cargue créditos para jugar.", "Sin créditos", JOptionPane.INFORMATION_MESSAGE);
                        // Reponer el foco al panel para que las teclas vuelvan a funcionar
                        SwingUtilities.invokeLater(() -> requestFocusInWindow());
                        return;
                    }
                    area.setDificultad(seleccionada);
                    area.iniciar();
                    ventanaJuego = new VentanaJuego(espacioJuego, EspacioMenuPrincipal.this);
                    // mantener viewer en sincronía con ventana abierta
                    viewer.ventanaJuegoAbierta = (ventanaJuego != null);
                }
            }
        });

        btnRanking.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btnRanking.setIcon(imgBtnRankingClicked);
            }
            @Override public void mouseExited(MouseEvent e) {
                btnRanking.setIcon(imgBtnRankingIdle);
            }
            @Override public void mousePressed(MouseEvent e) {
                btnRanking.setIcon(imgBtnRankingHover);
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (btnRanking.contains(e.getPoint())){
                    btnRanking.setIcon(imgBtnRankingClicked);
                }
                else {
                    btnRanking.setIcon(imgBtnRankingClicked);
                }
                VentanaRanking.mostrarDialogo(EspacioMenuPrincipal.this);
                SwingUtilities.invokeLater(() -> requestFocusInWindow());
            }
        });

        // Simple KeyListener para SPACE / ESC
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    // Pulsar espacio en el menú carga 1 crédito
                    cargarCreditos(1);
                }
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.requestFocusInWindow();
    }
}