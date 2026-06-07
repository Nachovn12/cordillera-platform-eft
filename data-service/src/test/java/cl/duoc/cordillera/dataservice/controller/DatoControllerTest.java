package cl.duoc.cordillera.dataservice.controller;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.service.DatoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

class DatoControllerTest {

    private DatoService datoService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        datoService = mock(DatoService.class);
        DatoController controller = new DatoController(datoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void listarTodosDebeRetornarOk() throws Exception {
        Dato dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
        when(datoService.listarTodos()).thenReturn(List.of(dato));

        mockMvc.perform(get("/api/datos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sistemaOrigen").value("POS"));

        verify(datoService).listarTodos();
    }

    @Test
    void buscarPorIdDebeRetornarOkCuandoExiste() throws Exception {
        Dato dato = new Dato(1L, "CRM", "CLIENTE", "ACTIVO", null, 2L);
        when(datoService.buscarPorId(1L)).thenReturn(dato);

        mockMvc.perform(get("/api/datos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sistemaOrigen").value("CRM"))
                .andExpect(jsonPath("$.tipoDato").value("CLIENTE"));

        verify(datoService).buscarPorId(1L);
    }

    @Test
    void crearDebeRetornarCreated() throws Exception {
        Dato nuevo = new Dato(null, "ERP", "INVENTARIO", "500", null, 1L);
        Dato guardado = new Dato(1L, "ERP", "INVENTARIO", "500", null, 1L);
        when(datoService.crear(any(Dato.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/datos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sistemaOrigen").value("ERP"));

        verify(datoService).crear(any(Dato.class));
    }

    @Test
    void actualizarDebeRetornarOk() throws Exception {
        Dato actualizado = new Dato(1L, "POS", "VENTA", "200000", null, 1L);
        when(datoService.actualizar(eq(1L), any(Dato.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/datos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("200000"));

        verify(datoService).actualizar(eq(1L), any(Dato.class));
    }

    @Test
    void eliminarDebeRetornarNoContent() throws Exception {
        doNothing().when(datoService).eliminar(1L);

        mockMvc.perform(delete("/api/datos/1"))
                .andExpect(status().isNoContent());

        verify(datoService).eliminar(1L);
    }

    @Test
    void buscarPorSistemaDebeRetornarOk() throws Exception {
        Dato dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
        when(datoService.buscarPorSistemaOrigen("POS")).thenReturn(List.of(dato));

        mockMvc.perform(get("/api/datos/sistema/POS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sistemaOrigen").value("POS"));

        verify(datoService).buscarPorSistemaOrigen("POS");
    }

    @Test
    void buscarPorSucursalDebeRetornarOk() throws Exception {
        Dato dato = new Dato(1L, "FINANZAS", "INGRESO", "980000", null, 3L);
        when(datoService.buscarPorSucursalId(3L)).thenReturn(List.of(dato));

        mockMvc.perform(get("/api/datos/sucursal/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sucursalId").value(3));

        verify(datoService).buscarPorSucursalId(3L);
    }
}
