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
 * Proxy BFF → Report Service.
 * Patrón AAA. RestTemplate mockeado con Mockito; @Value inyectado con ReflectionTestUtils.
 */
@ExtendWith(MockitoExtension.class)
class ReportesProxyControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReportesProxyController reportesProxyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reportesProxyController, "reportServiceUrl", "http://localhost:8085");
        mockMvc = MockMvcBuilders.standaloneSetup(reportesProxyController).build();
        objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------
    // GET /api/v1/reportes
    // -------------------------------------------------------

    @Test
    void listarTodos_debeRetornarOkConListaDeReportes() throws Exception {
        // Arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1, "titulo", "Reporte Q1"))));

        // Act & Assert
        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/api/reportes"), eq(HttpMethod.GET), isNull(), eq(Object.class));
    }

    // -------------------------------------------------------
    // POST /api/v1/reportes
    // -------------------------------------------------------

    @Test
    void crear_debeRetornarRespuestaDelReportService() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("titulo", "Reporte Ventas", "tipo", "VENTAS");
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.status(201).body(Map.of("id", 1)));

        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(restTemplate).postForEntity(contains("/api/reportes"), any(), eq(Object.class));
    }

    // -------------------------------------------------------
    // POST /api/v1/reportes/generar
    // -------------------------------------------------------

    @Test
    void generar_debeRetornarRespuestaGeneradaDelReportService() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("area", "Ventas", "periodo", "2026-05");
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "estado", "GENERADO")));

        // Act & Assert
        mockMvc.perform(post("/api/v1/reportes/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(restTemplate).postForEntity(contains("/api/reportes/generar"), any(), eq(Object.class));
    }

    // -------------------------------------------------------
    // GET /api/v1/reportes/{id}/exportar
    // -------------------------------------------------------

    @Test
    void exportar_cuandoFormatoEsPdf_debeRetornarOkConBytes() throws Exception {
        // Arrange
        byte[] pdfBytes = "PDF-CONTENT".getBytes();
        ResponseEntity<byte[]> pdfResponse = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(byte[].class)))
                .thenReturn(pdfResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reportes/1/exportar")
                        .param("formato", "pdf"))
                .andExpect(status().isOk());

        verify(restTemplate).exchange(contains("/exportar"), eq(HttpMethod.GET), isNull(), eq(byte[].class));
    }
}
