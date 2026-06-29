package cl.duoc.cordillera.dataservice.controller;

import cl.duoc.cordillera.dataservice.exception.GlobalExceptionHandler;
import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.service.DatoService;
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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Capa Controller — pruebas de endpoints REST con MockMvc (standaloneSetup).
 * Patrón AAA. DatoService mockeado con Mockito; GlobalExceptionHandler registrado
 * para validar respuestas 404 y 400.
 */
@ExtendWith(MockitoExtension.class)
class DatoControllerTest {

    @Mock
    private DatoService datoService;

    @InjectMocks
    private DatoController datoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(datoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    // -------------------------------------------------------
    // GET /api/datos
    // -------------------------------------------------------

    @Test
    void listarTodos_debeRetornarOkConListaDeDatos() throws Exception {
        // Arrange
        Dato dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
        when(datoService.listarTodos()).thenReturn(List.of(dato));

        // Act & Assert
        mockMvc.perform(get("/api/datos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sistemaOrigen").value("POS"))
                .andExpect(jsonPath("$[0].tipoDato").value("VENTA"));

        verify(datoService).listarTodos();
    }

    @Test
    void listarTodos_debeRetornarListaVaciaCuandoNoHayDatos() throws Exception {
        // Arrange
        when(datoService.listarTodos()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/datos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(datoService).listarTodos();
    }

    // -------------------------------------------------------
    // GET /api/datos/{id}
    // -------------------------------------------------------

    @Test
    void buscarPorId_debeRetornarOkCuandoExiste() throws Exception {
        // Arrange
        Dato dato = new Dato(1L, "CRM", "CLIENTE", "ACTIVO", null, 2L);
        when(datoService.buscarPorId(1L)).thenReturn(dato);

        // Act & Assert
        mockMvc.perform(get("/api/datos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sistemaOrigen").value("CRM"))
                .andExpect(jsonPath("$.tipoDato").value("CLIENTE"));

        verify(datoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        // Arrange — GlobalExceptionHandler convierte NoSuchElementException → 404
        when(datoService.buscarPorId(99L))
                .thenThrow(new NoSuchElementException("Dato no encontrado con id: 99"));

        // Act & Assert
        mockMvc.perform(get("/api/datos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Dato no encontrado con id: 99"));

        verify(datoService).buscarPorId(99L);
    }

    // -------------------------------------------------------
    // POST /api/datos
    // -------------------------------------------------------

    @Test
    void crear_debeRetornarCreatedConElDatoGuardado() throws Exception {
        // Arrange
        Dato nuevo = new Dato(null, "ERP", "INVENTARIO", "500", null, 1L);
        Dato guardado = new Dato(1L, "ERP", "INVENTARIO", "500", null, 1L);
        when(datoService.crear(any(Dato.class))).thenReturn(guardado);

        // Act & Assert
        mockMvc.perform(post("/api/datos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sistemaOrigen").value("ERP"));

        verify(datoService).crear(any(Dato.class));
    }

    @Test
    void crear_cuandoDatosInvalidos_debeRetornar400() throws Exception {
        // Arrange — sistemaOrigen en blanco viola @NotBlank → GlobalExceptionHandler → 400
        String bodyInvalido = "{\"sistemaOrigen\":\"\",\"tipoDato\":\"VENTA\",\"valor\":\"100\",\"sucursalId\":1}";

        // Act & Assert
        mockMvc.perform(post("/api/datos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());

        verify(datoService, never()).crear(any());
    }

    // -------------------------------------------------------
    // PUT /api/datos/{id}
    // -------------------------------------------------------

    @Test
    void actualizar_debeRetornarOkConDatosActualizados() throws Exception {
        // Arrange
        Dato actualizado = new Dato(1L, "POS", "VENTA", "200000", null, 1L);
        when(datoService.actualizar(eq(1L), any(Dato.class))).thenReturn(actualizado);

        // Act & Assert
        mockMvc.perform(put("/api/datos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("200000"));

        verify(datoService).actualizar(eq(1L), any(Dato.class));
    }

    // -------------------------------------------------------
    // DELETE /api/datos/{id}
    // -------------------------------------------------------

    @Test
    void eliminar_debeRetornarNoContent() throws Exception {
        // Arrange
        doNothing().when(datoService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/datos/1"))
                .andExpect(status().isNoContent());

        verify(datoService).eliminar(1L);
    }

    // -------------------------------------------------------
    // GET /api/datos/sistema/{origen}
    // -------------------------------------------------------

    @Test
    void buscarPorSistema_debeRetornarOkConDatosFiltrados() throws Exception {
        // Arrange
        Dato dato = new Dato(1L, "POS", "VENTA", "150000", null, 1L);
        when(datoService.buscarPorSistemaOrigen("POS")).thenReturn(List.of(dato));

        // Act & Assert
        mockMvc.perform(get("/api/datos/sistema/POS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sistemaOrigen").value("POS"));

        verify(datoService).buscarPorSistemaOrigen("POS");
    }

    @Test
    void buscarPorSistema_cuandoNoHayDatos_debeRetornarListaVacia() throws Exception {
        // Arrange
        when(datoService.buscarPorSistemaOrigen("INEXISTENTE")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/datos/sistema/INEXISTENTE"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(datoService).buscarPorSistemaOrigen("INEXISTENTE");
    }

    // -------------------------------------------------------
    // GET /api/datos/sucursal/{id}
    // -------------------------------------------------------

    @Test
    void buscarPorSucursal_debeRetornarOkConDatosDeLaSucursal() throws Exception {
        // Arrange
        Dato dato = new Dato(1L, "FINANZAS", "INGRESO", "980000", null, 3L);
        when(datoService.buscarPorSucursalId(3L)).thenReturn(List.of(dato));

        // Act & Assert
        mockMvc.perform(get("/api/datos/sucursal/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sucursalId").value(3));

        verify(datoService).buscarPorSucursalId(3L);
    }

    @Test
    void buscarPorSucursal_cuandoNoHayDatos_debeRetornarListaVacia() throws Exception {
        // Arrange
        when(datoService.buscarPorSucursalId(9999L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/datos/sucursal/9999"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(datoService).buscarPorSucursalId(9999L);
    }

    @Test
    void actualizar_conIdValido_retorna200() throws Exception {
        // Arrange - Escenario: corrección de dato en sucursal Santiago
        Dato datos = new Dato();
        datos.setSistemaOrigen("ERP");
        datos.setTipoDato("INVENTARIO");
        datos.setValor("150000");
        datos.setSucursalId(1L);

        Dato actualizado = new Dato();
        actualizado.setId(1L);
        actualizado.setSistemaOrigen("ERP");

        when(datoService.actualizar(eq(1L), any())).thenReturn(actualizado);

        // Act & Assert
        // NOTA: ruta interna es /api/datos/{id}, NO /api/v1/datos/{id}
        mockMvc.perform(put("/api/datos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sistemaOrigen").value("ERP"));
    }
    @Test
    void actualizar_conIdInexistente_retorna404() throws Exception {
        // Arrange - Escenario: intento actualizar dato inexistente con body valido
        Dato datoValido = new Dato(null, "ERP", "INVENTARIO", "150000", null, 1L);
        when(datoService.actualizar(eq(9999L), any()))
            .thenThrow(new NoSuchElementException("Dato no encontrado"));
        // Act & Assert
        mockMvc.perform(put("/api/datos/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datoValido)))
            .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_conIdValido_retorna204() throws Exception {
        // Arrange - Escenario: eliminar dato duplicado de sucursal
        doNothing().when(datoService).eliminar(1L);

        // Act & Assert
        // El estándar REST para DELETE exitoso es 204 No Content sin body
        mockMvc.perform(delete("/api/datos/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_conIdInexistente_retorna404() throws Exception {
        // Arrange
        // IMPORTANTE: DatoService lanza NoSuchElementException — NO DatoNoEncontradoException
        doThrow(new NoSuchElementException("Dato no encontrado"))
            .when(datoService).eliminar(9999L);

        // Act & Assert
        mockMvc.perform(delete("/api/datos/9999"))
            .andExpect(status().isNotFound());
    }
}

