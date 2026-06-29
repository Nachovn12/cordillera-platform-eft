package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula la tasa de uso del inventario disponible.
 * Fórmula: (stock utilizado / stock total) * 100
 * Unidad: unidades (porcentaje de utilización del stock)
 */
public class InventarioCalculator implements KpiCalculator {

    @Override
    public BigDecimal calcular(BigDecimal stockUtilizado, BigDecimal stockTotal) {
        if (stockTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return stockUtilizado.divide(stockTotal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getUnidad() {
        return "unidades";
    }
}