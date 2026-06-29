package cl.duoc.cordillera.bffgateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Capa Controller — pruebas de ConfiguracionController.
 * No requiere mocks ya que construye datos estáticos internamente.
 */
class ConfiguracionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ConfiguracionController()).build();
        objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------
    // GET /api/configuracion
    // -------------------------------------------------------

    @Test
    void obtenerConfiguracion_debeRetornarOkConParametros() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/configuracion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parametros").exists())
                .andExpect(jsonPath("$.integraciones").isArray())
                .andExpect(jsonPath("$.usuarios").isArray())
                .andExpect(jsonPath("$.perfiles").isArray());
    }

    @Test
    void obtenerConfiguracion_debeContenerGatewayOperativo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/configuracion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parametros.gateway").value("Operativo"));
    }

    // -------------------------------------------------------
    // PUT /api/configuracion
    // -------------------------------------------------------

    @Test
    void actualizarConfiguracion_debeRetornarOkConParametrosActualizados() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("periodo", "Junio 2026", "sucursal", "Santiago");

        // Act & Assert
        mockMvc.perform(put("/api/configuracion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parametros").exists());
    }

    @Test
    void actualizarConfiguracion_debeIncluirParametrosEnviados() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of("periodo", "Junio 2026");

        // Act & Assert
        mockMvc.perform(put("/api/configuracion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parametros.periodo").value("Junio 2026"));
    }
}
