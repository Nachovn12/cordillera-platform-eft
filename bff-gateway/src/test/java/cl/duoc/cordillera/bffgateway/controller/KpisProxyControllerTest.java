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
 * Proxy BFF → KPI Service.
 * Patrón AAA. RestTemplate mockeado con Mockito; @Value inyectado con ReflectionTestUtils.
 */
@ExtendWith(MockitoExtension.class)
class KpisProxyControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KpisProxyController kpisProxyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kpisProxyController, "kpiServiceUrl", "http://localhost:8084");
        mockMvc = MockMvcBuilders.standaloneSetup(kpisProxyController).build();
        objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------
    // GET /api/v1/kpis
    // -------------------------------------------------------

    @Test
    void listarTodos_debeRetornarOkConListaDeKpis() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1, "nombre", "Ventas Q1"))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/kpis"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/kpis"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }

    // -------------------------------------------------------
    // GET /api/v1/kpis/{id}
    // -------------------------------------------------------

    @Test
    void buscarPorId_debeRetornarOkCuandoExiste() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "nombre", "Ventas Q1")));

        // Act & Assert
        mockMvc.perform(get("/api/v1/kpis/1"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/kpis/1"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }

    // -------------------------------------------------------
    // POST /api/v1/kpis
    // -------------------------------------------------------

    @Test
    void crear_debeRetornarRespuestaDelKpiService() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("nombre", "Ventas Q2", "categoria", "ventas");
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.status(201).body(Map.of("id", 2)));

        // Act & Assert
        mockMvc.perform(post("/api/v1/kpis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(restTemplate).postForEntity(contains("/api/kpis"), any(), eq(Object.class));
    }

    // -------------------------------------------------------
    // PUT /api/v1/kpis/{id}
    // -------------------------------------------------------

    @Test
    void actualizar_debeRetornarOkConDatosActualizados() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("nombre", "Ventas Q2 Actualizado");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "nombre", "Ventas Q2 Actualizado")));

        // Act & Assert
        mockMvc.perform(put("/api/v1/kpis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/kpis/1"), eq(HttpMethod.PUT), any(), eq(Object.class));
    }

    // -------------------------------------------------------
    // DELETE /api/v1/kpis/{id}
    // -------------------------------------------------------

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // Arrange
        doNothing().when(restTemplate).delete(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/kpis/1"))
                .andExpect(status().isNoContent());

        verify(restTemplate).delete(contains("/api/kpis/1"));
    }

    // -------------------------------------------------------
    // GET /api/v1/kpis/categoria/{cat}
    // -------------------------------------------------------

    @Test
    void buscarPorCategoria_debeRetornarKpisDeLaCategoria() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("categoria", "ventas"))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/kpis/categoria/ventas"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/categoria/ventas"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }
}
