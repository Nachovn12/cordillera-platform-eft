package cl.duoc.cordillera.bffgateway.exception;

public class UsuarioYaExisteException extends RuntimeException {

    public UsuarioYaExisteException(String message) {
        super(message);
    }
}
