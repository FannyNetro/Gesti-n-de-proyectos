-- LIMPIEZA PREVIA (Opcional, pero recomendado para evitar duplicados si se corre varias veces)
-- Usamos DELETE en lugar de TRUNCATE para no romper foreign keys si no es necesario, o simplemente insertamos ignorando conflictos.

-- 1. INSERTAR RH (6 usuarios)
INSERT INTO usuarios (uid, nombre_completo, email, puesto, password_hash, activo, sueldo, dias_vacaciones, fecha_registro, created_at, updated_at) VALUES 
('11111111-0000-0000-0000-000000000001', 'Laura Gómez (RH)', 'rh1@vgtech.com', 'RH', 'admin', true, 25000.00, 15.0, now(), now(), now()),
('11111111-0000-0000-0000-000000000002', 'Roberto Díaz (RH)', 'rh2@vgtech.com', 'RH', 'admin', true, 26000.00, 15.0, now(), now(), now()),
('11111111-0000-0000-0000-000000000003', 'Mónica Salas (RH)', 'rh3@vgtech.com', 'RH', 'admin', true, 24000.00, 15.0, now(), now(), now()),
('11111111-0000-0000-0000-000000000004', 'Hugo Torres (RH)', 'rh4@vgtech.com', 'RH', 'admin', true, 27000.00, 15.0, now(), now(), now()),
('11111111-0000-0000-0000-000000000005', 'Silvia Ortiz (RH)', 'rh5@vgtech.com', 'RH', 'admin', true, 28000.00, 15.0, now(), now(), now()),
('11111111-0000-0000-0000-000000000006', 'Andrés Vega (RH)', 'rh6@vgtech.com', 'RH', 'admin', true, 25000.00, 15.0, now(), now(), now())
ON CONFLICT (uid) DO NOTHING;

-- 2. INSERTAR SUPERVISORES (6 usuarios)
INSERT INTO usuarios (uid, nombre_completo, email, puesto, password_hash, activo, sueldo, dias_vacaciones, fecha_registro, created_at, updated_at) VALUES 
('22222222-0000-0000-0000-000000000001', 'Carlos Medina (Sup)', 'sup1@vgtech.com', 'SUPERVISOR', 'super', true, 35000.00, 20.0, now(), now(), now()),
('22222222-0000-0000-0000-000000000002', 'Elena Castro (Sup)', 'sup2@vgtech.com', 'SUPERVISOR', 'super', true, 36000.00, 20.0, now(), now(), now()),
('22222222-0000-0000-0000-000000000003', 'Fernando Ríos (Sup)', 'sup3@vgtech.com', 'SUPERVISOR', 'super', true, 34000.00, 20.0, now(), now(), now()),
('22222222-0000-0000-0000-000000000004', 'Gabriela Luna (Sup)', 'sup4@vgtech.com', 'SUPERVISOR', 'super', true, 37000.00, 20.0, now(), now(), now()),
('22222222-0000-0000-0000-000000000005', 'Héctor Mora (Sup)', 'sup5@vgtech.com', 'SUPERVISOR', 'super', true, 38000.00, 20.0, now(), now(), now()),
('22222222-0000-0000-0000-000000000006', 'Isabel Solís (Sup)', 'sup6@vgtech.com', 'SUPERVISOR', 'super', true, 35000.00, 20.0, now(), now(), now())
ON CONFLICT (uid) DO NOTHING;

-- 3. INSERTAR CONSULTORES (6 usuarios)
INSERT INTO usuarios (uid, nombre_completo, email, puesto, password_hash, activo, sueldo, pago_por_hora, dias_vacaciones, fecha_registro, created_at, updated_at) VALUES 
('33333333-0000-0000-0000-000000000001', 'Julio Silva (Cons)', 'cons1@vgtech.com', 'CONSULTOR', 'cons', true, 0, 300.00, 10.0, now(), now(), now()),
('33333333-0000-0000-0000-000000000002', 'Karla Ponce (Cons)', 'cons2@vgtech.com', 'CONSULTOR', 'cons', true, 0, 320.00, 10.0, now(), now(), now()),
('33333333-0000-0000-0000-000000000003', 'Luis Rojas (Cons)', 'cons3@vgtech.com', 'CONSULTOR', 'cons', true, 0, 310.00, 10.0, now(), now(), now()),
('33333333-0000-0000-0000-000000000004', 'Martha Ruiz (Cons)', 'cons4@vgtech.com', 'CONSULTOR', 'cons', true, 0, 350.00, 10.0, now(), now(), now()),
('33333333-0000-0000-0000-000000000005', 'Natalia Gil (Cons)', 'cons5@vgtech.com', 'CONSULTOR', 'cons', true, 0, 340.00, 10.0, now(), now(), now()),
('33333333-0000-0000-0000-000000000006', 'Óscar Fuentes (Cons)', 'cons6@vgtech.com', 'CONSULTOR', 'cons', true, 0, 330.00, 10.0, now(), now(), now())
ON CONFLICT (uid) DO NOTHING;

