package cl.duoc.cordillera.dataservice.service;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Capa Service — pruebas de lógica de negocio con Mockito.
 * Patrón AAA. DatoRepository está mockeado para aislar la lógica del servicio.
 */
@ExtendWith(MockitoExtension.class)
class DatoServiceTest {

    @Mock
    private DatoRepository datoRepository;

    @InjectMocks
    private DatoService datoService;

    private Dato dato;

    @BeforeEach
    void setUp() {
        dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
    }

    // -------------------------------------------------------
    // listarTodos
    // -------------------------------------------------------

    @Test
    void listarTodos_debeRetornarListaDeRegistros() {
        // Arrange
        when(datoRepository.findAll()).thenReturn(List.of(dato));

        // Act
        List<Dato> resultado = datoService.listarTodos();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("POS", resultado.get(0).getSistemaOrigen());
        verify(datoRepository).findAll();
    }

    @Test
    void listarTodos_debeRetornarListaVaciaCuandoNoHayRegistros() {
        // Arrange
        when(datoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Dato> resultado = datoService.listarTodos();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(datoRepository).findAll();
    }

    // -------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------

    @Test
    void buscarPorId_debeRetornarDatoCuandoExiste() {
        // Arrange
        when(datoRepository.findById(1L)).thenReturn(Optional.of(dato));

        // Act
        Dato resultado = datoService.buscarPorId(1L);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("POS", resultado.getSistemaOrigen());
        verify(datoRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarNoSuchElementException() {
        // Arrange
        when(datoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> datoService.buscarPorId(99L));
        verify(datoRepository).findById(99L);
    }

    @Test
    void buscarPorId_mensajeExcepcionDebeContenerElId() {
        // Arrange
        when(datoRepository.findById(42L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> datoService.buscarPorId(42L));
        assertTrue(ex.getMessage().contains("42"), "El mensaje debe incluir el id buscado");
    }

    // -------------------------------------------------------
    // crear
    // -------------------------------------------------------

    @Test
    void crear_debeGuardarYRetornarElDato() {
        // Arrange
        Dato nuevo = new Dato(null, "INVENTARIO", "STOCK", "500", null, 1L);
        Dato guardado = new Dato(1L, "INVENTARIO", "STOCK", "500", null, 1L);
        when(datoRepository.save(nuevo)).thenReturn(guardado);

        // Act
        Dato resultado = datoService.crear(nuevo);

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("INVENTARIO", resultado.getSistemaOrigen());
        verify(datoRepository).save(nuevo);
    }

    // -------------------------------------------------------
    // actualizar
    // -------------------------------------------------------

    @Test
    void actualizar_debeModificarCamposDelDatoExistente() {
        // Arrange
        Dato cambios = new Dato(null, "ERP", "PEDIDO", "200000", null, 2L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(dato));
        when(datoRepository.save(any(Dato.class))).thenReturn(dato);

        // Act
        Dato resultado = datoService.actualizar(1L, cambios);

        // Assert
        assertNotNull(resultado);
        verify(datoRepository).findById(1L);
        verify(datoRepository).save(any(Dato.class));
    }

    @Test
    void actualizar_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(datoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> datoService.actualizar(99L, dato));
        verify(datoRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // eliminar
    // -------------------------------------------------------

    @Test
    void eliminar_debeEliminarDatoExistente() {
        // Arrange
        when(datoRepository.findById(1L)).thenReturn(Optional.of(dato));
        doNothing().when(datoRepository).deleteById(1L);

        // Act
        datoService.eliminar(1L);

        // Assert
        verify(datoRepository).findById(1L);
        verify(datoRepository).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(datoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> datoService.eliminar(99L));
        verify(datoRepository, never()).deleteById(any());
    }

    // -------------------------------------------------------
    // buscarPorSistemaOrigen
    // -------------------------------------------------------

    @Test
    void buscarPorSistemaOrigen_debeRetornarDatosFiltrados() {
        // Arrange
        when(datoRepository.findBySistemaOrigen("POS")).thenReturn(List.of(dato));

        // Act
        List<Dato> resultado = datoService.buscarPorSistemaOrigen("POS");

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("POS", resultado.get(0).getSistemaOrigen());
        verify(datoRepository).findBySistemaOrigen("POS");
    }

    @Test
    void buscarPorSistemaOrigen_cuandoNoExiste_debeRetornarListaVacia() {
        // Arrange
        when(datoRepository.findBySistemaOrigen("NADA")).thenReturn(Collections.emptyList());

        // Act
        List<Dato> resultado = datoService.buscarPorSistemaOrigen("NADA");

        // Assert
        assertTrue(resultado.isEmpty());
        verify(datoRepository).findBySistemaOrigen("NADA");
    }

    // -------------------------------------------------------
    // buscarPorSucursalId
    // -------------------------------------------------------

    @Test
    void buscarPorSucursalId_debeRetornarDatosDeLaSucursal() {
        // Arrange
        when(datoRepository.findBySucursalId(3L)).thenReturn(List.of(dato));

        // Act
        List<Dato> resultado = datoService.buscarPorSucursalId(3L);

        // Assert
        assertEquals(1, resultado.size());
        verify(datoRepository).findBySucursalId(3L);
    }

    @Test
    void buscarPorSucursalId_cuandoNoExiste_debeRetornarListaVacia() {
        // Arrange
        when(datoRepository.findBySucursalId(9999L)).thenReturn(Collections.emptyList());

        // Act
        List<Dato> resultado = datoService.buscarPorSucursalId(9999L);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(datoRepository).findBySucursalId(9999L);
    }

    @Test
    void actualizar_conIdExistente_debeRetornarDatoActualizado() {
        // Arrange - Escenario: corregir importe de venta POS a ERP en sucursal Santiago
        Dato datoExistente = new Dato();
        datoExistente.setId(1L);
        datoExistente.setSistemaOrigen("POS");
        datoExistente.setTipoDato("VENTA");
        datoExistente.setValor("125000");
        datoExistente.setSucursalId(1L);

        Dato nuevoDato = new Dato();
        nuevoDato.setSistemaOrigen("ERP");
        nuevoDato.setTipoDato("INVENTARIO");
        nuevoDato.setValor("150000");
        nuevoDato.setSucursalId(1L);

        Dato datoActualizado = new Dato();
        datoActualizado.setId(1L);
        datoActualizado.setSistemaOrigen("ERP");

        when(datoRepository.findById(1L)).thenReturn(Optional.of(datoExistente));
        when(datoRepository.save(any())).thenReturn(datoActualizado);

        // Act
        Dato resultado = datoService.actualizar(1L, nuevoDato);

        // Assert
        verify(datoRepository, times(1)).save(any());
        assertEquals("ERP", resultado.getSistemaOrigen());
    }

    @Test
    void actualizar_conIdInexistente_debeLanzarNoSuchElementException() {
        // Arrange - Escenario: intento corregir dato con id que no existe en data_db
        when(datoRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        // IMPORTANTE: DatoService lanza NoSuchElementException (java.util), NO excepción personalizada
        assertThrows(NoSuchElementException.class,
            () -> datoService.actualizar(9999L, new Dato()));
        verify(datoRepository, never()).save(any());
    }
}
