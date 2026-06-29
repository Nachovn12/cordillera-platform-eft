package cl.duoc.cordillera.dataservice.repository;

import cl.duoc.cordillera.dataservice.model.Dato;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatoRepositoryTest {

    @Autowired
    private DatoRepository datoRepository;

    @Test
    void findBySistemaOrigen_retornaLista() {
        Dato d = new Dato(null, "POS", "VENTA", "1000", LocalDateTime.now(), 1L);
        datoRepository.save(d);

        List<Dato> res = datoRepository.findBySistemaOrigen("POS");
        assertFalse(res.isEmpty());
        assertEquals("VENTA", res.get(0).getTipoDato());
    }

    @Test
    void findBySucursalId_retornaLista() {
        Dato d = new Dato(null, "SAP", "STOCK", "50", LocalDateTime.now(), 2L);
        datoRepository.save(d);

        List<Dato> res = datoRepository.findBySucursalId(2L);
        assertFalse(res.isEmpty());
        assertEquals("SAP", res.get(0).getSistemaOrigen());
    }
}
