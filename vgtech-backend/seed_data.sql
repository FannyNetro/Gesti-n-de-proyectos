-- ============================================
-- VG TECH — Seed Data
-- Ejecutar DESPUÉS del script DDL
-- Credenciales exactas solicitadas preservadas
-- ============================================

-- ============================================
-- USUARIOS (con credenciales reales)
-- ============================================

INSERT INTO usuarios (uid, nombre_completo, email, password_hash, puesto, sueldo, pago_por_hora, dias_vacaciones, fecha_registro, created_at, updated_at) VALUES
-- ── RH ──────────────────────────────────────────────────────────────────
('11111111-0000-0000-0000-000000000001', 'Laura Ramírez Torres',   'admin@vgtech.com',      'admin',  'RH',            25000.00, 156.25, 12.0, NOW(), NOW(), NOW()),
('11111111-0000-0000-0000-000000000002', 'Jorge Herrera Salinas',  'jorge.h@vgtech.com',    'adm2',   'RH',            20000.00, 125.00, 10.0, NOW(), NOW(), NOW()),
('11111111-0000-0000-0000-000000000003', 'Patricia Vela Muñoz',    'patricia.v@vgtech.com', 'adm3',   'ADMINISTRATIVO',18500.00, 115.62, 10.0, NOW(), NOW(), NOW()),

-- ── SUPERVISORES ─────────────────────────────────────────────────────────
('22222222-0000-0000-0000-000000000001', 'Carlos Mendoza Ríos',    'super@vgtech.com',      'super',  'SUPERVISOR',    38000.00, 237.50, 15.0, NOW(), NOW(), NOW()),
('22222222-0000-0000-0000-000000000002', 'Alejandro Fuentes',      'alejandro.f@vgtech.com','sup2',   'SUPERVISOR',    35000.00, 218.75, 14.0, NOW(), NOW(), NOW()),
('22222222-0000-0000-0000-000000000003', 'Daniela Ortiz Peña',     'daniela.o@vgtech.com',  'sup3',   'SUPERVISOR',    33000.00, 206.25, 12.0, NOW(), NOW(), NOW()),

-- ── CONSULTORES ──────────────────────────────────────────────────────────
('33333333-0000-0000-0000-000000000001', 'Fernando Castillo',      'consultor@vgtech.com',  'cons',   'CONSULTOR',     32000.00, 200.00, 12.0, NOW(), NOW(), NOW()),
('33333333-0000-0000-0000-000000000002', 'Ana García López',       'ana@vgtech.com',         'cons2',  'CONSULTOR',     30000.00, 187.50, 12.0, NOW(), NOW(), NOW()),
('33333333-0000-0000-0000-000000000003', 'Roberto Díaz Martín',    'roberto@vgtech.com',     'cons3',  'CONSULTOR',     28000.00, 175.00, 10.0, NOW(), NOW(), NOW()),
('33333333-0000-0000-0000-000000000004', 'Isabel Navarro Vega',    'isabel.n@vgtech.com',    'cons4',  'CONSULTOR',     26500.00, 165.62, 10.0, NOW(), NOW(), NOW()),

-- ── PROVEEDORES ──────────────────────────────────────────────────────────
('44444444-0000-0000-0000-000000000001', 'Constructora Pérez S.A.','proveedor@vgtech.com',  'prov',   'PROVEEDOR',         0.00,   0.00,  0.0, NOW(), NOW(), NOW()),
('44444444-0000-0000-0000-000000000002', 'Materiales del Norte',   'norte@vgtech.com',       'prov2',  'PROVEEDOR',         0.00,   0.00,  0.0, NOW(), NOW(), NOW()),
('44444444-0000-0000-0000-000000000003', 'Electro Servicios MX',   'electro@vgtech.com',     'prov3',  'PROVEEDOR',         0.00,   0.00,  0.0, NOW(), NOW(), NOW()),
('44444444-0000-0000-0000-000000000004', 'Hidráulica Integral',    'hidro@vgtech.com',        'prov4',  'PROVEEDOR',         0.00,   0.00,  0.0, NOW(), NOW(), NOW()),
('44444444-0000-0000-0000-000000000005', 'Arquitectura & Diseño MX','arqdmx@vgtech.com',     'prov5',  'PROVEEDOR',         0.00,   0.00,  0.0, NOW(), NOW(), NOW()),

-- ── CLIENTES ─────────────────────────────────────────────────────────────
('55555555-0000-0000-0000-000000000001', 'Grupo Empresarial León', 'cliente@vgtech.com',    'clie',   'CLIENTE',           0.00,   0.00,  0.0, NOW(), NOW(), NOW());

