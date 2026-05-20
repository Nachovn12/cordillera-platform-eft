package cl.duoc.cordillera.kpiservice.service.calculator;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KpiFactory {

    private final Map<String, KpiCalculator> calculators;

    public KpiFactory() {
        calculators = new HashMap<>();
        calculators.put("ventas", new VentasCalculator());
        calculators.put("inventario", new InventarioCalculator());
        calculators.put("logistica", new LogisticaCalculator());
        calculators.put("rentabilidad", new RentabilidadCalculator());
    }

    public KpiCalculator obtenerCalculador(String categoria) {
        KpiCalculator calculator = calculators.get(categoria.toLowerCase());
        if (calculator == null) {
            throw new IllegalArgumentException("Categoría no soportada: " + categoria);
        }
        return calculator;
    }
}