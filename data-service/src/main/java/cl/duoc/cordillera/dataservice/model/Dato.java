package cl.duoc.cordillera.dataservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "datos")
public class Dato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El sistema de origen no puede estar vacío")
    @Column(name = "sistema_origen", nullable = false)
    private String sistemaOrigen;

    @NotBlank(message = "El tipo de dato no puede estar vacío")
    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato;

    @NotBlank(message = "El valor no puede estar vacío")
    @Column(nullable = false)
    private String valor;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @NotNull(message = "El ID de sucursal no puede ser nulo")
    @Column(name = "sucursal_id", nullable = false)
    private Long sucursalId;

    @PrePersist
    protected void onCrear() {
        this.fechaRegistro = LocalDateTime.now();
    }
}