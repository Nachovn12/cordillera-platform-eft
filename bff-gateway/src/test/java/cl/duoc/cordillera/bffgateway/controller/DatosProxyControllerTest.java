package cl.duoc.cordillera.bffgateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Proxy BFF → Data Service.
 * Patrón AAA. RestTemplate mockeado con Mockito; @Value inyectado con ReflectionTestUtils.
 */
@ExtendWith(MockitoExtension.class)
class DatosProxyControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DatosProxyController datosProxyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(datosProxyController, "dataServiceUrl", "http://localhost:8083");
        mockMvc = MockMvcBuilders.standaloneSetup(datosProxyController).build();
        objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------
    // GET /api/v1/datos
    // -------------------------------------------------------

    @Test
    void listarTodos_debeRetornarOkConListaDeDatos() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1, "sistemaOrigen", "POS"))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/datos"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/datos"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }

    // -------------------------------------------------------
    // GET /api/v1/datos/{id}
    // -------------------------------------------------------

    @Test
    void buscarPorId_debeRetornarOkConElDato() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "sistemaOrigen", "CRM")));

        // Act & Assert
        mockMvc.perform(get("/api/v1/datos/1"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/datos/1"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }

    // -------------------------------------------------------
    // POST /api/v1/datos
    // -------------------------------------------------------

    @Test
    void crear_debeRetornarRespuestaDelDataService() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("sistemaOrigen", "ERP", "tipoDato", "STOCK", "valor", "500", "sucursalId", 1);
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.status(201).body(Map.of("id", 1)));

        // Act & Assert
        mockMvc.perform(post("/api/v1/datos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(restTemplate).postForEntity(contains("/api/datos"), any(), eq(Object.class));
    }

    // -------------------------------------------------------
    // PUT /api/v1/datos/{id}
    // -------------------------------------------------------

    @Test
    void actualizar_debeRetornarOkConDatosActualizados() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("valor", "200000");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "valor", "200000")));

        // Act & Assert
        mockMvc.perform(put("/api/v1/datos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/datos/1"), eq(HttpMethod.PUT), any(), eq(Object.class));
    }

    // -------------------------------------------------------
    // DELETE /api/v1/datos/{id}
    // -------------------------------------------------------

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // Arrange
        doNothing().when(restTemplate).delete(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/datos/1"))
                .andExpect(status().isNoContent());

        verify(restTemplate).delete(contains("/api/datos/1"));
    }

    // -------------------------------------------------------
    // GET /api/v1/datos/sistema/{origen}
    // -------------------------------------------------------

    @Test
    void buscarPorSistema_debeRetornarDatosFiltrados() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("sistemaOrigen", "POS"))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/datos/sistema/POS"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/sistema/POS"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }

    // -------------------------------------------------------
    // GET /api/v1/datos/sucursal/{id}
    // -------------------------------------------------------

    @Test
    void buscarPorSucursal_debeRetornarDatosDeLaSucursal() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("sucursalId", 1))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/datos/sucursal/1"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/sucursal/1"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }
}
