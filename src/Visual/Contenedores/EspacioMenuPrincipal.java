package Visual.Contenedores;
import javax.swing.*;
import java.awt.*;

public class EspacioMenuPrincipal extends JPanel {
    private JLabel lblNave;
    private JLabel lblBateria;
    private JLabel lblLogo;
    private JLabel lblSubtitulo;
    private JLabel lblCreditos;
    private JLabel lblbCreditoDecena;
    private JLabel lblbCreditoUnidad;
    private JButton btnJugar;
    private JButton btnRanking;

    public EspacioMenuPrincipal() {
        this.setBounds(0, 0, 800, 900);
        this.setFocusable(true);
        configurarEspacio();
        cargarElementos();
    }

    private void cargarElementos(){

        // -----------------------------
        // Labels
        // -----------------------------

        // ----------------------------- Logo
        lblLogo = new JLabel();
        lblLogo.setVisible(true);
        lblLogo.setBounds(278, 150, 244, 85);
        java.net.URL logoImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/logo.png");
        lblLogo.setIcon(new ImageIcon(logoImgUrl));
        this.add(lblLogo);

        // ----------------------------- Nave
        lblNave = new JLabel();
        lblNave.setVisible(true);
        lblNave.setBounds(490, 130, 50, 50);
        java.net.URL naveImgUrl = getClass().getResource("/Imagenes/Partida/Nave.png");
        lblNave.setIcon(new ImageIcon(naveImgUrl));
        this.add(lblNave);

        // ----------------------------- Batería
        lblBateria = new JLabel();
        lblBateria.setVisible(true);
        lblBateria.setBounds(250, 200, 50, 38);
        java.net.URL bateriaImgUrl = getClass().getResource("/Imagenes/Partida/Bateria.png");
        lblBateria.setIcon(new ImageIcon(bateriaImgUrl));
        this.add(lblBateria);

        // ----------------------------- Subtítulo
        lblSubtitulo = new JLabel();
        lblSubtitulo.setVisible(true);
        lblSubtitulo.setBounds(260, 260, 282, 16);
        java.net.URL subtituloImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/subtitulo.png");
        lblSubtitulo.setIcon(new ImageIcon(subtituloImgUrl));
        this.add(lblSubtitulo);

        // ----------------------------- Créditos
        lblCreditos = new JLabel();
        lblCreditos.setVisible(true);
        lblCreditos.setBounds(233, 580, 256, 40);
        java.net.URL creditosImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/creditos.png");
        lblCreditos.setIcon(new ImageIcon(creditosImgUrl));
        this.add(lblCreditos);

        // ----------------------------- CreditoDecena
        lblbCreditoDecena = new JLabel();
        lblbCreditoDecena.setVisible(true);
        lblbCreditoDecena.setBounds(513, 592, 24, 28);
        java.net.URL creditoDecenaImgUrl = getClass().getResource("/Imagenes/Numeros/0.png");
        lblbCreditoDecena.setIcon(new ImageIcon(creditoDecenaImgUrl));
        this.add(lblbCreditoDecena);

        // ----------------------------- CreditoUnidad
        lblbCreditoUnidad = new JLabel();
        lblbCreditoUnidad.setVisible(true);
        lblbCreditoUnidad.setBounds(543, 592, 24, 28);
        java.net.URL creditoUnidadImgUrl = getClass().getResource("/Imagenes/Numeros/0.png");
        lblbCreditoUnidad.setIcon(new ImageIcon(creditoUnidadImgUrl));
        this.add(lblbCreditoUnidad);

        // -----------------------------
        // Botones
        // -----------------------------

        // ----------------------------- Jugar
        btnJugar = new JButton();
        btnJugar.setVisible(true);
        btnJugar.setBounds(120, 690, 236, 60);
        java.net.URL jugarImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/botonJugarIdle.png");
        btnJugar.setIcon(new ImageIcon(jugarImgUrl));
        btnJugar.setBackground(Color.black);
        this.add(btnJugar);

        // ----------------------------- Ranking
        btnRanking = new JButton();
        btnRanking.setVisible(true);
        btnRanking.setBounds(444, 690, 236, 60);
        java.net.URL rankingImgUrl = getClass().getResource("/Imagenes/MenuPrincipal/botonRankingIdle.png");
        btnRanking.setIcon(new ImageIcon(rankingImgUrl));
        btnRanking.setBackground(Color.black);
        this.add(btnRanking);
    }

    private void configurarEspacio(){
        this.setBackground(Color.BLACK);
        this.setLayout(null);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.requestFocusInWindow();
    }
}