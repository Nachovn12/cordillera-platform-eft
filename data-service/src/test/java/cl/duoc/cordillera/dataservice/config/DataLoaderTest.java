package cl.duoc.cordillera.dataservice.config;

import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private DatoRepository datoRepository;

    @InjectMocks
    private DataLoader dataLoader;

    @Test
    void run_cuandoRepositorioVacio_debeCargarDatosSemilla() throws Exception {
        // Arrange - Repositorio vacio, debe insertar datos iniciales
        when(datoRepository.count()).thenReturn(0L);

        // Act
        dataLoader.run();

        // Assert
        verify(datoRepository, times(10)).save(any());
    }

    @Test
    void run_cuandoRepositorioConDatos_noDebeCargarDatosSemilla() throws Exception {
        // Arrange - Ya existen datos, no debe volver a insertar
        when(datoRepository.count()).thenReturn(5L);

        // Act
        dataLoader.run();

        // Assert
        verify(datoRepository, never()).save(any());
    }
}
