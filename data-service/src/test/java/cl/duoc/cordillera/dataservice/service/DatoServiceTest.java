package cl.duoc.cordillera.dataservice.service;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void crear_conPayloadValido_debePersistirYRetornar() {
        // Arrange - Escenario: Sistema POS registra venta en sucursal Santiago
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        dato.setSucursalId(1L);
        Dato datoConId = new Dato();
        datoConId.setId(1L);
        datoConId.setSistemaOrigen("POS");
        when(datoRepository.save(any())).thenReturn(datoConId);

        // Act
        Dato resultado = datoService.crear(dato);

        // Assert
        verify(datoRepository, times(1)).save(dato);
        assertNotNull(resultado.getId());
    }

    @Test
    void actualizar_conIdInexistente_debeLanzarNoSuchElementException() {
        // Arrange - Escenario: intento corregir dato con id incorrecto
        when(datoRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
            () -> datoService.actualizar(9999L, new Dato()));
    }

    @Test
    void eliminar_debeInvocarDeleteById() {
        // Arrange - Escenario: eliminar dato duplicado de sucursal
        Dato existente = new Dato();
        existente.setId(1L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(existente));

        // Act
        datoService.eliminar(1L);

        // Assert
        verify(datoRepository).deleteById(1L);
    }
}