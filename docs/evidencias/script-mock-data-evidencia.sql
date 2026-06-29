-- ==============================================================================
-- GRUPO CORDILLERA - SCRIPT DE EVIDENCIA DE DATOS MOCK
-- ==============================================================================
-- NOTA IMPORTANTE PARA REVISIÓN:
-- Este script es puramente demostrativo y sirve como "evidencia" de los
-- datos mock que exige la regla de negocio.
-- En el proyecto real, NO es necesario ejecutar este script manualmente.
-- Los microservicios están configurados con "DataLoaders" (CommandLineRunner)
-- que insertan estos mismos datos de forma automática en MySQL a través de
-- Hibernate al momento de levantar los contenedores con Docker Compose.
-- ==============================================================================

USE data_db;

-- ------------------------------------------------------------------------------
-- 1. DATA SERVICE: Datos Operacionales consolidados de Sucursales
-- ------------------------------------------------------------------------------
-- Sucursal 1 (Santiago)
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('POS', 'VENTA', '150000', 1);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('POS', 'VENTA', '89990', 1);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('INVENTARIO', 'STOCK', '500', 1);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('FINANZAS', 'INGRESO', '3500000', 1);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('CRM', 'CLIENTE', 'ACTIVO', 1);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('E-COMMERCE', 'PEDIDO', '125000', 1);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('FINANZAS', 'GASTO', '450000', 1);

-- Sucursal 2 (Viña del Mar)
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('POS', 'VENTA', '230000', 2);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('POS', 'VENTA', '45000', 2);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('INVENTARIO', 'STOCK', '320', 2);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('INVENTARIO', 'MERMA', '15', 2);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('FINANZAS', 'INGRESO', '2100000', 2);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('CRM', 'CLIENTE', 'ACTIVO', 2);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('CRM', 'CLIENTE', 'INACTIVO', 2);

-- Sucursal 3 (Concepción)
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('E-COMMERCE', 'PEDIDO', '89000', 3);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('E-COMMERCE', 'PEDIDO', '340000', 3);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('POS', 'VENTA', '120000', 3);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('INVENTARIO', 'STOCK', '410', 3);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('FINANZAS', 'INGRESO', '2800000', 3);
INSERT INTO datos (sistema_origen, tipo_dato, valor, sucursal_id) VALUES ('CRM', 'CLIENTE', 'ACTIVO', 3);


USE kpi_db;

-- ------------------------------------------------------------------------------
-- 2. KPI SERVICE: Indicadores Clave de Desempeño Ejecutivos
-- ------------------------------------------------------------------------------
INSERT INTO kpis (nombre, valor, unidad, categoria, estado) VALUES ('Crecimiento Ventas Omnicanal', 15.5, '%', 'VENTAS', 'ACTIVO');
INSERT INTO kpis (nombre, valor, unidad, categoria, estado) VALUES ('Rotacion de Stock Hogar/Tech', 85.0, '%', 'INVENTARIO', 'ACTIVO');
INSERT INTO kpis (nombre, valor, unidad, categoria, estado) VALUES ('Tasa de Entrega a Tiempo', 92.3, '%', 'LOGISTICA', 'ACTIVO');
INSERT INTO kpis (nombre, valor, unidad, categoria, estado) VALUES ('Margen Bruto General', 35.8, '%', 'RENTABILIDAD', 'ACTIVO');
INSERT INTO kpis (nombre, valor, unidad, categoria, estado) VALUES ('Retencion de Clientes', 76.4, '%', 'CRM', 'ACTIVO');


USE report_db;

-- ------------------------------------------------------------------------------
-- 3. REPORT SERVICE: Historial de Reportes Generados
-- ------------------------------------------------------------------------------
INSERT INTO reportes (titulo, tipo, area, valor, fecha_generacion, anio, mes) 
VALUES ('Reporte Consolidado Semestral Q2', 'CONSOLIDADO', 'DIRECCION', 12500000.00, NOW() - INTERVAL 5 DAY, YEAR(NOW()), MONTH(NOW()));

INSERT INTO reportes (titulo, tipo, area, valor, fecha_generacion, anio, mes) 
VALUES ('Desempeno E-commerce vs POS', 'COMPARATIVO', 'VENTAS', 4580000.00, NOW() - INTERVAL 2 DAY, YEAR(NOW()), MONTH(NOW()));

INSERT INTO reportes (titulo, tipo, area, valor, fecha_generacion, anio, mes) 
VALUES ('Analisis de Quiebre de Stock', 'ANALITICO', 'OPERACIONES', 12.00, NOW() - INTERVAL 1 DAY, YEAR(NOW()), MONTH(NOW()));

INSERT INTO reportes (titulo, tipo, area, valor, fecha_generacion, anio, mes) 
VALUES ('Estado de Resultados Mensual', 'FINANCIERO', 'FINANZAS', 35000000.00, NOW() - INTERVAL 10 HOUR, YEAR(NOW()), MONTH(NOW()));
