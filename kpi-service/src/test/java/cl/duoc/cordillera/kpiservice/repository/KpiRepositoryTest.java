package cl.duoc.cordillera.kpiservice.repository;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Capa Repository — pruebas de persistencia con H2 en memoria.
 *
 * @SpringBootTest carga el contexto con perfil "test" (H2 en lugar de MySQL).
 * @Transactional revierte cada test para aislar las pruebas.
 * @BeforeEach limpia la BD para neutralizar los datos del KpiDataLoader.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class KpiRepositoryTest {

    @Autowired
    private KpiRepository kpiRepository;

    @BeforeEach
    void limpiar() {
        kpiRepository.deleteAll();
    }

    // -------------------------------------------------------
    // CRUD básico
    // -------------------------------------------------------

    @Test
    void guardar_debeAsignarIdAutoGenerado() {
        // Arrange
        Kpi kpi = kpi("Ventas Q1", "150000", "CLP", "ventas", "Activo");

        // Act
        Kpi guardado = kpiRepository.save(kpi);

        // Assert
        assertNotNull(guardado.getId(), "El id debe ser generado por la base de datos");
    }

    @Test
    void guardarYFindById_debeRetornarElMismoKpi() {
        // Arrange
        Kpi kpi = kpi("Stock Central", "820", "unidades", "inventario", "Activo");

        // Act
        Kpi guardado = kpiRepository.save(kpi);
        Optional<Kpi> encontrado = kpiRepository.findById(guardado.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Stock Central", encontrado.get().getNombre());
        assertEquals("inventario", encontrado.get().getCategoria());
    }

    @Test
    void findAll_debeRetornarTodosLosKpisGuardados() {
        // Arrange
        kpiRepository.save(kpi("Ventas Q1", "100000", "CLP", "ventas", "Activo"));
        kpiRepository.save(kpi("Rotacion Inventario", "82", "%", "inventario", "Advertencia"));
        kpiRepository.save(kpi("Rentabilidad Op.", "18.4", "%", "rentabilidad", "Activo"));

        // Act
        List<Kpi> todos = kpiRepository.findAll();

        // Assert
        assertEquals(3, todos.size());
    }

    @Test
    void eliminar_debeRemoverElKpi() {
        // Arrange
        Kpi guardado = kpiRepository.save(kpi("KPI Temporal", "500", "CLP", "logistica", "Activo"));
        Long id = guardado.getId();

        // Act
        kpiRepository.delete(guardado);
        Optional<Kpi> resultado = kpiRepository.findById(id);

        // Assert
        assertFalse(resultado.isPresent(), "El KPI eliminado no debe encontrarse");
    }

    @Test
    void count_debeReflejarElNumeroRealDeKpis() {
        // Arrange
        kpiRepository.save(kpi("K1", "100", "CLP", "ventas", "Activo"));
        kpiRepository.save(kpi("K2", "200", "unidades", "inventario", "Activo"));

        // Act
        long total = kpiRepository.count();

        // Assert
        assertEquals(2, total);
    }

    // -------------------------------------------------------
    // Consulta personalizada: findByCategoria
    // -------------------------------------------------------

    @Test
    void findByCategoria_debeRetornarSoloLosKpisDeEsaCategoria() {
        // Arrange
        kpiRepository.save(kpi("Ventas Enero", "100000", "CLP", "ventas", "Activo"));
        kpiRepository.save(kpi("Ventas Febrero", "120000", "CLP", "ventas", "Activo"));
        kpiRepository.save(kpi("Stock Central", "820", "unidades", "inventario", "Activo"));

        // Act
        List<Kpi> resultado = kpiRepository.findByCategoria("ventas");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(k -> "ventas".equals(k.getCategoria())));
    }

    @Test
    void findByCategoria_debeRetornarListaVaciaParaCategoriaInexistente() {
        // Arrange
        kpiRepository.save(kpi("Ventas Q1", "100000", "CLP", "ventas", "Activo"));

        // Act
        List<Kpi> resultado = kpiRepository.findByCategoria("categoriaInexistente");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByCategoria_debeRetornarKpisDeDiferentesEstados() {
        // Arrange
        kpiRepository.save(kpi("Rentabilidad Op.", "18.4", "%", "rentabilidad", "Activo"));
        kpiRepository.save(kpi("Rentabilidad Neta", "12.1", "%", "rentabilidad", "Advertencia"));

        // Act
        List<Kpi> resultado = kpiRepository.findByCategoria("rentabilidad");

        // Assert
        assertEquals(2, resultado.size());
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------

    private Kpi kpi(String nombre, String valor, String unidad, String categoria, String estado) {
        Kpi k = new Kpi();
        k.setNombre(nombre);
        k.setValor(new BigDecimal(valor));
        k.setUnidad(unidad);
        k.setCategoria(categoria);
        k.setEstado(estado);
        return k;
    }
}
