package Excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a una nave en una posición inexistente
 * dentro de la matriz de la Oleada.
 */
public class IndiceNaveFueraDeRangoException extends RuntimeException {
    public IndiceNaveFueraDeRangoException(String message) { super(message); }
}