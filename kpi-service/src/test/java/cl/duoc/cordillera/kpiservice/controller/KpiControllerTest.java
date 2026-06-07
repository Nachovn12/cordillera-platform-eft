package cl.duoc.cordillera.kpiservice.controller;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.service.KpiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class KpiControllerTest {

    private KpiService kpiService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        kpiService = mock(KpiService.class);
        KpiController controller = new KpiController(kpiService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    private Kpi crearKpi() {
        Kpi kpi = new Kpi();
        kpi.setId(1L);
        kpi.setNombre("Ventas Q1");
        kpi.setValor(BigDecimal.valueOf(75000));
        kpi.setUnidad("CLP");
        kpi.setCategoria("ventas");
        kpi.setEstado("EN_OBJETIVO");
        return kpi;
    }

    @Test
    void getAllDebeRetornarOk() throws Exception {
        when(kpiService.findAll()).thenReturn(List.of(crearKpi()));

        mockMvc.perform(get("/api/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ventas Q1"))
                .andExpect(jsonPath("$[0].categoria").value("ventas"));

        verify(kpiService).findAll();
    }

    @Test
    void getByIdDebeRetornarOkCuandoExiste() throws Exception {
        when(kpiService.findById(1L)).thenReturn(crearKpi());

        mockMvc.perform(get("/api/kpis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas Q1"))
                .andExpect(jsonPath("$.unidad").value("CLP"));

        verify(kpiService).findById(1L);
    }

    @Test
    void createDebeRetornarCreated() throws Exception {
        Kpi kpi = crearKpi();
        when(kpiService.create(any(Kpi.class))).thenReturn(kpi);

        mockMvc.perform(post("/api/kpis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kpi)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ventas Q1"));

        verify(kpiService).create(any(Kpi.class));
    }

    @Test
    void updateDebeRetornarOk() throws Exception {
        Kpi kpi = crearKpi();
        kpi.setNombre("Ventas Q2");
        when(kpiService.update(eq(1L), any(Kpi.class))).thenReturn(kpi);

        mockMvc.perform(put("/api/kpis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kpi)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas Q2"));

        verify(kpiService).update(eq(1L), any(Kpi.class));
    }

    @Test
    void deleteDebeRetornarNoContent() throws Exception {
        doNothing().when(kpiService).delete(1L);

        mockMvc.perform(delete("/api/kpis/1"))
                .andExpect(status().isNoContent());

        verify(kpiService).delete(1L);
    }

    @Test
    void getByCategoriaDebeRetornarOk() throws Exception {
        when(kpiService.findByCategoria("ventas")).thenReturn(List.of(crearKpi()));

        mockMvc.perform(get("/api/kpis/categoria/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("ventas"));

        verify(kpiService).findByCategoria("ventas");
    }
}
