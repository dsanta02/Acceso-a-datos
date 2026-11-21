package gasolinera.persistence;

public class ErrorPersistencia extends RuntimeException {

    public ErrorPersistencia(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorPersistencia(String message) {
        super(message);
    }
}
