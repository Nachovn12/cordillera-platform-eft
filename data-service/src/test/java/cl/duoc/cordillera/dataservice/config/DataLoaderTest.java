package cl.duoc.cordillera.dataservice.config;

import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private DatoRepository datoRepository;

    @InjectMocks
    private DataLoader dataLoader;

    @Test
    void run_cuandoDbVacia_insertaDatos() {
        // Arrange - Escenario: La base de datos H2 se inicializa vacía
        when(datoRepository.count()).thenReturn(0L);

        // Act
        dataLoader.run();

        // Assert
        verify(datoRepository, times(10)).save(any());
    }

    @Test
    void run_cuandoDbNoEstaVacia_noInsertaDatos() {
        // Arrange - Escenario: La base de datos ya contiene datos previos
        when(datoRepository.count()).thenReturn(5L);

        // Act
        dataLoader.run();

        // Assert
        verify(datoRepository, never()).save(any());
    }
}
