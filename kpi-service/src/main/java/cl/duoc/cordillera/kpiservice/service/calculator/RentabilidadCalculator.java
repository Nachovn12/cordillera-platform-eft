package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RentabilidadCalculator implements KpiCalculator {

    @Override
    public BigDecimal calcular(BigDecimal valorBase, BigDecimal meta) {
        if (meta.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valorBase.divide(meta, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getUnidad() {
        return "porcentaje";
    }
}