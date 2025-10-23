package Modelo.Interfaces;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.TiposEntidades;

public interface Observador {
    public void actualizarPosiciones(int id, Punto punto, Dimension dimension, TiposEntidades tipo, boolean inactivo);
}
