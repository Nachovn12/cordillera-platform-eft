package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.service.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Factory Method (KpiFactory).
 * Valida que la factory retorne el calculador correcto para cada categoría
 * y que lance excepción para categorías no soportadas.
 */
class KpiFactoryTest {

    private KpiFactory kpiFactory;

    @BeforeEach
    void setUp() {
        kpiFactory = new KpiFactory();
    }

    // -------------------------------------------------------
    // Calculadores por categoría
    // -------------------------------------------------------

    @Test
    void obtenerCalculador_ventas_debeRetornarVentasCalculator() {
        // Act
        KpiCalculator calc = kpiFactory.obtenerCalculador("ventas");

        // Assert
        assertInstanceOf(VentasCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_inventario_debeRetornarInventarioCalculator() {
        // Act
        KpiCalculator calc = kpiFactory.obtenerCalculador("inventario");

        // Assert
        assertInstanceOf(InventarioCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_logistica_debeRetornarLogisticaCalculator() {
        // Act
        KpiCalculator calc = kpiFactory.obtenerCalculador("logistica");

        // Assert
        assertInstanceOf(LogisticaCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_rentabilidad_debeRetornarRentabilidadCalculator() {
        // Act
        KpiCalculator calc = kpiFactory.obtenerCalculador("rentabilidad");

        // Assert
        assertInstanceOf(RentabilidadCalculator.class, calc);
    }

    // -------------------------------------------------------
    // Comportamiento ante entradas inválidas
    // -------------------------------------------------------

    @Test
    void obtenerCalculador_categoriaInvalida_debeLanzarIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> kpiFactory.obtenerCalculador("invalida"));
    }

    @Test
    void obtenerCalculador_categoriaEnMayusculas_debeResolverCorrectamente() {
        // Arrange — la factory hace toLowerCase(), por lo que debe manejar mayúsculas
        // Act
        KpiCalculator calc = kpiFactory.obtenerCalculador("VENTAS");

        // Assert
        assertInstanceOf(VentasCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_categoriaMixta_debeResolverCorrectamente() {
        // Act
        KpiCalculator calc = kpiFactory.obtenerCalculador("Inventario");

        // Assert
        assertInstanceOf(InventarioCalculator.class, calc);
    }
}