-- 4. INSERTAR PROVEEDORES (6 usuarios)
INSERT INTO usuarios (uid, nombre_completo, email, puesto, password_hash, activo, fecha_registro, created_at, updated_at) VALUES 
('44444444-0000-0000-0000-000000000001', 'Constructora Apex', 'prov1@vgtech.com', 'PROVEEDOR', 'prov', true, now(), now(), now()),
('44444444-0000-0000-0000-000000000002', 'Materiales Titan', 'prov2@vgtech.com', 'PROVEEDOR', 'prov', true, now(), now(), now()),
('44444444-0000-0000-0000-000000000003', 'Acabados Premium', 'prov3@vgtech.com', 'PROVEEDOR', 'prov', true, now(), now(), now()),
('44444444-0000-0000-0000-000000000004', 'Herramientas Pro', 'prov4@vgtech.com', 'PROVEEDOR', 'prov', true, now(), now(), now()),
('44444444-0000-0000-0000-000000000005', 'Logística Sur', 'prov5@vgtech.com', 'PROVEEDOR', 'prov', true, now(), now(), now()),
('44444444-0000-0000-0000-000000000006', 'Sistemas Eléctricos SA', 'prov6@vgtech.com', 'PROVEEDOR', 'prov', true, now(), now(), now())
ON CONFLICT (uid) DO NOTHING;

-- 5. INSERTAR CLIENTES (6 usuarios)
INSERT INTO usuarios (uid, nombre_completo, email, puesto, password_hash, activo, fecha_registro, created_at, updated_at) VALUES 
('55555555-0000-0000-0000-000000000001', 'Inmobiliaria Norte', 'cli1@vgtech.com', 'CLIENTE', 'cli', true, now(), now(), now()),
('55555555-0000-0000-0000-000000000002', 'Grupo Residencial', 'cli2@vgtech.com', 'CLIENTE', 'cli', true, now(), now(), now()),
('55555555-0000-0000-0000-000000000003', 'Desarrollos Modernos', 'cli3@vgtech.com', 'CLIENTE', 'cli', true, now(), now(), now()),
('55555555-0000-0000-0000-000000000004', 'Torres de Alba', 'cli4@vgtech.com', 'CLIENTE', 'cli', true, now(), now(), now()),
('55555555-0000-0000-0000-000000000005', 'Plaza Comercial Centro', 'cli5@vgtech.com', 'CLIENTE', 'cli', true, now(), now(), now()),
('55555555-0000-0000-0000-000000000006', 'Corporativo Valle', 'cli6@vgtech.com', 'CLIENTE', 'cli', true, now(), now(), now())
ON CONFLICT (uid) DO NOTHING;

-- 6. ASIGNAR CATEGORÍAS A PROVEEDORES
INSERT INTO usuario_categorias_trabajo (id, usuario_uid, categoria) VALUES 
(gen_random_uuid(), '44444444-0000-0000-0000-000000000001', 'Obra Civil'),
(gen_random_uuid(), '44444444-0000-0000-0000-000000000002', 'Materiales'),
(gen_random_uuid(), '44444444-0000-0000-0000-000000000003', 'Pintura'),
(gen_random_uuid(), '44444444-0000-0000-0000-000000000004', 'Maquinaria'),
(gen_random_uuid(), '44444444-0000-0000-0000-000000000005', 'Transporte'),
(gen_random_uuid(), '44444444-0000-0000-0000-000000000006', 'Eléctrico');

