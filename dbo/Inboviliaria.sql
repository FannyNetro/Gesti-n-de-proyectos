-- ============================================================
--  VGtech - Sistema de Gestión Inmobiliaria
--  Base de datos: Inboviliaria
--  Generado: 2026-03-01
--  Servidor: SQL Server (localhost)
-- ============================================================

-- Crear base de datos si no existe
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'Inboviliaria')
BEGIN
    CREATE DATABASE Inboviliaria;
    PRINT '✓ Base de datos Inboviliaria creada.';
END
GO

USE Inboviliaria;
GO

-- ============================================================
--  TABLA: Usuarios
-- ============================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Usuarios')
BEGIN
    CREATE TABLE dbo.Usuarios (
        UsuarioID     INT           IDENTITY(1,1) PRIMARY KEY,
        NombreUsuario VARCHAR(50)   NOT NULL,
        Contrasena    VARCHAR(255)  NOT NULL,
        Rol           VARCHAR(50)   NOT NULL,
        FechaCreacion DATETIME      DEFAULT (GETDATE())
    );
    PRINT '✓ Tabla Usuarios creada.';
END
GO

-- ============================================================
--  TABLA: Empleados
-- ============================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Empleados')
BEGIN
    CREATE TABLE dbo.Empleados (
        EmpleadoID      INT            IDENTITY(1,1) PRIMARY KEY,
        NombreCompleto  NVARCHAR(200)  NOT NULL,
        Direccion       NVARCHAR(300)  NULL,
        Telefono        VARCHAR(20)    NULL,
        PuestoAsignado  VARCHAR(100)   NOT NULL,
        Sueldo          DECIMAL(18,2)  NOT NULL,
        Foto            VARCHAR(500)   NULL,
        FechaRegistro   DATETIME       DEFAULT (GETDATE())
    );
    PRINT '✓ Tabla Empleados creada.';
END
GO

-- ============================================================
--  USUARIO DE APLICACIÓN
-- ============================================================
IF NOT EXISTS (SELECT name FROM sys.server_principals WHERE name = 'vgtechpro')
BEGIN
    CREATE LOGIN vgtechpro WITH PASSWORD = 'vgtech',
        DEFAULT_DATABASE = Inboviliaria,
        CHECK_POLICY = ON,
        CHECK_EXPIRATION = OFF;
END
GO

IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'vgtechpro')
BEGIN
    CREATE USER vgtechpro FOR LOGIN vgtechpro;
END
GO

GRANT SELECT, INSERT, UPDATE ON dbo.Usuarios  TO vgtechpro;
GRANT SELECT, INSERT, UPDATE, DELETE ON dbo.Empleados TO vgtechpro;
GO

-- ============================================================
--  DATOS INICIALES: Usuarios
-- ============================================================
IF NOT EXISTS (SELECT 1 FROM dbo.Usuarios WHERE NombreUsuario = 'admin_rh')
BEGIN
    INSERT INTO dbo.Usuarios (NombreUsuario, Contrasena, Rol)
    VALUES ('admin_rh', '1234', 'RH');
    PRINT '✓ Usuario admin_rh insertado.';
END
GO

PRINT '';
PRINT '════════════════════════════════════════════════════════';
PRINT '  BASE DE DATOS Inboviliaria CONFIGURADA CORRECTAMENTE  ';
PRINT '════════════════════════════════════════════════════════';
PRINT '  Usuario app: vgtechpro  /  Contraseña: vgtech         ';
PRINT '  Usuario RH:  admin_rh   /  Contraseña: 1234           ';
PRINT '════════════════════════════════════════════════════════';
GO
