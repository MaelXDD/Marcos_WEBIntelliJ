package utp.phantom.phantom.exception;

/** Fallo de infraestructura al hablar con Supabase Storage (red, credenciales, bucket). */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
