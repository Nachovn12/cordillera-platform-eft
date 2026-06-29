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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Capa Service — pruebas de lógica de negocio con Mockito.
 * Patrón AAA. Repository y KpiFactory mockeados para aislar la lógica del servicio.
 */
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

    // -------------------------------------------------------
    // findAll
    // -------------------------------------------------------

    @Test
    void findAll_debeRetornarListaDeKpis() {
        // Arrange
        when(kpiRepository.findAll()).thenReturn(List.of(kpi));

        // Act
        List<Kpi> result = kpiService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Ventas Q1", result.get(0).getNombre());
        verify(kpiRepository).findAll();
    }

    @Test
    void findAll_debeRetornarListaVaciaCuandoNoHayKpis() {
        // Arrange
        when(kpiRepository.findAll()).thenReturn(List.of());

        // Act
        List<Kpi> result = kpiService.findAll();

        // Assert
        assertTrue(result.isEmpty());
        verify(kpiRepository).findAll();
    }

    // -------------------------------------------------------
    // findById
    // -------------------------------------------------------

    @Test
    void findById_debeRetornarKpiCuandoExiste() {
        // Arrange
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));

        // Act
        Kpi result = kpiService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Ventas Q1", result.getNombre());
        verify(kpiRepository).findById(1L);
    }

    @Test
    void findById_debeLanzarNotFound404SiNoExiste() {
        // Arrange
        when(kpiRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        org.springframework.web.server.ResponseStatusException ex =
                assertThrows(org.springframework.web.server.ResponseStatusException.class,
                        () -> kpiService.findById(99L));
        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getMessage().contains("99"));
        verify(kpiRepository).findById(99L);
    }

    // -------------------------------------------------------
    // create — incluye calcularValor (Factory Method)
    // -------------------------------------------------------

    @Test
    void create_debeCalcularValorUsandoFactoryYGuardarElKpi() {
        // Arrange
        KpiCalculator calculator = new VentasCalculator();
        when(kpiFactory.obtenerCalculador("ventas")).thenReturn(calculator);
        when(kpiRepository.save(any(Kpi.class))).thenReturn(kpi);

        // Act
        Kpi result = kpiService.create(kpi);

        // Assert
        assertNotNull(result);
        verify(kpiFactory).obtenerCalculador("ventas");
        verify(kpiRepository).save(any(Kpi.class));
    }

    @Test
    void create_debeLanzarExcepcionCuandoCategoriaNoEsSoportada() {
        // Arrange
        kpi.setCategoria("invalida");
        when(kpiFactory.obtenerCalculador("invalida"))
                .thenThrow(new IllegalArgumentException("Categoría no soportada: invalida"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> kpiService.create(kpi));
        verify(kpiRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // update
    // -------------------------------------------------------

    @Test
    void update_debeActualizarCamposYRecalcularValor() {
        // Arrange
        KpiCalculator calculator = new VentasCalculator();
        when(kpiFactory.obtenerCalculador("ventas")).thenReturn(calculator);
        when(kpiRepository.findById(1L)).thenReturn(Optional.of(kpi));
        when(kpiRepository.save(any(Kpi.class))).thenReturn(kpi);

        // Act
        Kpi result = kpiService.update(1L, kpi);

        // Assert
        assertNotNull(result);
        verify(kpiFactory).obtenerCalculador("ventas");
        verify(kpiRepository).save(any(Kpi.class));
    }

    @Test
    void update_debeLanzarNotFound404SiKpiNoExiste() {
        // Arrange
        when(kpiRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        org.springframework.web.server.ResponseStatusException ex =
                assertThrows(org.springframework.web.server.ResponseStatusException.class,
                        () -> kpiService.update(99L, kpi));
        assertEquals(404, ex.getStatusCode().value());
        verify(kpiRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // delete
    // -------------------------------------------------------

    @Test
    void delete_debeEliminarKpiPorId() {
        // Arrange
        doNothing().when(kpiRepository).deleteById(1L);

        // Act
        kpiService.delete(1L);

        // Assert
        verify(kpiRepository).deleteById(1L);
    }

    // -------------------------------------------------------
    // findByCategoria
    // -------------------------------------------------------

    @Test
    void findByCategoria_debeRetornarKpisFiltradosPorCategoria() {
        // Arrange
        when(kpiRepository.findByCategoria("ventas")).thenReturn(List.of(kpi));

        // Act
        List<Kpi> result = kpiService.findByCategoria("ventas");

        // Assert
        assertEquals(1, result.size());
        assertEquals("ventas", result.get(0).getCategoria());
        verify(kpiRepository).findByCategoria("ventas");
    }

    @Test
    void findByCategoria_debeRetornarListaVaciaParaCategoriaInexistente() {
        // Arrange
        when(kpiRepository.findByCategoria("desconocida")).thenReturn(List.of());

        // Act
        List<Kpi> result = kpiService.findByCategoria("desconocida");

        // Assert
        assertTrue(result.isEmpty());
        verify(kpiRepository).findByCategoria("desconocida");
    }
}
