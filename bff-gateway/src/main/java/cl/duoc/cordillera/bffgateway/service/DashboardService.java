package cl.duoc.cordillera.bffgateway.service;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class DashboardService {

    private final RestTemplate restTemplate;

    @Value("${services.kpi.url}")
    private String kpiServiceUrl;

    @Value("${services.data.url}")
    private String dataServiceUrl;

    public DashboardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DashboardResponse getDashboard() {
        List<?> kpis = fetchKpis();
        List<?> datos = fetchDatos();
        return new DashboardResponse(
                "Operativo",
                BigDecimal.ZERO,
                kpis,
                datos.isEmpty()
                        ? Collections.emptyList()
                        : List.of("Datos operacionales disponibles desde Data Service")
        );
    }

    public DashboardResponse getDashboardKpis() {
        List<?> kpis = fetchKpis();
        return new DashboardResponse("Operativo", null, kpis, Collections.emptyList());
    }

    public DashboardResponse getDashboardSucursal(Long id) {
        List<?> datosSucursal = fetchDatosPorSucursal(id);
        return new DashboardResponse(
                "Operativo",
                BigDecimal.ZERO,
                Collections.emptyList(),
                datosSucursal.isEmpty()
                        ? List.of("No existen datos para la sucursal " + id)
                        : List.of("Datos de sucursal " + id + " obtenidos desde Data Service"),
                datosSucursal
        );
    }

    private List<?> fetchKpis() {
        try {
            String url = kpiServiceUrl + "/api/kpis";
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<?> fetchDatos() {
        try {
            String url = dataServiceUrl + "/api/datos";
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<?> fetchDatosPorSucursal(Long id) {
        try {
            String url = dataServiceUrl + "/api/datos/sucursal/" + id;
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