-- Categorías de trabajo para proveedores
INSERT INTO usuario_categorias_trabajo (usuario_uid, categoria) VALUES
('44444444-0000-0000-0000-000000000001', 'Obra Civil'),
('44444444-0000-0000-0000-000000000001', 'Cimentación'),
('44444444-0000-0000-0000-000000000002', 'Materiales'),
('44444444-0000-0000-0000-000000000002', 'Acabados'),
('44444444-0000-0000-0000-000000000003', 'Eléctrico'),
('44444444-0000-0000-0000-000000000003', 'Iluminación'),
('44444444-0000-0000-0000-000000000004', 'Plomería'),
('44444444-0000-0000-0000-000000000004', 'Hidráulica'),
('44444444-0000-0000-0000-000000000005', 'Diseño'),
('44444444-0000-0000-0000-000000000005', 'Acabados');

-- ============================================
-- PROYECTOS
-- ============================================

INSERT INTO proyectos (id, titulo, descripcion, proveedor_uid, supervisor_uid, consultor_uid, cliente_uid, progreso, estado, calificacion_proveedor, resultado_evaluacion, fecha_inicio, created_at, updated_at) VALUES
-- Finalizados
('aaaaaaaa-0000-0000-0000-000000000101', 'Hospital General – Fase 1',
 'Obra civil e instalaciones iniciales. Presupuesto cliente: $360,000.',
 '44444444-0000-0000-0000-000000000001', '22222222-0000-0000-0000-000000000001',
 '33333333-0000-0000-0000-000000000001', '55555555-0000-0000-0000-000000000001',
 1.0, 'Finalizado', 4.80, 'Aprobado con Distinción', NOW() - INTERVAL '120 days', NOW(), NOW()),

('aaaaaaaa-0000-0000-0000-000000000102', 'Plaza Comercial Sur',
 'Instalaciones eléctricas completas. Presupuesto cliente: $300,000.',
 '44444444-0000-0000-0000-000000000003', '22222222-0000-0000-0000-000000000002',
 '33333333-0000-0000-0000-000000000002', '55555555-0000-0000-0000-000000000001',
 1.0, 'Finalizado', 0, '', NOW() - INTERVAL '100 days', NOW(), NOW()),

('aaaaaaaa-0000-0000-0000-000000000103', 'Centro Educativo Federal',
 'Diseño arquitectónico y acabados. Presupuesto cliente: $220,000.',
 '44444444-0000-0000-0000-000000000005', '22222222-0000-0000-0000-000000000003',
 '33333333-0000-0000-0000-000000000004', '55555555-0000-0000-0000-000000000001',
 1.0, 'Finalizado', 0, '', NOW() - INTERVAL '80 days', NOW(), NOW()),

-- En Progreso
('bbbbbbbb-0000-0000-0000-000000000001', 'Hospital General – Fase 2',
 'Ampliación de instalaciones y sistemas de oxígeno. Presupuesto cliente: $240,000.',
 '44444444-0000-0000-0000-000000000001', '22222222-0000-0000-0000-000000000001',
 '33333333-0000-0000-0000-000000000001', '55555555-0000-0000-0000-000000000001',
 0.75, 'En Progreso', 0, '', NOW() - INTERVAL '45 days', NOW(), NOW()),

('bbbbbbbb-0000-0000-0000-000000000002', 'Residencial Las Lomas',
 'Estructura principal y cimentación. Presupuesto cliente: $500,000.',
 '44444444-0000-0000-0000-000000000002', '22222222-0000-0000-0000-000000000002',
 '33333333-0000-0000-0000-000000000002', '55555555-0000-0000-0000-000000000001',
 0.60, 'En Progreso', 0, '', NOW() - INTERVAL '60 days', NOW(), NOW()),

('bbbbbbbb-0000-0000-0000-000000000003', 'Torre Corporativa Alfa',
 'Diseño, supervisión y suministro de materiales. Presupuesto cliente: $420,000.',
 '44444444-0000-0000-0000-000000000002', '22222222-0000-0000-0000-000000000001',
 '33333333-0000-0000-0000-000000000001', '55555555-0000-0000-0000-000000000001',
 0.35, 'En Progreso', 0, '', NOW() - INTERVAL '40 days', NOW(), NOW()),

('bbbbbbbb-0000-0000-0000-000000000004', 'Parque Industrial Oriente',
 'Red eléctrica e iluminación industrial. Presupuesto cliente: $280,000.',
 '44444444-0000-0000-0000-000000000003', '22222222-0000-0000-0000-000000000003',
 '33333333-0000-0000-0000-000000000002', '55555555-0000-0000-0000-000000000001',
 0.45, 'En Progreso', 0, '', NOW() - INTERVAL '35 days', NOW(), NOW()),

