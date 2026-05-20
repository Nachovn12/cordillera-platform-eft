package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;

public interface KpiCalculator {
    BigDecimal calcular(BigDecimal valorBase, BigDecimal meta);
    String getUnidad();
}