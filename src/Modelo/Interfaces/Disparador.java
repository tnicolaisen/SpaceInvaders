package Modelo.Interfaces;

import Modelo.Entidades.Proyectil;

public interface Disparador {
    /**
     * Dispara un Proyectil.
     * @return Proyectil disparado.
     */
    public Proyectil disparar();
}
