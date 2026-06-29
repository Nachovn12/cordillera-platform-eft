# Descripción de la Capa de Persistencia — Grupo Cordillera

**Proyecto:** Plataforma de Monitoreo Organizacional  
**Sprint:** S3 — EP3  
**Equipo:** Grupo Cordillera — Equipo de Desarrollo Backend  

---

## 1. Introducción

El sistema implementa el patrón **Database per Service** con Spring Data JPA: cada microservicio gestiona su propia base de datos MySQL de forma independiente, sin foreign keys entre bases de datos. Esto elimina el common coupling y permite que cada servicio evolucione de forma autónoma.

Los tres schemas MySQL son: \data_db\ (datos operacionales), \kpi_db\ (indicadores) y \eport_db\ (reportes ejecutivos). Spring Data JPA implementa el patrón Repository automáticamente a partir de interfaces Java.

---

## 2. Entidades JPA

### 2.1 Dato.java (data-service → data_db.datos)

\\\java
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
\\\

### 2.2 Kpi.java (kpi-service → kpi_db.kpis)

\\\java
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
\\\

### 2.3 Reporte.java (report-service → report_db.reportes)

\\\java
@Entity
@Table(
    name = "reportes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_reporte_periodo",
            columnNames = {"area", "tipo", "anio", "mes"}
        )
    },
    indexes = {
        @jakarta.persistence.Index(name = "idx_reporte_anio", columnList = "anio"),
        @jakarta.persistence.Index(name = "idx_reporte_area_anio", columnList = "area, anio")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El título del reporte es obligatorio")
  @Column(nullable = false, length = 150)
  private String titulo;

  @NotBlank(message = "El tipo de reporte es obligatorio")
  @Column(nullable = false, length = 50)
  private String tipo;

  @NotBlank(message = "El área del reporte es obligatoria")
  @Column(nullable = false, length = 80)
  private String area;

  @NotNull(message = "El valor del reporte es obligatorio")
  @PositiveOrZero(message = "El valor del reporte no puede ser negativo")
  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal valor;

  @Column(nullable = false)
  private LocalDateTime fechaGeneracion;

  @Column(name = "anio")
  private Integer anio;

  @Column(name = "mes")
  private Integer mes;

  @PrePersist
  public void prePersist() {
    if (this.fechaGeneracion == null) {
      this.fechaGeneracion = LocalDateTime.now();
    }
    if (this.anio == null && this.fechaGeneracion != null) {
      this.anio = this.fechaGeneracion.getYear();
    }
    if (this.mes == null && this.fechaGeneracion != null) {
      this.mes = this.fechaGeneracion.getMonthValue();
    }
  }
}
\\\

---

## 3. Repositorios Spring Data JPA

| Repositorio | Query Methods reales | SQL generado automáticamente |
| :--- | :--- | :--- |
| DatoRepository | findBySistemaOrigen(String) | SELECT * FROM datos WHERE sistema_origen = ? |
| DatoRepository | findBySucursalId(Long) | SELECT * FROM datos WHERE sucursal_id = ? |
| KpiRepository | findByCategoria(String) | SELECT * FROM kpis WHERE categoria = ? |
| ReporteRepository | findByArea(String) | SELECT * FROM reportes WHERE area = ? |
| ReporteRepository | existsByAreaAndTipoAndAnioAndMes(...) | SELECT COUNT(*) FROM reportes WHERE area=? AND tipo=? AND anio=? AND mes=? |

---

## 4. Diagrama ER

![Diagrama ER](diagrama-er.png)

---

## 5. Patrones de Diseño en la Persistencia

- **Repository Pattern**: Spring genera implementaciones SQL a partir de interfaces Java.
- **Database per Service**: 3 schemas independientes sin FK cross-service.
- **@PrePersist**: lógica de dominio en la entidad (fecha auto-asignada).
- **@UniqueConstraint**: idempotencia garantizada a nivel de base de datos.
