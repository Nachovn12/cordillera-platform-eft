package cl.duoc.cordillera.bffgateway.auth.repository;

import cl.duoc.cordillera.bffgateway.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByUsuarioAndContrasena(String usuario, String contrasena);
}
