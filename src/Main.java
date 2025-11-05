import Controlador.Controlador;
import Visual.Contenedores.EspacioMenuPrincipal;
import Visual.Ventanas.VentanaMenuPrincipal;

public class Main {
    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        EspacioMenuPrincipal panel = new EspacioMenuPrincipal(controlador);
        new VentanaMenuPrincipal(panel);
    }
}