package cl.duoc.cordillera.bffgateway.auth.service;

import cl.duoc.cordillera.bffgateway.auth.dto.AuthResponseDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.LoginRequestDTO;
import cl.duoc.cordillera.bffgateway.exception.CustomUnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    @Test
    void autenticar_comoGerenteGeneral_retornaTokenYRolCorrecto() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("a.gatica@cordillera.cl");
        request.setContrasena("gerencia2026");

        AuthResponseDTO response = authService.autenticar(request);

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getNombre()).isEqualTo("A. Gatica");
        assertThat(response.getRol()).isEqualTo("GERENTE_GENERAL");
        assertThat(response.getArea()).isEqualTo("Gerencia General");
    }

    @Test
    void autenticar_comoAdministrador_retornaTokenYRolCorrecto() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("admin.valdivia@cordillera.cl");
        request.setContrasena("admin123");

        AuthResponseDTO response = authService.autenticar(request);

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getRol()).isEqualTo("ADMINISTRADOR");
    }

    @Test
    void autenticar_cadaLlamadaGerenteGeneraTokenUnico() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("a.gatica@cordillera.cl");
        request.setContrasena("gerencia2026");

        String token1 = authService.autenticar(request).getToken();
        String token2 = authService.autenticar(request).getToken();

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void autenticar_usuarioValidoConClaveIncorrecta_lanzaExcepcion() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("a.gatica@cordillera.cl");
        request.setContrasena("claveEquivocada");

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(CustomUnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    void autenticar_usuarioInexistente_lanzaExcepcion() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("intruso@cordillera.cl");
        request.setContrasena("cualquierClave");

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(CustomUnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    void autenticar_adminConClaveIncorrecta_lanzaExcepcion() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsuario("admin.valdivia@cordillera.cl");
        request.setContrasena("wrongpass");

        assertThatThrownBy(() -> authService.autenticar(request))
                .isInstanceOf(CustomUnauthorizedException.class);
    }
}
