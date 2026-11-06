package cl.duoc.pacientes_service.exception;

public class RutExistenteException extends RuntimeException {
    public RutExistenteException(String message) {
        super(message);
    }
}