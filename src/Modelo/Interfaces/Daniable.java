package Modelo.Interfaces;

public interface Daniable {
    /**
     * Reduce la vida de la Entidad.
     */
    public void serDaniado();

    /**
     * Devuelve la vida restante del objeto.
     * @return Vida restante.
     */
    public int getVida();
}
