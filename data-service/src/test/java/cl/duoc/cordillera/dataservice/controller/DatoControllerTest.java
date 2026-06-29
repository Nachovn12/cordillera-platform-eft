package cl.duoc.cordillera.dataservice.controller;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.service.DatoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatoController.class)
class DatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DatoService datoService;

    @Test
    void crear_conPayloadValido_retorna201() throws Exception {
        // Arrange - Escenario: POS sucursal Santiago envía venta correctamente
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        dato.setSucursalId(1L);
        Dato datoConId = new Dato();
        datoConId.setId(1L);
        datoConId.setSistemaOrigen("POS");
        when(datoService.crear(any())).thenReturn(datoConId);

        // Act & Assert
        mockMvc.perform(post("/api/datos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dato)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void crear_conSucursalIdNulo_retorna400() throws Exception {
        // Arrange - Escenario: POS envía dato sin identificar la sucursal
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        // sucursalId = null → debe fallar @NotNull

        // Act & Assert
        mockMvc.perform(post("/api/datos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dato)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listar_retorna200ConLista() throws Exception {
        // Arrange - Escenario: dashboard solicita todos los datos operacionales
        Dato d = new Dato();
        d.setId(1L);
        d.setSistemaOrigen("POS");
        when(datoService.listarTodos()).thenReturn(List.of(d));

        // Act & Assert
        mockMvc.perform(get("/api/datos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listarPorSistema_conResultados_retorna200() throws Exception {
        // Arrange - Escenario: BFF solicita datos del sistema SAP para el dashboard
        Dato d1 = new Dato();
        d1.setId(1L);
        d1.setSistemaOrigen("SAP");
        d1.setTipoDato("FINANZAS");
        Dato d2 = new Dato();
        d2.setId(2L);
        d2.setSistemaOrigen("SAP");
        d2.setTipoDato("FINANZAS");
        when(datoService.buscarPorSistemaOrigen("SAP")).thenReturn(List.of(d1, d2));

        // Act & Assert
        mockMvc.perform(get("/api/datos/sistema/SAP"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listarPorSucursal_conResultados_retorna200() throws Exception {
        // Arrange - Escenario: gerente de zona consulta datos de sucursal 1
        Dato d = new Dato();
        d.setId(1L);
        d.setSucursalId(1L);
        when(datoService.buscarPorSucursalId(1L)).thenReturn(List.of(d));

        // Act & Assert
        mockMvc.perform(get("/api/datos/sucursal/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void buscarPorSistema_sinResultados_retorna200Vacio() throws Exception {
        // Arrange - Escenario: sistema CRM aún no ha enviado datos (estado válido)
        when(datoService.buscarPorSistemaOrigen("CRM")).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/datos/sistema/CRM"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
        // IMPORTANTE: 200 + [] es el contrato correcto para colecciones vacías
    }
}
