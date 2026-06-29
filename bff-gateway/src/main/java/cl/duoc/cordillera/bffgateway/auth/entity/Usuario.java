package cl.duoc.cordillera.bffgateway.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    private String id;
    private String usuario;
    private String contrasena;
    private String nombre;
    private String rol;
    private String area;
    private Integer sucursalId;
}
