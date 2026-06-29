package cl.duoc.cordillera.bffgateway.service;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Capa Service — pruebas de lógica del DashboardService con Mockito.
 * Patrón AAA. RestTemplate mockeado para aislar las llamadas a microservicios.
 * Los @Value se inyectan con ReflectionTestUtils en @BeforeEach.
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dashboardService, "kpiServiceUrl",    "http://localhost:8084");
        ReflectionTestUtils.setField(dashboardService, "dataServiceUrl",   "http://localhost:8083");
        ReflectionTestUtils.setField(dashboardService, "reportServiceUrl", "http://localhost:8085");
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void mockEndpoint(String url) {
        ResponseEntity<List<?>> response = ResponseEntity.ok(Collections.emptyList());
        when(restTemplate.exchange(
                eq(url), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
    }

    @SuppressWarnings("unchecked")
    private void mockEndpointWithData(String url, List<?> data) {
        ResponseEntity<List<?>> response = ResponseEntity.ok(data);
        when(restTemplate.exchange(
                eq(url), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);
    }

    @SuppressWarnings("unchecked")
    private void mockEndpointFallando(String url) {
        when(restTemplate.exchange(
                eq(url), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Servicio no disponible"));
    }

    // -------------------------------------------------------
    // getDashboard
    // -------------------------------------------------------

    @Test
    void getDashboard_cuandoTodosMicroserviciosResponden_debeRetornarStatusOperativo() {
        // Arrange
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        DashboardResponse response = dashboardService.getDashboard();

        // Assert
        assertNotNull(response);
        assertEquals("Operativo", response.getStatusBff());
    }

    @Test
    void getDashboard_cuandoKpiServiceFalla_debeRetornarStatusDegradado() {
        // Arrange
        mockEndpointFallando("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        DashboardResponse response = dashboardService.getDashboard();

        // Assert
        assertNotNull(response);
        assertEquals("Degradado", response.getStatusBff());
        assertFalse(response.getAlertas().isEmpty(), "Debe haber al menos una alerta cuando KPI falla");
    }

    @Test
    void getDashboard_cuandoTodosLosMicroserviciosFallan_debeRetornarStatusDegradado() {
        // Arrange
        mockEndpointFallando("http://localhost:8084/api/kpis");
        mockEndpointFallando("http://localhost:8083/api/datos");
        mockEndpointFallando("http://localhost:8085/api/reportes");

        // Act
        DashboardResponse response = dashboardService.getDashboard();

        // Assert
        assertNotNull(response);
        assertEquals("Degradado", response.getStatusBff());
    }

    @Test
    void getDashboard_cuandoDataServiceTieneDatos_debeAgregarAlertaInformativa() {
        // Arrange
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpointWithData("http://localhost:8083/api/datos",
                List.of(Map.of("id", 1, "sistemaOrigen", "POS")));
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        DashboardResponse response = dashboardService.getDashboard();

        // Assert
        assertNotNull(response);
        assertEquals("Operativo", response.getStatusBff());
        assertFalse(response.getAlertas().isEmpty(), "Debe existir alerta informativa con datos disponibles");
    }

    // -------------------------------------------------------
    // getDashboardKpis
    // -------------------------------------------------------

    @Test
    void getDashboardKpis_cuandoKpiServiceResponde_debeRetornarKpis() {
        // Arrange
        mockEndpoint("http://localhost:8084/api/kpis");

        // Act
        DashboardResponse response = dashboardService.getDashboardKpis();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getKpis());
        assertEquals("Operativo", response.getStatusBff());
    }

    @Test
    void getDashboardKpis_cuandoKpiServiceFalla_debeRetornarDegradado() {
        // Arrange
        mockEndpointFallando("http://localhost:8084/api/kpis");

        // Act
        DashboardResponse response = dashboardService.getDashboardKpis();

        // Assert
        assertNotNull(response);
        assertEquals("Degradado", response.getStatusBff());
    }

    // -------------------------------------------------------
    // getDashboardSucursal
    // -------------------------------------------------------

    @Test
    void getDashboardSucursal_cuandoSucursalTieneDatos_debeRetornarOperativo() {
        // Arrange
        mockEndpointWithData("http://localhost:8083/api/datos/sucursal/1",
                List.of(Map.of("id", 1, "sistemaOrigen", "POS", "valor", "150000")));

        // Act
        DashboardResponse response = dashboardService.getDashboardSucursal(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Operativo", response.getStatusBff());
    }

    @Test
    void getDashboardSucursal_cuandoSucursalSinDatos_debeAgregarAlertaAdvertencia() {
        // Arrange — data service responde pero lista vacía
        mockEndpoint("http://localhost:8083/api/datos/sucursal/99");

        // Act
        DashboardResponse response = dashboardService.getDashboardSucursal(99L);

        // Assert
        assertNotNull(response);
        assertFalse(response.getAlertas().isEmpty(), "Debe haber alerta de sucursal sin datos");
    }

    @Test
    void getDashboardSucursal_cuandoDataServiceFalla_debeRetornarDegradado() {
        // Arrange
        mockEndpointFallando("http://localhost:8083/api/datos/sucursal/2");

        // Act
        DashboardResponse response = dashboardService.getDashboardSucursal(2L);

        // Assert
        assertNotNull(response);
        assertEquals("Degradado", response.getStatusBff());
    }

    // -------------------------------------------------------
    // getAlertas
    // -------------------------------------------------------

    @Test
    void getAlertas_cuandoTodosOperativos_debeRetornarAlertaInformativaBff() {
        // Arrange
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        Map<String, Object> alertas = dashboardService.getAlertas();

        // Assert
        assertNotNull(alertas);
        assertNotNull(alertas.get("alertas"));
        List<?> alertasList = (List<?>) alertas.get("alertas");
        assertFalse(alertasList.isEmpty(), "Debe existir alerta informativa cuando todo está OK");
    }

    @Test
    void getAlertas_cuandoKpiServiceFalla_debeContenerAlertaCritica() {
        // Arrange
        mockEndpointFallando("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        Map<String, Object> resultado = dashboardService.getAlertas();

        // Assert
        assertNotNull(resultado);
        List<?> alertasList = (List<?>) resultado.get("alertas");
        assertFalse(alertasList.isEmpty(), "Debe haber una alerta crítica cuando KPI falla");
    }

    // -------------------------------------------------------
    // getServices
    // -------------------------------------------------------

    @Test
    void getServices_cuandoTodosOperativos_debeRetornar4Servicios() {
        // Arrange
        mockEndpoint("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        Map<String, Object> services = dashboardService.getServices();

        // Assert
        assertNotNull(services);
        assertNotNull(services.get("servicios"));
        List<?> serviciosList = (List<?>) services.get("servicios");
        assertEquals(4, serviciosList.size(), "Debe incluir BFF + 3 microservicios");
    }

    @Test
    void getServices_cuandoDataServiceFalla_debeIncluirIncidente() {
        // Arrange
        mockEndpointFallando("http://localhost:8083/api/datos");
        mockEndpoint("http://localhost:8084/api/kpis");
        mockEndpoint("http://localhost:8085/api/reportes");

        // Act
        Map<String, Object> services = dashboardService.getServices();

        // Assert
        assertNotNull(services);
        List<?> incidentes = (List<?>) services.get("incidentes");
        assertFalse(incidentes.isEmpty(), "Debe haber incidente cuando Data Service falla");
    }
}
