package cl.duoc.cordillera.dataservice.service;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatoServiceTest {

    @Mock
    private DatoRepository datoRepository;

    @InjectMocks
    private DatoService datoService;

    @Test
    void listarTodosDebeRetornarDatos() {
        Dato dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
        when(datoRepository.findAll()).thenReturn(List.of(dato));

        List<Dato> resultado = datoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("POS", resultado.get(0).getSistemaOrigen());
        verify(datoRepository).findAll();
    }

    @Test
    void buscarPorIdExistenteDebeRetornarDato() {
        Dato dato = new Dato(1L, "CRM", "CLIENTE", "ACTIVO", null, 2L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(dato));

        Dato resultado = datoService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("CRM", resultado.getSistemaOrigen());
        verify(datoRepository).findById(1L);
    }

    @Test
    void buscarPorIdInexistenteDebeLanzarExcepcion() {
        when(datoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> datoService.buscarPorId(99L));
        verify(datoRepository).findById(99L);
    }

    @Test
    void crearDebeGuardarDato() {
        Dato dato = new Dato(null, "INVENTARIO", "STOCK", "500", null, 1L);
        Dato guardado = new Dato(1L, "INVENTARIO", "STOCK", "500", null, 1L);

        when(datoRepository.save(dato)).thenReturn(guardado);

        Dato resultado = datoService.crear(dato);

        assertNotNull(resultado.getId());
        assertEquals("INVENTARIO", resultado.getSistemaOrigen());
        verify(datoRepository).save(dato);
    }

    @Test
    void buscarPorSistemaOrigenDebeRetornarDatosFiltrados() {
        Dato dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
        when(datoRepository.findBySistemaOrigen("POS")).thenReturn(List.of(dato));

        List<Dato> resultado = datoService.buscarPorSistemaOrigen("POS");

        assertEquals(1, resultado.size());
        assertEquals("POS", resultado.get(0).getSistemaOrigen());
        verify(datoRepository).findBySistemaOrigen("POS");
    }

    @Test
    void buscarPorSucursalIdDebeRetornarDatosFiltrados() {
        Dato dato = new Dato(1L, "FINANZAS", "INGRESO", "980000", null, 3L);
        when(datoRepository.findBySucursalId(3L)).thenReturn(List.of(dato));

        List<Dato> resultado = datoService.buscarPorSucursalId(3L);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getSucursalId());
        verify(datoRepository).findBySucursalId(3L);
    }
}