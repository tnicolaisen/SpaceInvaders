package Visual.Contenedores;
import Controlador.Controlador;
import Modelo.Area;
import Modelo.Dificultad;
import Visual.Ventanas.VentanaDificultad;
import Visual.Ventanas.VentanaJuego;

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
    private JLabel lblbCreditoDecena;
    private JLabel lblbCreditoUnidad;

    private JButton btnJugar;
    private JButton btnRanking;

    ImageIcon imgBtnJugarIdle;
    ImageIcon imgBtnJugarClicked;
    ImageIcon imgBtnJugarHover;
    ImageIcon imgBtnRankingIdle;
    ImageIcon imgBtnRankingHover;
    ImageIcon imgBtnRankingClicked;

    /**
     * Constructor.
     * @param controlador controlador principal
     */
    public EspacioMenuPrincipal(Controlador controlador) {
        this.controlador = controlador;
        configurarEspacio();
        cargarElementos();
        cargarImagenesBotones();
        espacioJuego = new EspacioJuego(controlador);
        area = new Area(espacioJuego);
        configurarListeners();
    }

    public Area getArea() {
        return area;
    }

    /**
     * Reinicia el Area a su estado inicial (se usa cuando se cierra la ventana del juego).
     * Ahora: limpiamos la vista primero (EDT) y ejecutamos la reinicialización del modelo en un hilo de fondo
     * para evitar bloquear el EDT si algún Timer-thread aún tiene locks en curso.
     */
    public void reiniciarArea() {
        if (espacioJuego != null) {
            espacioJuego.limpiarSprites();
        }
        if (area != null) {
            new Thread(() -> area.reiniciar()).start();
        }
    }

    /**
     * Cierra (dispose) la ventana del juego si está abierta.
     */
    public void cerrarVentanaJuego() {
        if (ventanaJuego != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                ventanaJuego.dispose();
                ventanaJuego = null;
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ventanaJuego.dispose();
                        ventanaJuego = null;
                    }
                });
            }
        }
    }

    // El resto del archivo (cargarElementos, cargarImagenesBotones, configurarEspacio, configurarListeners, etc.)
    // permanece igual que antes; no se modificaron.
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

        lblbCreditoDecena = new JLabel();
        lblbCreditoDecena.setVisible(true);
        lblbCreditoDecena.setBounds(513, 592, 24, 28);
        java.net.URL creditoDecenaImgUrl = getClass().getResource("/Imagenes/Numeros/0.png");
        lblbCreditoDecena.setIcon(new ImageIcon(creditoDecenaImgUrl));
        this.add(lblbCreditoDecena);

        lblbCreditoUnidad = new JLabel();
        lblbCreditoUnidad.setVisible(true);
        lblbCreditoUnidad.setBounds(543, 592, 24, 28);
        java.net.URL creditoUnidadImgUrl = getClass().getResource("/Imagenes/Numeros/0.png");
        lblbCreditoUnidad.setIcon(new ImageIcon(creditoUnidadImgUrl));
        this.add(lblbCreditoUnidad);

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
                    area.setDificultad(seleccionada);
                    area.iniciar();
                    ventanaJuego = new VentanaJuego(espacioJuego, EspacioMenuPrincipal.this);
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
            }
        });

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("Saliendo...");
                    System.exit(0);
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