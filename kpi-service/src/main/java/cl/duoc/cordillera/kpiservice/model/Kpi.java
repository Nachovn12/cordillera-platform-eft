package cl.duoc.cordillera.kpiservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "kpis")
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del KPI es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El valor del KPI es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
    @Column(nullable = false)
    private BigDecimal valor;

    @NotBlank(message = "La unidad del KPI es obligatoria")
    @Column(nullable = false)
    private String unidad;

    @NotBlank(message = "La categoría del KPI es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotBlank(message = "El estado del KPI es obligatorio")
    @Column(nullable = false)
    private String estado;

    public Kpi() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}