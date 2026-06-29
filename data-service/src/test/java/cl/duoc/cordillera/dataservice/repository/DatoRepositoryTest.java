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
    void findBySistemaOrigen_retornaListaFiltrada() {
        // Arrange - Escenario: 2 ventas POS y 1 transacción SAP en sucursal Santiago
        Dato pos1 = crearDato("POS", "VENTA", "120000", 1L);
        Dato pos2 = crearDato("POS", "VENTA", "95000", 2L);
        Dato sap1 = crearDato("SAP", "FINANZAS", "500000", 1L);
        datoRepository.save(pos1);
        datoRepository.save(pos2);
        datoRepository.save(sap1);

        // Act
        List<Dato> resultado = datoRepository.findBySistemaOrigen("POS");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(d -> "POS".equals(d.getSistemaOrigen())));
    }

    @Test
    void findBySucursalId_retornaListaFiltrada() {
        // Arrange - Escenario: sucursal 1 tiene 2 registros, sucursal 2 tiene 1
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
        assertTrue(resultado.stream().allMatch(d -> Long.valueOf(1L).equals(d.getSucursalId())));
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
