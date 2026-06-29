package cl.duoc.cordillera.bffgateway.controller;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import cl.duoc.cordillera.bffgateway.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Capa Controller — pruebas de endpoints del Dashboard BFF con MockMvc (standaloneSetup).
 * Patrón AAA. DashboardService mockeado con Mockito para aislar la capa web.
 */
@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    // -------------------------------------------------------
    // GET /api/dashboard/stats
    // -------------------------------------------------------

    @Test
    void getStats_cuandoTodoOperativo_debeRetornarOkConStatusOperativo() throws Exception {
        // Arrange
        when(dashboardService.getDashboard()).thenReturn(dashboardOperativo());

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Operativo"))
                .andExpect(jsonPath("$.ventasTotales").value(500000));

        verify(dashboardService).getDashboard();
    }

    @Test
    void getStats_cuandoStatusEsDegradado_debeRetornarOkConStatusDegradado() throws Exception {
        // Arrange
        DashboardResponse degradado = new DashboardResponse(
                "Degradado", BigDecimal.ZERO,
                Collections.emptyList(),
                List.of(Map.of("id", "kpi-service-down", "severidad", "Critica"))
        );
        when(dashboardService.getDashboard()).thenReturn(degradado);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Degradado"))
                .andExpect(jsonPath("$.alertas[0].id").value("kpi-service-down"));

        verify(dashboardService).getDashboard();
    }

    // -------------------------------------------------------
    // GET /api/dashboard/kpis
    // -------------------------------------------------------

    @Test
    void getKpis_debeRetornarOkConKpisDelServicio() throws Exception {
        // Arrange
        DashboardResponse kpisResponse = new DashboardResponse(
                "Operativo", BigDecimal.ZERO, Collections.emptyList(), Collections.emptyList()
        );
        when(dashboardService.getDashboardKpis()).thenReturn(kpisResponse);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Operativo"))
                .andExpect(jsonPath("$.kpis").isArray());

        verify(dashboardService).getDashboardKpis();
    }

    // -------------------------------------------------------
    // GET /api/dashboard/sucursal/{id}
    // -------------------------------------------------------

    @Test
    void getSucursal_cuandoDatosExisten_debeRetornarOk() throws Exception {
        // Arrange
        DashboardResponse response = new DashboardResponse(
                "Operativo", BigDecimal.ZERO, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
        );
        when(dashboardService.getDashboardSucursal(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/sucursal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Operativo"));

        verify(dashboardService).getDashboardSucursal(1L);
    }

    @Test
    void getSucursal_cuandoServicioFalla_debeRetornarStatusDegradado() throws Exception {
        // Arrange
        DashboardResponse degradado = new DashboardResponse(
                "Degradado", BigDecimal.ZERO,
                Collections.emptyList(),
                List.of(Map.of("id", "data-service-down")),
                Collections.emptyList()
        );
        when(dashboardService.getDashboardSucursal(5L)).thenReturn(degradado);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/sucursal/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Degradado"));

        verify(dashboardService).getDashboardSucursal(5L);
    }

    // -------------------------------------------------------
    // GET /api/dashboard/alertas
    // -------------------------------------------------------

    @Test
    void getAlertas_debeRetornarMapaConClaveAlertas() throws Exception {
        // Arrange
        Map<String, Object> alertas = Map.of(
                "alertas", List.of(),
                "historial", List.of(),
                "heatmap", List.of()
        );
        when(dashboardService.getAlertas()).thenReturn(alertas);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertas").isArray())
                .andExpect(jsonPath("$.historial").isArray());

        verify(dashboardService).getAlertas();
    }

    // -------------------------------------------------------
    // GET /api/dashboard/services
    // -------------------------------------------------------

    @Test
    void getServices_debeRetornarMapaConClaveServicios() throws Exception {
        // Arrange
        Map<String, Object> services = Map.of(
                "servicios", List.of(),
                "incidentes", List.of()
        );
        when(dashboardService.getServices()).thenReturn(services);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicios").isArray())
                .andExpect(jsonPath("$.incidentes").isArray());

        verify(dashboardService).getServices();
    }

    // -------------------------------------------------------
    // CORD-124: tests requeridos por nombre específico
    // -------------------------------------------------------

    @Test
    void getStats_debeRetornar200() throws Exception {
        when(dashboardService.getDashboard()).thenReturn(dashboardOperativo());

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    void getStatsDegradado_debeRetornar200() throws Exception {
        DashboardResponse degradado = new DashboardResponse(
                "Degradado", BigDecimal.ZERO, Collections.emptyList(),
                List.of(Map.of("id", "kpi-service-down", "severidad", "Critica"))
        );
        when(dashboardService.getDashboard()).thenReturn(degradado);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Degradado"));
    }

    @Test
    void getSucursal_debeRetornar200() throws Exception {
        DashboardResponse response = new DashboardResponse(
                "Operativo", BigDecimal.ZERO, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
        );
        when(dashboardService.getDashboardSucursal(1L)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/sucursal/1"))
                .andExpect(status().isOk());
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------

    private DashboardResponse dashboardOperativo() {
        return new DashboardResponse(
                "Operativo",
                BigDecimal.valueOf(500000),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
