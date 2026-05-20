package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;
import java.util.List;

public interface KpiCalculator {
    BigDecimal calcular(BigDecimal valorBase, BigDecimal meta);
    String getUnidad();
}