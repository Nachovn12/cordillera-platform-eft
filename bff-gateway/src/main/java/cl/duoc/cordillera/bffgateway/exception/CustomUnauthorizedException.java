package cl.duoc.cordillera.bffgateway.exception;

public class CustomUnauthorizedException extends RuntimeException {

    public CustomUnauthorizedException(String message) {
        super(message);
    }
}
