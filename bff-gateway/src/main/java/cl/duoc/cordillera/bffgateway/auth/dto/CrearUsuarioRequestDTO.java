package cl.duoc.cordillera.bffgateway.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearUsuarioRequestDTO {

    @NotBlank(message = "El usuario no puede estar vacío")
    @Email(message = "El usuario debe ser un email válido")
    private String usuario;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasena;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El rol no puede estar vacío")
    private String rol;

    @NotBlank(message = "El área no puede estar vacía")
    private String area;
}
