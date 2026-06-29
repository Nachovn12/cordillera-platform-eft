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
    void run_ejecutaCargaInicial() {
        when(datoRepository.count()).thenReturn(0L);
        dataLoader.run();
        verify(datoRepository, times(10)).save(any());
    }

    @Test
    void run_noCargaSiYaHayDatos() {
        when(datoRepository.count()).thenReturn(5L);
        dataLoader.run();
        verify(datoRepository, never()).save(any());
    }
}
