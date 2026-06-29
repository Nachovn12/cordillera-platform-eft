package cl.duoc.cordillera.kpiservice.service.calculator;

import java.math.BigDecimal;

/**
 * Estrategia de cálculo de KPI — patrón Factory Method.
 *
 * Cada implementación define cómo calcular el valor de un indicador
 * y qué unidad lo representa. La {@link KpiFactory} es la encargada
 * de instanciar el calculador correcto según la categoría del KPI.
 */
public interface KpiCalculator {

    /**
     * Calcula el valor del KPI.
     *
     * @param valorBase  el valor real medido (ventas, stock, entregas, etc.)
     * @param referencia el valor de referencia o meta contra el que se calcula
     * @return           el valor calculado del indicador
     */
    BigDecimal calcular(BigDecimal valorBase, BigDecimal referencia);

    /**
     * @return la unidad de medida del resultado (porcentaje, unidades, entregas…)
     */
    String getUnidad();
}