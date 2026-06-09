package cl.duoc.cordillera.kpiservice.controller;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.service.KpiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Capa Controller — pruebas de endpoints REST con MockMvc (standaloneSetup).
 * Patrón AAA. El KpiService está mockeado con Mockito para aislar la capa web.
 */
@ExtendWith(MockitoExtension.class)
class KpiControllerTest {

    @Mock
    private KpiService kpiService;

    @InjectMocks
    private KpiController kpiController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(kpiController).build();
        objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------
    // GET /api/kpis
    // -------------------------------------------------------

    @Test
    void getAll_debeRetornarOkConListaDeKpis() throws Exception {
        // Arrange
        when(kpiService.findAll()).thenReturn(List.of(kpi()));

        // Act & Assert
        mockMvc.perform(get("/api/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ventas Q1"))
                .andExpect(jsonPath("$[0].categoria").value("ventas"));

        verify(kpiService).findAll();
    }

    @Test
    void getAll_debeRetornarListaVaciaCuandoNoHayKpis() throws Exception {
        // Arrange
        when(kpiService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/kpis"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(kpiService).findAll();
    }

    // -------------------------------------------------------
    // GET /api/kpis/{id}
    // -------------------------------------------------------

    @Test
    void getById_debeRetornarOkCuandoExiste() throws Exception {
        // Arrange
        when(kpiService.findById(1L)).thenReturn(kpi());

        // Act & Assert
        mockMvc.perform(get("/api/kpis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas Q1"))
                .andExpect(jsonPath("$.unidad").value("CLP"));

        verify(kpiService).findById(1L);
    }

    @Test
    void getById_debeRetornar500CuandoKpiNoExiste() throws Exception {
        // Arrange — KpiService lanza RuntimeException (sin @ResponseStatus → 500)
        when(kpiService.findById(99L))
                .thenThrow(new RuntimeException("KPI no encontrado: 99"));

        // Act & Assert
        mockMvc.perform(get("/api/kpis/99"))
                .andExpect(status().isInternalServerError());

        verify(kpiService).findById(99L);
    }

    // -------------------------------------------------------
    // POST /api/kpis
    // -------------------------------------------------------

    @Test
    void create_debeRetornarCreatedConElKpiGuardado() throws Exception {
        // Arrange
        Kpi k = kpi();
        when(kpiService.create(any(Kpi.class))).thenReturn(k);

        // Act & Assert
        mockMvc.perform(post("/api/kpis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(k)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ventas Q1"));

        verify(kpiService).create(any(Kpi.class));
    }

    // -------------------------------------------------------
    // PUT /api/kpis/{id}
    // -------------------------------------------------------

    @Test
    void update_debeRetornarOkConDatosActualizados() throws Exception {
        // Arrange
        Kpi k = kpi();
        k.setNombre("Ventas Q2");
        when(kpiService.update(eq(1L), any(Kpi.class))).thenReturn(k);

        // Act & Assert
        mockMvc.perform(put("/api/kpis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(k)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas Q2"));

        verify(kpiService).update(eq(1L), any(Kpi.class));
    }

    // -------------------------------------------------------
    // DELETE /api/kpis/{id}
    // -------------------------------------------------------

    @Test
    void delete_debeRetornarNoContent() throws Exception {
        // Arrange
        doNothing().when(kpiService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/kpis/1"))
                .andExpect(status().isNoContent());

        verify(kpiService).delete(1L);
    }

    // -------------------------------------------------------
    // GET /api/kpis/categoria/{cat}
    // -------------------------------------------------------

    @Test
    void getByCategoria_debeRetornarKpisDeLaCategoria() throws Exception {
        // Arrange
        when(kpiService.findByCategoria("ventas")).thenReturn(List.of(kpi()));

        // Act & Assert
        mockMvc.perform(get("/api/kpis/categoria/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("ventas"));

        verify(kpiService).findByCategoria("ventas");
    }

    @Test
    void getByCategoria_debeRetornarListaVaciaParaCategoriaInexistente() throws Exception {
        // Arrange
        when(kpiService.findByCategoria("desconocida")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/kpis/categoria/desconocida"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(kpiService).findByCategoria("desconocida");
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------

    private Kpi kpi() {
        Kpi k = new Kpi();
        k.setId(1L);
        k.setNombre("Ventas Q1");
        k.setValor(BigDecimal.valueOf(75000));
        k.setUnidad("CLP");
        k.setCategoria("ventas");
        k.setEstado("EN_OBJETIVO");
        return k;
    }
}
