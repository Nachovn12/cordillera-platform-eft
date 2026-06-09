package cl.duoc.cordillera.kpiservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas del DataServiceClient y su fallback del Circuit Breaker.
 *
 * El @CircuitBreaker (Resilience4j) no se activa en tests unitarios sin Spring.
 * Se testea el cuerpo del método directamente y el fallback por separado.
 */
@ExtendWith(MockitoExtension.class)
class DataServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DataServiceClient dataServiceClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dataServiceClient, "dataServiceUrl", "http://localhost:8083");
    }

    // -------------------------------------------------------
    // getData — camino normal
    // -------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    void getData_cuandoServicioResponde_debeRetornarElMapaDeRespuesta() {
        // Arrange
        Map<String, Object> respuesta = Map.of("ventas", 150000, "sucursal", "Santiago");
        ResponseEntity<Map<String, Object>> entity = ResponseEntity.ok(respuesta);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(entity);

        // Act
        Map<String, Object> resultado = dataServiceClient.getData("/api/datos");

        // Assert
        assertNotNull(resultado);
        assertEquals(150000, resultado.get("ventas"));
        verify(restTemplate).exchange(
                anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getData_cuandoServicioRetornaCuerpoNulo_debeRetornarMapaVacio() {
        // Arrange — ResponseEntity con body null
        ResponseEntity<Map<String, Object>> entity = ResponseEntity.ok(null);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(entity);

        // Act
        Map<String, Object> resultado = dataServiceClient.getData("/api/datos");

        // Assert
        assertNotNull(resultado, "No debe retornar null");
        assertTrue(resultado.isEmpty(), "Debe retornar mapa vacío cuando el body es null");
    }

    // -------------------------------------------------------
    // getDataFallback — Circuit Breaker degradación elegante
    // -------------------------------------------------------

    @Test
    void getDataFallback_debeRetornarMapaConMensajeDeError() {
        // Arrange — el Circuit Breaker abre e invoca el fallback
        RuntimeException causa = new RuntimeException("Data Service no disponible");

        // Act
        Map<String, Object> resultado = dataServiceClient.getDataFallback("/api/datos", causa);

        // Assert
        assertNotNull(resultado, "El fallback no debe retornar null");
        assertTrue(resultado.containsKey("error"), "El fallback debe contener clave 'error'");
        assertNotNull(resultado.get("error"), "El mensaje de error no debe ser null");
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getDataFallback_debeRetornarMensajeDeCacheParaCualquierEndpoint() {
        // Arrange
        RuntimeException causa = new RuntimeException("Connection refused");

        // Act
        Map<String, Object> resultado = dataServiceClient.getDataFallback("/api/datos/sistema/pos", causa);

        // Assert
        assertTrue(resultado.containsKey("error"));
        String mensaje = (String) resultado.get("error");
        assertFalse(mensaje.isBlank(), "El mensaje de error no debe estar vacío");
        verifyNoInteractions(restTemplate);
    }
}
