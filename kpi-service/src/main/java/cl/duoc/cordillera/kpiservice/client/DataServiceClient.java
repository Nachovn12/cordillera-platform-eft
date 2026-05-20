package cl.duoc.cordillera.kpiservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class DataServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.data.url}")
    private String dataServiceUrl;

    public DataServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "dataService", fallbackMethod = "getDataFallback")
    public Map<String, Object> getData(String endpoint) {
        String url = dataServiceUrl + endpoint;
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getDataFallback(String endpoint, Throwable ex) {
        return Collections.singletonMap("error", 
            "Data Service no disponible. Usando datos en cache.");
    }
}