package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula el porcentaje de cumplimiento de ventas respecto a la meta.
 * Fórmula: (ventas reales / meta de ventas) * 100
 * Unidad: porcentaje
 */
public class VentasCalculator implements KpiCalculator {

    @Override
    public BigDecimal calcular(BigDecimal ventasReales, BigDecimal metaVentas) {
        if (metaVentas.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return ventasReales.divide(metaVentas, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getUnidad() {
        return "porcentaje";
    }
}