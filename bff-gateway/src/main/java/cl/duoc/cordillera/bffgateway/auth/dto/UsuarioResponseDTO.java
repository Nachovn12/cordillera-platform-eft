package cl.duoc.cordillera.bffgateway.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponseDTO {
    private String id;
    private String usuario;
    private String nombre;
    private String rol;
    private String area;
}
