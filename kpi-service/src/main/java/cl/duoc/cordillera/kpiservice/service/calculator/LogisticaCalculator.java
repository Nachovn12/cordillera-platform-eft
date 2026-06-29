package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula el porcentaje de entregas completadas a tiempo.
 * Fórmula: (entregas a tiempo / total entregas) * 100
 * Unidad: entregas (porcentaje de cumplimiento logístico)
 */
public class LogisticaCalculator implements KpiCalculator {

    @Override
    public BigDecimal calcular(BigDecimal entregasATiempo, BigDecimal totalEntregas) {
        if (totalEntregas.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return entregasATiempo.divide(totalEntregas, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getUnidad() {
        return "entregas";
    }
}