package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import cl.duoc.cordillera.kpiservice.service.calculator.KpiFactory;
import cl.duoc.cordillera.kpiservice.service.calculator.VentasCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiServiceTest {

    @Mock
    private KpiRepository kpiRepository;

    @Mock
    private KpiFactory kpiFactory;

    @InjectMocks
    private KpiService kpiService;

    private Kpi kpi;

    @BeforeEach
    void setUp() {
        kpi = new Kpi();
        kpi.setId(1L);
        kpi.setNombre("Ventas Q1");
        kpi.setValor(BigDecimal.valueOf(75000));
        kpi.setUnidad("CLP");
        kpi.setCategoria("ventas");
        kpi.setEstado("EN_PROGRESO");
    }

    @Test
    void findAll_debeRetornarListaDeKpis() {
        when(kpiRepository.findAll()).thenReturn(List.of(kpi));
        List<Kpi> result = kpiService.findAll();
        assertEquals(1, result.size());
        verify(kpiRepository, times(1)).findAll();
    }

    @Test
    void findById_debeRetornarKpi() {
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));
        Kpi result = kpiService.findById(1L);
        assertNotNull(result);
        assertEquals("Ventas Q1", result.getNombre());
    }

    @Test
    void findById_debeLanzarExcepcionSiNoExiste() {
        when(kpiRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> kpiService.findById(99L));
    }

    @Test
    void create_debeGuardarKpi() {
        when(kpiFactory.obtenerCalculador("ventas")).thenReturn(new VentasCalculator());
        when(kpiRepository.save(any(Kpi.class))).thenReturn(kpi);
        Kpi result = kpiService.create(kpi);
        assertNotNull(result);
        verify(kpiRepository, times(1)).save(any(Kpi.class));
    }
}