package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula el margen de rentabilidad operacional.
 * Fórmula: (utilidad operacional / ingresos totales) * 100
 * Unidad: porcentaje
 */
public class RentabilidadCalculator implements KpiCalculator {

    @Override
    public BigDecimal calcular(BigDecimal utilidadOperacional, BigDecimal ingresosTotales) {
        if (ingresosTotales.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return utilidadOperacional.divide(ingresosTotales, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getUnidad() {
        return "porcentaje";
    }
}