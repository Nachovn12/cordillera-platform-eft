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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final RestTemplate restTemplate;

    @Value("${services.kpi.url}")
    private String kpiServiceUrl;

    @Value("${services.data.url}")
    private String dataServiceUrl;

    @Value("${services.report.url}")
    private String reportServiceUrl;

    public DashboardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DashboardResponse getDashboard() {
        FetchResult kpiResult = fetchList(kpiServiceUrl + "/api/kpis", "KPI Service");
        FetchResult dataResult = fetchList(dataServiceUrl + "/api/datos", "Data Service");
        FetchResult reportResult = fetchList(reportServiceUrl + "/api/reportes", "Report Service");

        List<Map<String, Object>> alertas = new ArrayList<>();
        addAlertIfFailed(alertas, "kpi-service", "KPI Service", kpiResult);
        addAlertIfFailed(alertas, "data-service", "Data Service", dataResult);
        addAlertIfFailed(alertas, "report-service", "Report Service", reportResult);

        String status = alertas.isEmpty() ? "Operativo" : "Degradado";

        if (dataResult.success() && !dataResult.data().isEmpty()) {
            alertas.add(alert(
                    "datos-disponibles",
                    "Datos operacionales disponibles",
                    "Data Service entrego informacion operacional para el dashboard.",
                    "Datos",
                    "Data Service",
                    "Informativa",
                    "Resuelta"
            ));
        }

        BigDecimal ventasTotales = extractVentasTotales(kpiResult.data());

        return new DashboardResponse(
                status,
                ventasTotales,
                kpiResult.data(),
                alertas,
                Collections.emptyList(),
                buildSalesTrend(ventasTotales),
                reportResult.data().stream().limit(3).toList(),
                buildDashboardServices(kpiResult, dataResult, reportResult)
        );
    }

    public DashboardResponse getDashboardKpis() {
        FetchResult kpiResult = fetchList(kpiServiceUrl + "/api/kpis", "KPI Service");

        List<Map<String, Object>> alertas = new ArrayList<>();
        addAlertIfFailed(alertas, "kpi-service", "KPI Service", kpiResult);

        String status = alertas.isEmpty() ? "Operativo" : "Degradado";

        return new DashboardResponse(status, BigDecimal.ZERO, kpiResult.data(), alertas);
    }

    public DashboardResponse getDashboardSucursal(Long id) {
        FetchResult kpiResult = fetchList(kpiServiceUrl + "/api/kpis", "KPI Service");
        FetchResult dataResult = fetchList(dataServiceUrl + "/api/datos/sucursal/" + id, "Data Service");
        FetchResult reportResult = fetchList(reportServiceUrl + "/api/reportes", "Report Service");

        List<Map<String, Object>> alertas = new ArrayList<>();
        addAlertIfFailed(alertas, "kpi-service", "KPI Service", kpiResult);
        addAlertIfFailed(alertas, "data-service", "Data Service", dataResult);
        addAlertIfFailed(alertas, "report-service", "Report Service", reportResult);

        if (dataResult.success()) {
            if (dataResult.data().isEmpty()) {
                alertas.add(alert(
                        "sucursal-" + id + "-sin-datos",
                        "Sucursal sin datos",
                        "No existen datos para la sucursal " + id,
                        "Datos",
                        "Data Service",
                        "Advertencia",
                        "Activa"
                ));
            } else {
                alertas.add(alert(
                        "sucursal-" + id + "-ok",
                        "Datos de sucursal disponibles",
                        "Datos de sucursal " + id + " obtenidos desde Data Service",
                        "Datos",
                        "Data Service",
                        "Informativa",
                        "Resuelta"
                ));
            }
        }

        String status = (kpiResult.success() && dataResult.success() && reportResult.success()) ? "Operativo" : "Degradado";
        BigDecimal ventasTotales = extractVentasFromDatos(dataResult.data());

        return new DashboardResponse(
                status,
                ventasTotales,
                kpiResult.data(),
                alertas,
                dataResult.data(),
                buildSalesTrend(ventasTotales),
                reportResult.data().stream().limit(3).toList(),
                buildDashboardServices(kpiResult, dataResult, reportResult)
        );
    }

    public Map<String, Object> getAlertas() {
        FetchResult kpiResult = fetchList(kpiServiceUrl + "/api/kpis", "KPI Service");
        FetchResult dataResult = fetchList(dataServiceUrl + "/api/datos", "Data Service");
        FetchResult reportResult = fetchList(reportServiceUrl + "/api/reportes", "Report Service");

        List<Map<String, Object>> alertas = new ArrayList<>();

        addServiceAlertIfFailed(alertas, "kpi-service", "KPI Service", kpiResult);
        addServiceAlertIfFailed(alertas, "data-service", "Data Service", dataResult);
        addServiceAlertIfFailed(alertas, "report-service", "Report Service", reportResult);

        if (alertas.isEmpty()) {
            alertas.add(alert(
                    "bff-ok",
                    "Flujo BFF operativo",
                    "Frontend, BFF Gateway y microservicios principales responden correctamente.",
                    "Servicios",
                    "BFF Gateway",
                    "Informativa",
                    "Resuelta"
            ));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("alertas", alertas);
        response.put("historial", alertas);
        response.put("heatmap", Collections.emptyList());
        return response;
    }

    public Map<String, Object> getServices() {
        List<Map<String, Object>> services = new ArrayList<>();

        services.add(serviceStatus("bff-gateway", "BFF Gateway", "Operativo",
                "Punto de entrada del frontend", 12, 100));

        addRemoteServiceStatus(services, "data-service", "Data Service",
                "Datos operacionales", dataServiceUrl + "/api/datos");
        addRemoteServiceStatus(services, "kpi-service", "KPI Service",
                "Indicadores ejecutivos", kpiServiceUrl + "/api/kpis");
        addRemoteServiceStatus(services, "report-service", "Report Service",
                "Reportes ejecutivos", reportServiceUrl + "/api/reportes");

        List<Map<String, Object>> incidents = services.stream()
                .filter(service -> !"Operativo".equals(service.get("estado")))
                .map(service -> incident(
                        String.valueOf(service.get("id")) + "-incident",
                        service.get("nombre") + " no disponible",
                        String.valueOf(service.get("descripcion")),
                        "Mayor",
                        "Activa"
                ))
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("servicios", services);
        response.put("incidentes", incidents);
        response.put("eventos", List.of(event("bff-check", "Verificacion de servicios", "Consulta ejecutada desde BFF Gateway", "Operativo")));
        response.put("historialDisponibilidad", Collections.emptyList());
        return response;
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

    private void addAlertIfFailed(List<Map<String, Object>> alertas, String id, String name, FetchResult result) {
        if (!result.success()) {
            addServiceAlertIfFailed(alertas, id, name, result);
        }
    }

    private void addServiceAlertIfFailed(List<Map<String, Object>> alertas, String id, String name, FetchResult result) {
        if (!result.success()) {
            alertas.add(alert(
                    id + "-down",
                    name + " no disponible",
                    result.errorMessage(),
                    "Servicio",
                    name,
                    "Critica",
                    "Activa"
            ));
        }
    }

    private void addRemoteServiceStatus(List<Map<String, Object>> services, String id, String name, String description, String url) {
        long start = System.nanoTime();
        FetchResult result = fetchList(url, name);
        long latency = Math.max(1, (System.nanoTime() - start) / 1_000_000);

        services.add(serviceStatus(
                id,
                name,
                result.success() ? "Operativo" : "Error",
                result.success() ? description : result.errorMessage(),
                latency,
                result.success() ? 99 : 0
        ));
    }

    private Map<String, Object> alert(String id, String title, String description,
            String category, String origin, String severity, String status) {
        Map<String, Object> alert = new LinkedHashMap<>();
        alert.put("id", id);
        alert.put("titulo", title);
        alert.put("descripcion", description);
        alert.put("categoria", category);
        alert.put("origen", origin);
        alert.put("severidad", severity);
        alert.put("estado", status);
        alert.put("fechaDeteccion", java.time.LocalDateTime.now().toString());
        return alert;
    }

    private Map<String, Object> serviceStatus(String id, String name, String status,
            String description, long latencyMs, int uptime) {
        Map<String, Object> service = new LinkedHashMap<>();
        service.put("id", id);
        service.put("nombre", name);
        service.put("estado", status);
        service.put("descripcion", description);
        service.put("latenciaMs", latencyMs);
        service.put("disponibilidad", uptime);
        service.put("tendencia", List.of(95, 97, 99, uptime));
        return service;
    }

    private Map<String, Object> incident(String id, String title, String description, String severity, String status) {
        Map<String, Object> incident = new LinkedHashMap<>();
        incident.put("id", id);
        incident.put("titulo", title);
        incident.put("descripcion", description);
        incident.put("severidad", severity);
        incident.put("estado", status);
        incident.put("fecha", java.time.LocalDateTime.now().toString());
        return incident;
    }

    private Map<String, Object> event(String id, String title, String description, String status) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("id", id);
        event.put("titulo", title);
        event.put("descripcion", description);
        event.put("estado", status);
        event.put("fecha", java.time.LocalDateTime.now().toString());
        return event;
    }

    private BigDecimal extractVentasTotales(List<?> kpis) {
        return kpis.stream()
                .filter(item -> item instanceof Map<?, ?>)
                .map(this::toStringObjectMap)
                .filter(kpi -> {
                    String category = String.valueOf(kpi.getOrDefault("categoria", "")).toLowerCase();
                    String name = String.valueOf(kpi.getOrDefault("nombre", "")).toLowerCase();
                    return category.contains("venta") || name.contains("venta");
                })
                .map(kpi -> new BigDecimal(String.valueOf(kpi.getOrDefault("valor", "0"))))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal extractVentasFromDatos(List<?> datos) {
        return datos.stream()
                .filter(item -> item instanceof Map<?, ?>)
                .map(this::toStringObjectMap)
                .filter(dato -> {
                    String tipo = String.valueOf(dato.getOrDefault("tipoDato", "")).toUpperCase();
                    return "VENTA".equals(tipo) || "PEDIDO".equals(tipo);
                })
                .map(dato -> new BigDecimal(String.valueOf(dato.getOrDefault("valor", "0"))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, Object> toStringObjectMap(Object value) {
        Map<?, ?> source = (Map<?, ?>) value;
        Map<String, Object> typed = new LinkedHashMap<>();

        source.forEach((key, mapValue) -> {
            if (key != null) {
                typed.put(String.valueOf(key), mapValue);
            }
        });

        return typed;
    }

    private List<Map<String, Object>> buildSalesTrend(BigDecimal ventasTotales) {
        BigDecimal base = ventasTotales.compareTo(BigDecimal.ZERO) > 0
                ? ventasTotales
                : BigDecimal.valueOf(380000);
        String[] labels = {"Ene", "Feb", "Mar", "Abr", "May"};
        double[] factors = {0.72, 0.81, 0.88, 0.94, 1.0};

        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", labels[i]);
            item.put("valor", base.multiply(BigDecimal.valueOf(factors[i])).longValue());
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> buildDashboardServices(FetchResult kpiResult, FetchResult dataResult, FetchResult reportResult) {
        return List.of(
                serviceStatus("bff-gateway", "BFF Gateway", "Operativo", "Punto de entrada del frontend", 12, 100),
                serviceStatus("data-service", "Data Service", dataResult.success() ? "Operativo" : "Error",
                        dataResult.success() ? "Datos operacionales" : dataResult.errorMessage(), 24, dataResult.success() ? 99 : 0),
                serviceStatus("kpi-service", "KPI Service", kpiResult.success() ? "Operativo" : "Error",
                        kpiResult.success() ? "Indicadores ejecutivos" : kpiResult.errorMessage(), 31, kpiResult.success() ? 99 : 0),
                serviceStatus("report-service", "Report Service", reportResult.success() ? "Operativo" : "Error",
                        reportResult.success() ? "Reportes ejecutivos" : reportResult.errorMessage(), 36, reportResult.success() ? 99 : 0)
        );
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
