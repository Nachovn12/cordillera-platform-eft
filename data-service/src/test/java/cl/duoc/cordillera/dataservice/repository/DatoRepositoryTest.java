package cl.duoc.cordillera.dataservice.repository;

import cl.duoc.cordillera.dataservice.model.Dato;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DatoRepositoryTest {

    @Autowired
    private DatoRepository datoRepository;

    @Test
    void save_debeRetornarDatoConId() {
        // Arrange - Escenario: Sistema POS de sucursal Santiago registra nueva venta
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        dato.setSucursalId(1L);

        // Act
        Dato guardado = datoRepository.save(dato);

        // Assert
        assertNotNull(guardado.getId());
        assertEquals("POS", guardado.getSistemaOrigen());
    }

    @Test
    void findBySistemaOrigen_debeRetornarLista() {
        // Arrange - Escenario: 2 ventas POS de distintas sucursales
        Dato d1 = crearDato("POS", "VENTA", "120000", 1L);
        Dato d2 = crearDato("POS", "VENTA", "95000", 2L);
        Dato d3 = crearDato("SAP", "FINANZAS", "500000", 1L);
        datoRepository.save(d1);
        datoRepository.save(d2);
        datoRepository.save(d3);

        // Act
        List<Dato> resultado = datoRepository.findBySistemaOrigen("POS");

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void findBySucursalId_debeRetornarLista() {
        // Arrange - Escenario: datos de sucursal Santiago (id=1)
        Dato d1 = crearDato("POS", "VENTA", "120000", 1L);
        Dato d2 = crearDato("ERP", "INVENTARIO", "50", 1L);
        Dato d3 = crearDato("CRM", "CLIENTE", "1", 2L);
        datoRepository.save(d1);
        datoRepository.save(d2);
        datoRepository.save(d3);

        // Act
        List<Dato> resultado = datoRepository.findBySucursalId(1L);

        // Assert
        assertEquals(2, resultado.size());
    }

    private Dato crearDato(String origen, String tipo, String valor, Long sucursalId) {
        Dato d = new Dato();
        d.setSistemaOrigen(origen);
        d.setTipoDato(tipo);
        d.setValor(valor);
        d.setSucursalId(sucursalId);
        return d;
    }
}
