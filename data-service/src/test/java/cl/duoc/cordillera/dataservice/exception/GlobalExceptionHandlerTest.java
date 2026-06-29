package cl.duoc.cordillera.dataservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_retorna404() {
        NoSuchElementException ex = new NoSuchElementException("Dato no encontrado");
        ResponseEntity<Map<String, String>> res = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals("Dato no encontrado", res.getBody().get("error"));
    }

    @Test
    void handleValidation_retorna400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dato", "valor", "El valor no puede estar vacio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> res = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("El valor no puede estar vacio", res.getBody().get("error"));
    }
}
