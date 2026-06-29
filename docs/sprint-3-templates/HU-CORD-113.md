# HU-CORD-113 — Registrar dato operacional desde sistema externo (POS/ERP/CRM)

## Historia de Usuario

Como sistema externo POS/ERP/CRM del Grupo Cordillera
quiero registrar un dato operacional (venta, inventario, finanzas, CRM) via API REST
para consolidar la información dispersa de las sucursales en data_db y disponibilizarla para KPIs y reportes ejecutivos

## Contexto Técnico e Integración

Flujo end-to-end: Cliente externo hace POST http://localhost:8081/api/v1/datos al BFF Gateway (DatosProxyController.crear), que internamente hace POST http://data-service:8083/api/datos a DatoController.crear, que delega en DatoService.crear para persistencia en MySQL data_db, tabla datos.

Payload esperado: { sistemaOrigen: POS, tipoDato: VENTA, valor: 125000, sucursalId: 1 }

Validaciones Bean Validation en Dato: @NotBlank sistemaOrigen, @NotBlank tipoDato, @NotBlank valor, @NotNull sucursalId. Manejo global via GlobalExceptionHandler.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Repository Pattern** sobre Spring Data JPA: DatoRepository extends JpaRepository abstrae el acceso a data_db.
- **BFF/API Gateway**: el frontend NUNCA llega a data-service:8083; único contrato visible es /api/v1/datos en BFF:8081.
- **Database per Service**: Dato persiste en data_db, separada de kpi_db y report_db (elimina common coupling).
- **Bounded Context (DDD)**: Dato es el aggregate raíz del contexto Datos Operacionales.

## Criterios de Aceptación (Gherkin)

**AC1: Caso feliz de registro de venta POS**

```
Dado que el BFF está levantado en localhost:8081 y data-service conectado a data_db
Cuando el sistema POS envía POST http://localhost:8081/api/v1/datos con { sistemaOrigen:POS, tipoDato:VENTA, valor:125000, sucursalId:1 }
Entonces el BFF retorna 201 Created con el body completo del Dato persistido (incluyendo id autogenerado y fechaRegistro en ISO-8601)
Y la fila queda persistida verificable con SELECT * FROM datos WHERE id=<id>
Y fechaRegistro se setea automáticamente via @PrePersist
```

**AC2: Validación Bean Validation rechaza payload inválido**

```
Dado que el endpoint exige @NotBlank en sistemaOrigen, tipoDato, valor y @NotNull en sucursalId
Cuando el cliente envía POST con sucursalId: null o cualquier campo obligatorio vacío
Entonces el GlobalExceptionHandler responde 400 Bad Request con un body estructurado que lista los campos violados
Y no se persiste ninguna fila en data_db.datos
Y el BFF propaga el 400 al cliente sin transformarlo en 500
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `DatoController.java` | Verificar | Asegurar @Valid en crear() y actualizar() |
| `DatoService.java` | Sin cambios funcionales | Sin cambios funcionales |
| `GlobalExceptionHandler.java` | Verificar | Confirmar mapeo MethodArgumentNotValidException a 400 |
| `application-test.properties` | Crear | Con H2 en memoria |
| `DatoRepositoryTest.java` | Crear | Con @DataJpaTest |
| `DatoServiceTest.java` | Crear | Con Mockito |
| `DatoControllerTest.java` | Crear | Con @WebMvcTest y MockMvc |
| `data-service/pom.xml` | Verificar | Asegurar com.h2database:h2 con scope test |

## Estrategia de Testing (Cobertura > 60%)

- **DatoRepositoryTest**: save y findById, findBySistemaOrigen, findBySucursalId
- **DatoServiceTest**: crear() caso feliz, actualizar() con id inexistente lanza excepción, eliminar() invoca deleteById
- **DatoControllerTest**: POST válido retorna 201 con cuerpo, POST con sucursalId nulo retorna 400 con detalle de validación
- Mapeo CA: AC1 → DatoControllerTest.crear_conPayloadValido_retorna201(); AC2 → DatoControllerTest.crear_conSucursalIdNulo_retorna400()

## Definición de Hecho (DoD)

- [ ] mvn -pl data-service compile sin errores
- [ ] mvn -pl data-service test BUILD SUCCESS, los 3 archivos de test verdes
- [ ] Cobertura JaCoCo mayor a 60% en el paquete cl.duoc.cordillera.dataservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-134]: Crear DatoRepositoryTest.java con @DataJpaTest y H2

Crear el archivo src/test/java/.../DatoRepositoryTest.java anotado con @DataJpaTest. Configurar application-test.properties para usar H2 en memoria (spring.datasource.url=jdbc:h2:mem:testdb). Implementar tests: (1) save_debeRetornarDatoConId: guardar un Dato y verificar que el id generado no sea null; (2) findBySistemaOrigen_debeRetornarLista: guardar 2 datos con sistemaOrigen=POS y verificar que findBySistemaOrigen("POS") retorna lista de size=2; (3) findBySucursalId_debeRetornarLista: verificar filtro por sucursalId. Alineado a Indicador 4 rubrica EP3 (cobertura >=60% con JaCoCo) y patrón Repository Pattern (CASO sección 1).

### Sub-task 2 [CORD-135]: Crear DatoServiceTest.java con Mockito

Crear DatoServiceTest.java con @ExtendWith(MockitoExtension.class). Mockear DatoRepository con @Mock. IMPORTANTE: DatoService.buscarPorId() lanza NoSuchElementException (NOT DatoNoEncontradoException — revisar DatoService.java real). Los métodos reales del service son: crear(), actualizar(Long id, Dato dato), eliminar(Long id), buscarPorSistemaOrigen(), buscarPorSucursalId(). Implementar: (1) crear_conPayloadValido_debePersistirYRetornar: dado un Dato con sistemaOrigen="POS", tipoDato="VENTA", valor="125000", sucursalId=1L; mockear datoRepository.save(any()) para retornar el dato con id=1L; verificar con verify(datoRepository, times(1)).save(dato); (2) actualizar_conIdInexistente_debeLanzarNoSuchElementException: mockear findById(9999L) para retornar Optional.empty(), verificar assertThrows(NoSuchElementException.class); (3) eliminar_debeInvocarDeleteById: mockear findById(1L) para retornar Optional.of(datoExistente), verificar verify(datoRepository).deleteById(1L). Cubre la lógica de negocio del Service (Indicador 4 EP3).

### Sub-task 3 [CORD-136]: Crear DatoControllerTest.java con @WebMvcTest y MockMvc

Crear DatoControllerTest.java con @WebMvcTest(DatoController.class). Mockear DatoService con @MockBean. Implementar tests MockMvc: (1) crear_conPayloadValido_retorna201: POST /api/datos con body {"sistemaOrigen":"POS","tipoDato":"VENTA","valor":"125000","sucursalId":1} debe retornar HTTP 201 y body JSON con id; (2) crear_conSucursalIdNulo_retorna400: POST con sucursalId:null debe retornar HTTP 400 con detalle de campos violados (validación @NotNull/@NotBlank via GlobalExceptionHandler); (3) listar_retorna200ConLista: GET /api/datos debe retornar HTTP 200 con array JSON. Cubre AC1 y AC2 de la historia (Indicadores 2 y 4 EP3).
