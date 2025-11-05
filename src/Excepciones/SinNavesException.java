package Excepciones;

/**
 * Excepción lanzada cuando no hay naves disponibles para realizar una acción
 * (ej: disparar desde la oleada).
 */
public class SinNavesException extends RuntimeException {
    public SinNavesException(String message) { super(message); }
}