('bbbbbbbb-0000-0000-0000-000000000005', 'Clínica Privada Norte',
 'Sistema hidráulico y plomería completa. Presupuesto cliente: $180,000.',
 '44444444-0000-0000-0000-000000000004', '22222222-0000-0000-0000-000000000002',
 '33333333-0000-0000-0000-000000000004', '55555555-0000-0000-0000-000000000001',
 0.20, 'En Progreso', 0, '', NOW() - INTERVAL '20 days', NOW(), NOW()),

-- Pendiente
('cccccccc-0000-0000-0000-000000000006', 'Estadio Municipal Reforma',
 'Obra civil integral. Pendiente de asignación de proveedor.',
 NULL, '22222222-0000-0000-0000-000000000001',
 NULL, '55555555-0000-0000-0000-000000000001',
 0.0, 'Pendiente', 0, '', NOW() - INTERVAL '5 days', NOW(), NOW());

-- ============================================
-- TRANSACCIONES PROVEEDOR
-- ============================================

INSERT INTO transacciones_proveedor (id, proveedor_id, proyecto_id, timestamp, tipo, monto_bruto, corte_empresa, corte_proveedor, descripcion, created_at) VALUES
('tx000001-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000001','aaaaaaaa-0000-0000-0000-000000000101', NOW()-INTERVAL '90 days','SERVICE',360000,180000,180000,'Hospital General – Fase 1: Obra civil',NOW()),
('tx000002-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000001','aaaaaaaa-0000-0000-0000-000000000101', NOW()-INTERVAL '75 days','PAYMENT',180000,0,0,'Liquidación total – Hospital Fase 1',NOW()),
('tx000003-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000001','bbbbbbbb-0000-0000-0000-000000000001', NOW()-INTERVAL '30 days','SERVICE',240000,120000,120000,'Hospital General – Fase 2: Ampliación',NOW()),
('tx000004-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000001','bbbbbbbb-0000-0000-0000-000000000001', NOW()-INTERVAL '15 days','PAYMENT', 60000,0,0,'Anticipo 50% – Hospital Fase 2',NOW()),
('tx000005-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000002', NOW()-INTERVAL '60 days','SERVICE',500000,250000,250000,'Residencial Las Lomas – Estructura',NOW()),
('tx000006-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000002', NOW()-INTERVAL '45 days','PAYMENT',100000,0,0,'Abono Fase 1/3 – Las Lomas',NOW()),
('tx000007-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000003', NOW()-INTERVAL '40 days','SERVICE',420000,210000,210000,'Torre Corporativa Alfa – Materiales',NOW()),
('tx000008-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000003', NOW()-INTERVAL '20 days','PAYMENT',100000,0,0,'Anticipo inicial – Torre Alfa',NOW()),
('tx000009-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000003','aaaaaaaa-0000-0000-0000-000000000102', NOW()-INTERVAL '80 days','SERVICE',300000,150000,150000,'Plaza Comercial Sur – Eléctrico',NOW()),
('tx000010-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000003','aaaaaaaa-0000-0000-0000-000000000102', NOW()-INTERVAL '60 days','PAYMENT',150000,0,0,'Liquidación total – Plaza Comercial',NOW()),
('tx000011-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000003','bbbbbbbb-0000-0000-0000-000000000004', NOW()-INTERVAL '35 days','SERVICE',280000,140000,140000,'Parque Industrial Oriente – Iluminación',NOW()),
('tx000012-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000003','bbbbbbbb-0000-0000-0000-000000000004', NOW()-INTERVAL '10 days','PAYMENT', 60000,0,0,'Anticipo – Parque Industrial',NOW()),
('tx000013-0000-0000-0000-000000000004','44444444-0000-0000-0000-000000000004','bbbbbbbb-0000-0000-0000-000000000005', NOW()-INTERVAL '20 days','SERVICE',180000, 90000, 90000,'Clínica Privada Norte – Hidráulico',NOW()),
('tx000014-0000-0000-0000-000000000005','44444444-0000-0000-0000-000000000005','aaaaaaaa-0000-0000-0000-000000000103', NOW()-INTERVAL '70 days','SERVICE',220000,110000,110000,'Centro Educativo Federal – Diseño',NOW()),
('tx000015-0000-0000-0000-000000000005','44444444-0000-0000-0000-000000000005','aaaaaaaa-0000-0000-0000-000000000103', NOW()-INTERVAL '50 days','PAYMENT',110000,0,0,'Liquidación total – Centro Educativo',NOW());

-- ============================================
-- FASES DE PAGO
-- ============================================

