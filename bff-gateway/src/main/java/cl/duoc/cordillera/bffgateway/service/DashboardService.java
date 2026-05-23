package cl.duoc.cordillera.bffgateway.service;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        FetchResult kpiResult = fetchList(kpiServiceUrl + "/api/kpis", "KPI Service");
        FetchResult dataResult = fetchList(dataServiceUrl + "/api/datos", "Data Service");

        List<String> alertas = new ArrayList<>();
        addAlertIfFailed(alertas, kpiResult);
        addAlertIfFailed(alertas, dataResult);

        String status = alertas.isEmpty() ? "Operativo" : "Degradado";

        if (dataResult.success() && !dataResult.data().isEmpty()) {
            alertas.add("Datos operacionales disponibles desde Data Service");
        }

        return new DashboardResponse(status, BigDecimal.ZERO, kpiResult.data(), alertas);
    }

    public DashboardResponse getDashboardKpis() {
        FetchResult kpiResult = fetchList(kpiServiceUrl + "/api/kpis", "KPI Service");

        List<String> alertas = new ArrayList<>();
        addAlertIfFailed(alertas, kpiResult);

        String status = alertas.isEmpty() ? "Operativo" : "Degradado";

        return new DashboardResponse(status, BigDecimal.ZERO, kpiResult.data(), alertas);
    }

    public DashboardResponse getDashboardSucursal(Long id) {
        FetchResult dataResult = fetchList(
                dataServiceUrl + "/api/datos/sucursal/" + id, "Data Service");

        List<String> alertas = new ArrayList<>();
        addAlertIfFailed(alertas, dataResult);

        if (dataResult.success()) {
            if (dataResult.data().isEmpty()) {
                alertas.add("No existen datos para la sucursal " + id);
            } else {
                alertas.add("Datos de sucursal " + id + " obtenidos desde Data Service");
            }
        }

        String status = dataResult.success() ? "Operativo" : "Degradado";

        return new DashboardResponse(
                status,
                BigDecimal.ZERO,
                Collections.emptyList(),
                alertas,
                dataResult.data()
        );
    }

    private FetchResult fetchList(String url, String serviceName) {
        try {
            ResponseEntity<List<?>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<?>>() {});
            List<?> body = response.getBody() != null ? response.getBody() : Collections.emptyList();
            return FetchResult.success(body);
        } catch (Exception e) {
            return FetchResult.failure("No fue posible obtener información desde " + serviceName);
        }
    }

    private void addAlertIfFailed(List<String> alertas, FetchResult result) {
        if (!result.success()) {
            alertas.add(result.errorMessage());
        }
    }

    private record FetchResult(boolean success, List<?> data, String errorMessage) {

        private static FetchResult success(List<?> data) {
            return new FetchResult(true, data, null);
        }

        private static FetchResult failure(String errorMessage) {
            return new FetchResult(false, Collections.emptyList(), errorMessage);
        }
    }
}