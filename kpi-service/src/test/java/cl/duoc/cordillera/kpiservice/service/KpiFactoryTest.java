package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.service.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KpiFactoryTest {

    private KpiFactory kpiFactory;

    @BeforeEach
    void setUp() {
        kpiFactory = new KpiFactory();
    }

    @Test
    void debeRetornarVentasCalculator() {
        KpiCalculator calc = kpiFactory.obtenerCalculador("ventas");
        assertInstanceOf(VentasCalculator.class, calc);
    }

    @Test
    void debeRetornarInventarioCalculator() {
        KpiCalculator calc = kpiFactory.obtenerCalculador("inventario");
        assertInstanceOf(InventarioCalculator.class, calc);
    }

    @Test
    void debeRetornarLogisticaCalculator() {
        KpiCalculator calc = kpiFactory.obtenerCalculador("logistica");
        assertInstanceOf(LogisticaCalculator.class, calc);
    }

    @Test
    void debeRetornarRentabilidadCalculator() {
        KpiCalculator calc = kpiFactory.obtenerCalculador("rentabilidad");
        assertInstanceOf(RentabilidadCalculator.class, calc);
    }

    @Test
    void debeLanzarExcepcionParaCategoriaInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
            kpiFactory.obtenerCalculador("invalida"));
    }
}