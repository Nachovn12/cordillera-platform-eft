package cl.duoc.cordillera.bffgateway.auth.service;

import cl.duoc.cordillera.bffgateway.auth.dto.AuthResponseDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.CrearUsuarioRequestDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.LoginRequestDTO;
import cl.duoc.cordillera.bffgateway.auth.dto.UsuarioResponseDTO;
import cl.duoc.cordillera.bffgateway.auth.entity.Usuario;
import cl.duoc.cordillera.bffgateway.auth.repository.UsuarioRepository;
import cl.duoc.cordillera.bffgateway.exception.CustomUnauthorizedException;
import cl.duoc.cordillera.bffgateway.exception.UsuarioNoEncontradoException;
import cl.duoc.cordillera.bffgateway.exception.UsuarioYaExisteException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AtomicInteger contador = new AtomicInteger(0);

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void init() {
        if (usuarioRepository.count() == 0) {
            agregarUsuarioInicial("a.gatica@cordillera.cl", "gerencia2026", "A. Gatica", "GERENTE_GENERAL", "Gerencia General", null);
            agregarUsuarioInicial("admin.valdivia@cordillera.cl", "admin123", "Admin Valdivia", "ADMINISTRADOR", "Administración", 2);
        } else {
            // Actualizar contador para los nuevos IDs si ya hay datos
            contador.set((int) usuarioRepository.count());
        }
    }

    private void agregarUsuarioInicial(String email, String pass, String nombre, String rol, String area, Integer sucursalId) {
        String id = generarId();
        Usuario usuario = Usuario.builder()
                .id(id)
                .usuario(email)
                .contrasena(pass)
                .nombre(nombre)
                .rol(rol)
                .area(area)
                .sucursalId(sucursalId)
                .build();
        usuarioRepository.save(usuario);
    }

    private String generarId() {
        return String.format("USR-%03d", contador.incrementAndGet());
    }

    public AuthResponseDTO autenticar(LoginRequestDTO request) {
        Usuario user = usuarioRepository.findByUsuarioAndContrasena(request.getUsuario(), request.getContrasena())
                .orElseThrow(() -> new CustomUnauthorizedException("Credenciales inválidas. Verifique usuario y contraseña."));

        return new AuthResponseDTO(UUID.randomUUID().toString(), user.getNombre(), user.getRol(), user.getArea());
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getUsuario(), u.getNombre(), u.getRol(), u.getArea(), u.getSucursalId()))
                .toList();
    }

    public UsuarioResponseDTO crearUsuario(CrearUsuarioRequestDTO request) {
        boolean emailExiste = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getUsuario().equals(request.getUsuario()));
        if (emailExiste) {
            throw new UsuarioYaExisteException("El usuario '" + request.getUsuario() + "' ya existe.");
        }
        String id = generarId();
        Usuario nuevo = Usuario.builder()
                .id(id)
                .usuario(request.getUsuario())
                .contrasena(request.getContrasena())
                .nombre(request.getNombre())
                .rol(request.getRol())
                .area(request.getArea())
                .sucursalId(request.getSucursalId())
                .build();
        
        usuarioRepository.save(nuevo);
        
        return new UsuarioResponseDTO(nuevo.getId(), nuevo.getUsuario(), nuevo.getNombre(), nuevo.getRol(), nuevo.getArea(), nuevo.getSucursalId());
    }

    public void eliminarUsuario(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
    }
}
