package cl.duoc.cordillera.bffgateway.controller;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import cl.duoc.cordillera.bffgateway.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTest {

    private DashboardService dashboardService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        dashboardService = mock(DashboardService.class);
        DashboardController controller = new DashboardController(dashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private DashboardResponse crearDashboardResponse() {
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

    @Test
    void getStatsDebeRetornarOk() throws Exception {
        when(dashboardService.getDashboard()).thenReturn(crearDashboardResponse());

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Operativo"));

        verify(dashboardService).getDashboard();
    }

    @Test
    void getKpisDebeRetornarOk() throws Exception {
        DashboardResponse kpisResponse = new DashboardResponse(
                "Operativo", BigDecimal.ZERO, Collections.emptyList(), Collections.emptyList()
        );
        when(dashboardService.getDashboardKpis()).thenReturn(kpisResponse);

        mockMvc.perform(get("/api/dashboard/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBff").value("Operativo"));

        verify(dashboardService).getDashboardKpis();
    }

    @Test
    void getSucursalDebeRetornarOk() throws Exception {
        DashboardResponse response = new DashboardResponse(
                "Operativo", BigDecimal.ZERO, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
        );
        when(dashboardService.getDashboardSucursal(1L)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/sucursal/1"))
                .andExpect(status().isOk());

        verify(dashboardService).getDashboardSucursal(1L);
    }

    @Test
    void getAlertasDebeRetornarOk() throws Exception {
        Map<String, Object> alertas = Map.of(
                "alertas", List.of(),
                "historial", List.of(),
                "heatmap", List.of()
        );
        when(dashboardService.getAlertas()).thenReturn(alertas);

        mockMvc.perform(get("/api/dashboard/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertas").isArray());

        verify(dashboardService).getAlertas();
    }

    @Test
    void getServicesDebeRetornarOk() throws Exception {
        Map<String, Object> services = Map.of(
                "servicios", List.of(),
                "incidentes", List.of()
        );
        when(dashboardService.getServices()).thenReturn(services);

        mockMvc.perform(get("/api/dashboard/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicios").isArray());

        verify(dashboardService).getServices();
    }
}
