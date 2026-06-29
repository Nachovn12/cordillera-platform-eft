package cl.duoc.cordillera.dataservice.service;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    void listarTodos_retornaLista() {
        when(datoRepository.findAll()).thenReturn(List.of(new Dato()));
        assertFalse(datoService.listarTodos().isEmpty());
        verify(datoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_exito() {
        Dato d = new Dato(1L, "POS", "VENTA", "100", LocalDateTime.now(), 1L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(d));
        assertNotNull(datoService.buscarPorId(1L));
        verify(datoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_lanzaExcepcion() {
        when(datoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> datoService.buscarPorId(99L));
        verify(datoRepository, times(1)).findById(99L);
    }

    @Test
    void crear_retornaDato() {
        Dato d = new Dato(null, "POS", "VENTA", "100", LocalDateTime.now(), 1L);
        when(datoRepository.save(d)).thenReturn(d);
        assertEquals(d, datoService.crear(d));
        verify(datoRepository, times(1)).save(d);
    }

    @Test
    void actualizar_exito() {
        Dato d = new Dato(1L, "POS", "VENTA", "100", LocalDateTime.now(), 1L);
        Dato nuevo = new Dato(null, "ERP", "CAJA", "500", null, 2L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(d));
        when(datoRepository.save(any(Dato.class))).thenReturn(d);

        assertNotNull(datoService.actualizar(1L, nuevo));
        verify(datoRepository, times(1)).save(d);
    }

    @Test
    void eliminar_exito() {
        Dato d = new Dato(1L, "POS", "VENTA", "100", LocalDateTime.now(), 1L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(d));
        doNothing().when(datoRepository).deleteById(1L);

        datoService.eliminar(1L);
        verify(datoRepository, times(1)).deleteById(1L);
    }

    @Test
    void buscarPorSistemaOrigen_retornaLista() {
        when(datoRepository.findBySistemaOrigen("POS")).thenReturn(List.of(new Dato()));
        assertFalse(datoService.buscarPorSistemaOrigen("POS").isEmpty());
        verify(datoRepository, times(1)).findBySistemaOrigen("POS");
    }

    @Test
    void buscarPorSucursalId_retornaLista() {
        when(datoRepository.findBySucursalId(1L)).thenReturn(List.of(new Dato()));
        assertFalse(datoService.buscarPorSucursalId(1L).isEmpty());
        verify(datoRepository, times(1)).findBySucursalId(1L);
    }
}
