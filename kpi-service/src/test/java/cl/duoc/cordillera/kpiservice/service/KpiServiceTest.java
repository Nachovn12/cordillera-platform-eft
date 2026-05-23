package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import cl.duoc.cordillera.kpiservice.service.calculator.KpiCalculator;
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
        KpiCalculator calculator = new VentasCalculator();
        when(kpiFactory.obtenerCalculador("ventas")).thenReturn(calculator);
        when(kpiRepository.save(any(Kpi.class))).thenReturn(kpi);
        Kpi result = kpiService.create(kpi);
        assertNotNull(result);
        verify(kpiRepository, times(1)).save(any(Kpi.class));
    }

    @Test
    void update_debeActualizarKpi() {
        KpiCalculator calculator = new VentasCalculator();
        when(kpiFactory.obtenerCalculador("ventas")).thenReturn(calculator);
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));
        when(kpiRepository.save(any(Kpi.class))).thenReturn(kpi);
        Kpi result = kpiService.update(1L, kpi);
        assertNotNull(result);
        verify(kpiRepository, times(1)).save(any(Kpi.class));
    }

    @Test
    void delete_debeEliminarKpi() {
        doNothing().when(kpiRepository).deleteById(1L);
        kpiService.delete(1L);
        verify(kpiRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByCategoria_debeRetornarKpisFiltrados() {
        when(kpiRepository.findByCategoria("ventas")).thenReturn(List.of(kpi));
        List<Kpi> result = kpiService.findByCategoria("ventas");
        assertEquals(1, result.size());
        verify(kpiRepository, times(1)).findByCategoria("ventas");
    }
}
