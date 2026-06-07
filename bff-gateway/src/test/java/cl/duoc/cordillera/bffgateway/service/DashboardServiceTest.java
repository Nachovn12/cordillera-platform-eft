package cl.duoc.cordillera.bffgateway.service;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private RestTemplate restTemplate;
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        dashboardService = new DashboardService(restTemplate);
        // Inyecta las URLs de microservicios mediante ReflectionTestUtils
        ReflectionTestUtils.setField(dashboardService, "kpiServiceUrl",    "http://localhost:8084");
        ReflectionTestUtils.setField(dashboardService, "dataServiceUrl",   "http://localhost:8083");
        ReflectionTestUtils.setField(dashboardService, "reportServiceUrl", "http://localhost:8085");
    }

    @SuppressWarnings("unchecked")
    private void mockEndpoint(String url) {
        ResponseEntity<List<?>> response = ResponseEntity.ok(Collections.emptyList());
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
    }

    @Test
    void getDashboard_debeRetornarStatusOperativo() {
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        DashboardResponse response = dashboardService.getDashboard();

        assertNotNull(response);
        assertEquals("Operativo", response.getStatusBff());
    }

    @Test
    void getDashboard_cuandoMicroservicioFalla_debeRetornarDegradado() {
        // KPI falla, los demás responden
        when(restTemplate.exchange(
                eq("http://localhost:8084/api/kpis"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("KPI Service no disponible"));

        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        DashboardResponse response = dashboardService.getDashboard();

        assertNotNull(response);
        assertEquals("Degradado", response.getStatusBff());
    }

    @Test
    void getDashboardKpis_debeRetornarKpis() {
        mockEndpoint("http://localhost:8084/api/kpis");

        DashboardResponse response = dashboardService.getDashboardKpis();

        assertNotNull(response);
        assertNotNull(response.getKpis());
    }

    @Test
    void getAlertas_cuandoTodosOperativos_debeRetornarAlertaInformativa() {
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        Map<String, Object> alertas = dashboardService.getAlertas();

        assertNotNull(alertas);
        assertNotNull(alertas.get("alertas"));
    }

    @Test
    void getServices_debeRetornarListaDeServicios() {
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8085/api/reportes");

        Map<String, Object> services = dashboardService.getServices();

        assertNotNull(services);
        assertNotNull(services.get("servicios"));
    }

    @Test
    void getDashboardSucursal_debeRetornarDatosDeSucursal() {
        ResponseEntity<List<?>> response = ResponseEntity.ok(Collections.emptyList());
        when(restTemplate.exchange(
                eq("http://localhost:8083/api/datos/sucursal/1"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        DashboardResponse result = dashboardService.getDashboardSucursal(1L);

        assertNotNull(result);
    }
}
