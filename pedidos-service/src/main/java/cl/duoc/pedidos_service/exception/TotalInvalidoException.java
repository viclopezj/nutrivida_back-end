package cl.duoc.pedidos_service.exception;

public class TotalInvalidoException extends RuntimeException {
    public TotalInvalidoException(String message) {
        super(message);
    }
}