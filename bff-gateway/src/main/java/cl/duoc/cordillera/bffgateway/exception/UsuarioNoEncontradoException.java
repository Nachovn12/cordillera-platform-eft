package cl.duoc.cordillera.bffgateway.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String id) {
        super("No existe un usuario con id: " + id);
    }
}
