package cl.duoc.cordillera.dataservice.repository;

import cl.duoc.cordillera.dataservice.model.Dato;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Capa Repository — pruebas de acceso a datos con H2 en memoria.
 * DataLoader usa @Profile("!test"), por lo que no se ejecuta en este perfil.
 * Cada test corre en su propia transacción que se revierte al terminar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class DatoRepositoryTest {

    @Autowired
    private DatoRepository datoRepository;

    // -------------------------------------------------------
    // save / findById
    // -------------------------------------------------------

    @Test
    void save_debeGenerarIdAutomaticamente() {
        // Arrange
        Dato dato = new Dato(null, "POS", "VENTA", "150000", null, 1L);

        // Act
        Dato guardado = datoRepository.save(dato);

        // Assert
        assertNotNull(guardado.getId(), "El ID debe ser generado automáticamente");
        assertTrue(guardado.getId() > 0);
    }

    @Test
    void findById_debeRetornarDatoGuardado() {
        // Arrange
        Dato dato = new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 2L);
        Dato guardado = datoRepository.save(dato);

        // Act
        Optional<Dato> resultado = datoRepository.findById(guardado.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("CRM", resultado.get().getSistemaOrigen());
        assertEquals("ACTIVO", resultado.get().getValor());
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarVacio() {
        // Act
        Optional<Dato> resultado = datoRepository.findById(9999L);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // -------------------------------------------------------
    // findAll / delete
    // -------------------------------------------------------

    @Test
    void findAll_debeRetornarTodosLosRegistrosGuardados() {
        // Arrange
        datoRepository.save(new Dato(null, "POS", "VENTA", "150000", null, 1L));
        datoRepository.save(new Dato(null, "ERP", "STOCK", "500", null, 2L));
        datoRepository.save(new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 3L));

        // Act
        List<Dato> resultado = datoRepository.findAll();

        // Assert
        assertEquals(3, resultado.size());
    }

    @Test
    void delete_debeEliminarRegistroPorId() {
        // Arrange
        Dato dato = datoRepository.save(new Dato(null, "FINANZAS", "GASTO", "450000", null, 1L));
        Long id = dato.getId();

        // Act
        datoRepository.deleteById(id);

        // Assert
        assertFalse(datoRepository.findById(id).isPresent());
    }

    // -------------------------------------------------------
    // @PrePersist — fechaRegistro
    // -------------------------------------------------------

    @Test
    void onCrear_debeAsignarFechaRegistroAutomaticamente() {
        // Arrange — fechaRegistro null antes de persistir
        Dato dato = new Dato(null, "INVENTARIO", "STOCK", "320", null, 2L);
        assertNull(dato.getFechaRegistro(), "fechaRegistro debe ser null antes de persistir");

        // Act
        Dato guardado = datoRepository.save(dato);
        datoRepository.flush(); // fuerza el @PrePersist

        // Assert
        assertNotNull(guardado.getFechaRegistro(), "@PrePersist debe asignar fechaRegistro");
    }

    // -------------------------------------------------------
    // findBySistemaOrigen
    // -------------------------------------------------------

    @Test
    void findBySistemaOrigen_debeRetornarRegistrosFiltrados() {
        // Arrange
        datoRepository.save(new Dato(null, "POS", "VENTA", "150000", null, 1L));
        datoRepository.save(new Dato(null, "POS", "VENTA", "230000", null, 2L));
        datoRepository.save(new Dato(null, "CRM", "CLIENTE", "ACTIVO", null, 1L));

        // Act
        List<Dato> resultado = datoRepository.findBySistemaOrigen("POS");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(d -> "POS".equals(d.getSistemaOrigen())));
    }

    @Test
    void findBySistemaOrigen_cuandoNoExiste_debeRetornarListaVacia() {
        // Arrange
        datoRepository.save(new Dato(null, "POS", "VENTA", "150000", null, 1L));

        // Act
        List<Dato> resultado = datoRepository.findBySistemaOrigen("SISTEMA_INEXISTENTE");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // -------------------------------------------------------
    // findBySucursalId
    // -------------------------------------------------------

    @Test
    void findBySucursalId_debeRetornarRegistrosDeLaSucursal() {
        // Arrange
        datoRepository.save(new Dato(null, "POS", "VENTA", "150000", null, 1L));
        datoRepository.save(new Dato(null, "ERP", "STOCK", "500", null, 1L));
        datoRepository.save(new Dato(null, "FINANZAS", "INGRESO", "980000", null, 3L));

        // Act
        List<Dato> resultado = datoRepository.findBySucursalId(1L);

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(d -> d.getSucursalId().equals(1L)));
    }

    @Test
    void findBySucursalId_cuandoNoExiste_debeRetornarListaVacia() {
        // Act
        List<Dato> resultado = datoRepository.findBySucursalId(9999L);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
