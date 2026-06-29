package cl.duoc.cordillera.bffgateway.auth.service;

import cl.duoc.cordillera.bffgateway.auth.dto.AuthResponseDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.CrearUsuarioRequestDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.LoginRequestDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.UsuarioResponseDTO;
import cl.duoc.cordillera.bffgateway.exception.CustomUnauthorizedException;
import cl.duoc.cordillera.bffgateway.exception.UsuarioNoEncontradoException;
import cl.duoc.cordillera.bffgateway.exception.UsuarioYaExisteException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AuthService {

    private final AtomicInteger contador = new AtomicInteger(0);
    private final ConcurrentHashMap<String, UsuarioInterno> usuarios = new ConcurrentHashMap<>();

    public AuthService() {
        agregarUsuarioInicial("a.gatica@cordillera.cl",      "gerencia2026", "A. Gatica",      "GERENTE_GENERAL", "Gerencia General");
        agregarUsuarioInicial("admin.valdivia@cordillera.cl", "admin123",     "Admin Valdivia", "ADMINISTRADOR",   "Administración");
    }

    private void agregarUsuarioInicial(String email, String pass, String nombre, String rol, String area) {
        String id = generarId();
        usuarios.put(id, new UsuarioInterno(id, email, pass, nombre, rol, area));
    }

    private String generarId() {
        return String.format("USR-%03d", contador.incrementAndGet());
    }

    public AuthResponseDTO autenticar(LoginRequestDTO request) {
        UsuarioInterno user = usuarios.values().stream()
                .filter(u -> u.usuario().equals(request.getUsuario()) && u.contrasena().equals(request.getContrasena()))
                .findFirst()
                .orElseThrow(() -> new CustomUnauthorizedException("Credenciales inválidas. Verifique usuario y contraseña."));

        return new AuthResponseDTO(UUID.randomUUID().toString(), user.nombre(), user.rol(), user.area());
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarios.values().stream()
                .map(u -> new UsuarioResponseDTO(u.id(), u.usuario(), u.nombre(), u.rol(), u.area()))
                .toList();
    }

    public UsuarioResponseDTO crearUsuario(CrearUsuarioRequestDTO request) {
        boolean emailExiste = usuarios.values().stream()
                .anyMatch(u -> u.usuario().equals(request.getUsuario()));
        if (emailExiste) {
            throw new UsuarioYaExisteException("El usuario '" + request.getUsuario() + "' ya existe.");
        }
        String id = generarId();
        UsuarioInterno nuevo = new UsuarioInterno(id, request.getUsuario(), request.getContrasena(),
                request.getNombre(), request.getRol(), request.getArea());
        usuarios.put(id, nuevo);
        return new UsuarioResponseDTO(nuevo.id(), nuevo.usuario(), nuevo.nombre(), nuevo.rol(), nuevo.area());
    }

    public void eliminarUsuario(String id) {
        if (!usuarios.containsKey(id)) {
            throw new UsuarioNoEncontradoException(id);
        }
        usuarios.remove(id);
    }

    private record UsuarioInterno(String id, String usuario, String contrasena, String nombre, String rol, String area) {}
}
