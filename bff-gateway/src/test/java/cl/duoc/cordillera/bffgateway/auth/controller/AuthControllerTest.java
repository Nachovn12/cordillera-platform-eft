package cl.duoc.cordillera.bffgateway.auth.controller;

import cl.duoc.cordillera.bffgateway.auth.dto.AuthResponseDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.LoginRequestDTO;
import cl.duoc.cordillera.bffgateway.auth.service.AuthService;
import cl.duoc.cordillera.bffgateway.exception.CustomUnauthorizedException;
import cl.duoc.cordillera.bffgateway.exception.GlobalExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_credencialesValidas_retorna200ConToken() throws Exception {
        AuthResponseDTO respuesta = new AuthResponseDTO(
                "uuid-token-test",
                "A. Gatica",
                "GERENTE_GENERAL",
                "Gerencia General"
        );
        when(authService.autenticar(any())).thenReturn(respuesta);

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("a.gatica@cordillera.cl");
        request.setContrasena("gerencia2026");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("uuid-token-test"))
                .andExpect(jsonPath("$.nombre").value("A. Gatica"))
                .andExpect(jsonPath("$.rol").value("GERENTE_GENERAL"))
                .andExpect(jsonPath("$.area").value("Gerencia General"));
    }

    @Test
    void login_credencialesInvalidas_retorna401ConMensajeError() throws Exception {
        when(authService.autenticar(any()))
                .thenThrow(new CustomUnauthorizedException("Credenciales inválidas. Verifique usuario y contraseña."));

        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("intruso@cordillera.cl");
        request.setContrasena("contrasenaErronea");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas. Verifique usuario y contraseña."));
    }

    @Test
    void login_camposVacios_retorna400() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("");
        request.setContrasena("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_bodyVacio_retorna400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
