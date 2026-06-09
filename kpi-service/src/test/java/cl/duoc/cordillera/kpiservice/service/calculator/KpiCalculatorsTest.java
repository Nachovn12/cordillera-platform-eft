package cl.duoc.cordillera.kpiservice.service.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de los 4 calculadores del patrón Factory Method.
 * Tests puros sin Spring — validan la lógica matemática y la unidad de cada calculador.
 */
class KpiCalculatorsTest {

    // -------------------------------------------------------
    // VentasCalculator
    // -------------------------------------------------------

    @Test
    void ventasCalculator_calcular_conValoresNormales_debeRetornarPorcentaje() {
        // Arrange
        VentasCalculator calc = new VentasCalculator();

        // Act
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(75), BigDecimal.valueOf(100));

        // Assert — (75 / 100) * 100 = 75.0000
        assertEquals(new BigDecimal("75.0000"), resultado);
    }

    @Test
    void ventasCalculator_calcular_cuandoMetaEsCero_debeRetornarCero() {
        // Arrange
        VentasCalculator calc = new VentasCalculator();

        // Act
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(50), BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void ventasCalculator_getUnidad_debeRetornarPorcentaje() {
        // Arrange & Act & Assert
        assertEquals("porcentaje", new VentasCalculator().getUnidad());
    }

    // -------------------------------------------------------
    // InventarioCalculator
    // -------------------------------------------------------

    @Test
    void inventarioCalculator_calcular_conValoresNormales_debeRetornarTasaDeUso() {
        // Arrange
        InventarioCalculator calc = new InventarioCalculator();

        // Act — (820 / 1000) * 100 = 82.0000
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(820), BigDecimal.valueOf(1000));

        // Assert
        assertEquals(new BigDecimal("82.0000"), resultado);
    }

    @Test
    void inventarioCalculator_calcular_cuandoMetaEsCero_debeRetornarCero() {
        // Arrange
        InventarioCalculator calc = new InventarioCalculator();

        // Act
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(100), BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void inventarioCalculator_getUnidad_debeRetornarUnidades() {
        assertEquals("unidades", new InventarioCalculator().getUnidad());
    }

    // -------------------------------------------------------
    // LogisticaCalculator
    // -------------------------------------------------------

    @Test
    void logisticaCalculator_calcular_conValoresNormales_debeRetornarTasaDeEntregas() {
        // Arrange
        LogisticaCalculator calc = new LogisticaCalculator();

        // Act — (950 / 1000) * 100 = 95.0000
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(950), BigDecimal.valueOf(1000));

        // Assert
        assertEquals(new BigDecimal("95.0000"), resultado);
    }

    @Test
    void logisticaCalculator_calcular_cuandoMetaEsCero_debeRetornarCero() {
        // Arrange
        LogisticaCalculator calc = new LogisticaCalculator();

        // Act
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(200), BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void logisticaCalculator_getUnidad_debeRetornarEntregas() {
        assertEquals("entregas", new LogisticaCalculator().getUnidad());
    }

    // -------------------------------------------------------
    // RentabilidadCalculator
    // -------------------------------------------------------

    @Test
    void rentabilidadCalculator_calcular_conValoresNormales_debeRetornarMargen() {
        // Arrange
        RentabilidadCalculator calc = new RentabilidadCalculator();

        // Act — (18.4 / 100) * 100 = 18.4000
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(18.4), BigDecimal.valueOf(100));

        // Assert
        assertEquals(new BigDecimal("18.4000"), resultado);
    }

    @Test
    void rentabilidadCalculator_calcular_cuandoMetaEsCero_debeRetornarCero() {
        // Arrange
        RentabilidadCalculator calc = new RentabilidadCalculator();

        // Act
        BigDecimal resultado = calc.calcular(BigDecimal.valueOf(30), BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void rentabilidadCalculator_getUnidad_debeRetornarPorcentaje() {
        assertEquals("porcentaje", new RentabilidadCalculator().getUnidad());
    }
}