-- 7. CREAR 6 PROYECTOS SURTIDOS (Asignando un supervisor, consultor, cliente y proveedor aleatorio)
INSERT INTO proyectos (id, titulo, descripcion, supervisor_uid, consultor_uid, cliente_uid, proveedor_uid, progreso, estado, fecha_inicio, created_at, updated_at) VALUES 
('66666666-0000-0000-0000-000000000001', 'Torre Corporativa VG', 'Construcción de oficinas centrales', '22222222-0000-0000-0000-000000000001', '33333333-0000-0000-0000-000000000001', '55555555-0000-0000-0000-000000000001', '44444444-0000-0000-0000-000000000001', 0.45, 'En Progreso', now() - interval '60 days', now(), now()),
('66666666-0000-0000-0000-000000000002', 'Residencial Los Álamos', 'Conjunto de 50 casas de lujo', '22222222-0000-0000-0000-000000000002', '33333333-0000-0000-0000-000000000002', '55555555-0000-0000-0000-000000000002', '44444444-0000-0000-0000-000000000002', 0.80, 'En Progreso', now() - interval '120 days', now(), now()),
('66666666-0000-0000-0000-000000000003', 'Plaza Sur', 'Centro comercial local', '22222222-0000-0000-0000-000000000003', '33333333-0000-0000-0000-000000000003', '55555555-0000-0000-0000-000000000003', '44444444-0000-0000-0000-000000000003', 0.10, 'Pendiente', now() - interval '10 days', now(), now()),
('66666666-0000-0000-0000-000000000004', 'Clínica Médica Norte', 'Hospital de especialidades', '22222222-0000-0000-0000-000000000004', '33333333-0000-0000-0000-000000000004', '55555555-0000-0000-0000-000000000004', '44444444-0000-0000-0000-000000000004', 0.95, 'Finalizado', now() - interval '300 days', now(), now()),
('66666666-0000-0000-0000-000000000005', 'Escuela Secundaria Técnica', 'Construcción de aulas', '22222222-0000-0000-0000-000000000005', '33333333-0000-0000-0000-000000000005', '55555555-0000-0000-0000-000000000005', '44444444-0000-0000-0000-000000000005', 0.00, 'Pendiente', now(), now(), now()),
('66666666-0000-0000-0000-000000000006', 'Edificio Departamental Azul', 'Complejo de 10 niveles', '22222222-0000-0000-0000-000000000006', '33333333-0000-0000-0000-000000000006', '55555555-0000-0000-0000-000000000006', '44444444-0000-0000-0000-000000000006', 0.25, 'Pendiente', now() - interval '90 days', now(), now())
ON CONFLICT (id) DO NOTHING;

-- 8. GENERAR TRANSACCIONES Y FASES DE PAGO PARA ESTOS PROYECTOS
INSERT INTO fases_pago (id, proveedor_id, proyecto_id, numero_fase, total_fases, monto_a_pagar, fecha_programada, estado, created_at, updated_at) VALUES 
('77777777-0000-0000-0000-000000000001', '44444444-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000001', 1, 3, 50000.00, now() - interval '10 days', 'PAGADO', now(), now()),
('77777777-0000-0000-0000-000000000002', '44444444-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000001', 2, 3, 50000.00, now() + interval '10 days', 'PENDIENTE', now(), now()),
('77777777-0000-0000-0000-000000000003', '44444444-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000002', 1, 2, 120000.00, now() - interval '30 days', 'PAGADO', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO transacciones_proveedor (id, proveedor_id, proyecto_id, fase_id, timestamp, tipo, monto_bruto, corte_empresa, corte_proveedor, descripcion, created_at) VALUES 
('88888888-0000-0000-0000-000000000001', '44444444-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000001', null, now() - interval '15 days', 'SERVICE', 100000.00, 5000.00, 95000.00, 'Facturación Inicial Cliente', now()),
('88888888-0000-0000-0000-000000000002', '44444444-0000-0000-0000-000000000001', '66666666-0000-0000-0000-000000000001', '77777777-0000-0000-0000-000000000001', now() - interval '10 days', 'PAYMENT', 50000.00, 0.0, 0.0, 'Pago anticipo Fase 1', now()),

('88888888-0000-0000-0000-000000000003', '44444444-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000002', null, now() - interval '35 days', 'SERVICE', 200000.00, 10000.00, 190000.00, 'Facturación Materiales', now()),
('88888888-0000-0000-0000-000000000004', '44444444-0000-0000-0000-000000000002', '66666666-0000-0000-0000-000000000002', '77777777-0000-0000-0000-000000000003', now() - interval '30 days', 'PAYMENT', 120000.00, 0.0, 0.0, 'Materiales base Fase 1', now())
ON CONFLICT (id) DO NOTHING;

-- 9. REGISTRO DE HORAS PARA CONSULTORES Y SUPERVISORES
INSERT INTO registro_horas (id, empleado_uid, fecha, horas_trabajadas, horas_extra, tarifa_extra, tarifa_hora, pago_total, observaciones, created_at) VALUES 
(gen_random_uuid(), '22222222-0000-0000-0000-000000000001', (now() - interval '2 days')::date, 8, 2, 2.0, 218.75, (8 * 218.75) + (2 * 218.75 * 2.0), 'Visita Torre Corporativa', now()),
(gen_random_uuid(), '22222222-0000-0000-0000-000000000001', (now() - interval '1 days')::date, 8, 0, 2.0, 218.75, (8 * 218.75), 'Revisión en oficina', now()),
(gen_random_uuid(), '33333333-0000-0000-0000-000000000001', (now() - interval '3 days')::date, 8, 0, 2.0, 300.00, (8 * 300.00), 'Levantamiento de requisitos', now()),
(gen_random_uuid(), '33333333-0000-0000-0000-000000000001', (now() - interval '2 days')::date, 8, 3, 2.0, 300.00, (8 * 300.00) + (3 * 300.00 * 2.0), 'Trabajo en campo Torre', now()),
(gen_random_uuid(), '33333333-0000-0000-0000-000000000002', (now() - interval '1 days')::date, 8, 0, 2.0, 320.00, (8 * 320.00), 'Supervisión de obra', now());