INSERT INTO fases_pago (proveedor_id, proyecto_id, numero_fase, total_fases, monto_a_pagar, fecha_programada, estado, fecha_pago, created_at, updated_at) VALUES
('44444444-0000-0000-0000-000000000001','bbbbbbbb-0000-0000-0000-000000000001',1,2,60000, NOW()-INTERVAL '15 days','PAGADO', NOW()-INTERVAL '15 days', NOW(), NOW()),
('44444444-0000-0000-0000-000000000001','bbbbbbbb-0000-0000-0000-000000000001',2,2,60000, NOW()+INTERVAL '15 days','PENDIENTE',NULL, NOW(), NOW()),
('44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000002',1,3,100000,NOW()-INTERVAL '45 days','PAGADO', NOW()-INTERVAL '45 days', NOW(), NOW()),
('44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000002',2,3,75000, NOW()+INTERVAL '10 days','PENDIENTE',NULL, NOW(), NOW()),
('44444444-0000-0000-0000-000000000002','bbbbbbbb-0000-0000-0000-000000000002',3,3,75000, NOW()+INTERVAL '30 days','PENDIENTE',NULL, NOW(), NOW());

-- ============================================
-- INVITACIONES Y COTIZACIONES
-- ============================================

INSERT INTO invitaciones_proveedor (id, proyecto_id, proveedor_uid, supervisor_uid, mensaje, fecha, estado, created_at, updated_at) VALUES
('ee000001-0000-0000-0000-000000000001',
 'bbbbbbbb-0000-0000-0000-000000000004',
 '44444444-0000-0000-0000-000000000003',
 '22222222-0000-0000-0000-000000000001',
 'Invitación para cotizar instalaciones eléctricas.',
 NOW()-INTERVAL '40 days','Cotizada', NOW(), NOW()),

('ee000002-0000-0000-0000-000000000001',
 'cccccccc-0000-0000-0000-000000000006',
 '44444444-0000-0000-0000-000000000001',
 '22222222-0000-0000-0000-000000000001',
 'Cotización para obra civil integral del estadio.',
 NOW()-INTERVAL '10 days','Cotizada', NOW(), NOW());

INSERT INTO cotizaciones (id, invitacion_id, proyecto_id, proveedor_uid, monto, dias_estimados, descripcion, enviada_a_cliente, estado_cliente, confirmado_supervisor, fecha, created_at, updated_at) VALUES
('ff000001-0000-0000-0000-000000000001',
 'ee000002-0000-0000-0000-000000000001',
 'cccccccc-0000-0000-0000-000000000006',
 '44444444-0000-0000-0000-000000000001',
 620000, 90,
 'Obra civil integral: excavación, cimentación y estructura. El cliente aprobó el presupuesto.',
 TRUE, 'Aprobada', FALSE,
 NOW()-INTERVAL '5 days', NOW(), NOW()),

('ff000002-0000-0000-0000-000000000001',
 'ee000001-0000-0000-0000-000000000001',
 'bbbbbbbb-0000-0000-0000-000000000004',
 '44444444-0000-0000-0000-000000000003',
 450000, 45,
 'Incluye cableado, tableros e iluminación industrial.',
 TRUE, 'Pendiente', FALSE,
 NOW()-INTERVAL '35 days', NOW(), NOW());

-- ============================================
-- REPORTES DE PROGRESO
-- ============================================

INSERT INTO progreso_proyecto (proyecto_id, proveedor_uid, fecha, porcentaje_avance, descripcion, tipo_reporte, aspectos_positivos, problemas, created_at, updated_at) VALUES
('bbbbbbbb-0000-0000-0000-000000000001','44444444-0000-0000-0000-000000000001', NOW()-INTERVAL '1 day',  85,'Instalación de tuberías de cobre completada.','Diario', 'Avance sin incidentes','Ninguno', NOW(), NOW()),
('bbbbbbbb-0000-0000-0000-000000000002','44444444-0000-0000-0000-000000000002', NOW()-INTERVAL '2 days', 50,'Colado de losa de entrepiso.','Semanal','Buen clima','Retraso en entrega de concreto', NOW(), NOW()),
('bbbbbbbb-0000-0000-0000-000000000003','44444444-0000-0000-0000-000000000002', NOW()-INTERVAL '3 days', 30,'Excavación y preparación de terreno.','Semanal','Terreno estable','Maquinaria con falla menor', NOW(), NOW());

-- ============================================
-- VERIFICACIÓN FINAL
-- ============================================
SELECT 'usuarios' AS tabla, COUNT(*) FROM usuarios
UNION ALL SELECT 'proyectos', COUNT(*) FROM proyectos
UNION ALL SELECT 'transacciones_proveedor', COUNT(*) FROM transacciones_proveedor
UNION ALL SELECT 'fases_pago', COUNT(*) FROM fases_pago
UNION ALL SELECT 'invitaciones_proveedor', COUNT(*) FROM invitaciones_proveedor
UNION ALL SELECT 'cotizaciones', COUNT(*) FROM cotizaciones
UNION ALL SELECT 'progreso_proyecto', COUNT(*) FROM progreso_proyecto;
