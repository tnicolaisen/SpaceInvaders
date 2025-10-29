package Modelo.Interfaces;
import Utilidades.Dimension;
import Utilidades.Punto;
import Utilidades.TiposEntidades;

public interface Observador {
    /**
     * Permite transmitir información entre el Modelo y la Visual de un objeto.
     * @param id ID del objeto a cambiar.
     * @param punto Posición en el espacio del objeto a cambiar.
     * @param dimension Ancho y alto del objeto a cambiar.
     * @param tipo Tipo de objeto a cambiar.
     * @param inactivo Si el objeto está inactivo o no.
     */
    public void actualizarPosiciones(int id, Punto punto, Dimension dimension, TiposEntidades tipo, boolean inactivo);

    /**
     * Indica a la vista que elimine el sprite asociado al id (por ejemplo cuando la entidad
     * fue removida del modelo). Esta llamada debe ejecutarse en el EDT.
     * @param id ID de la entidad eliminada.
     */
    public void eliminarEntidad(int id);
}