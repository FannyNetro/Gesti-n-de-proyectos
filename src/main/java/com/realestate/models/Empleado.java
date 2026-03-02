package com.realestate.models;

/**
 * Modelo: Empleado (Personal interno de VG Tech)
 *
 * Representa a un empleado registrado por el área de RH.
 *
 * ══════════════════════════════════════════════════════════
 * FUTURO — DDL TABLA SQL SERVER:
 * ══════════════════════════════════════════════════════════
 * CREATE TABLE Empleados (
 *     id             INT IDENTITY(1,1) PRIMARY KEY,
 *     nombreCompleto NVARCHAR(200)  NOT NULL,
 *     direccion      NVARCHAR(300)  NOT NULL,
 *     telefono       NVARCHAR(15)   NOT NULL,
 *     puesto         NVARCHAR(50)   NOT NULL,   -- 'Consultor','Supervisor','Administrativo'
 *     sueldo         DECIMAL(12,2)  NOT NULL,
 *     rutaFoto       NVARCHAR(500)  NULL,        -- ruta relativa al archivo guardado
 *     fechaRegistro  DATETIME DEFAULT GETDATE()
 * );
 * ══════════════════════════════════════════════════════════
 */
public class Empleado {

    private int    id;
    private String nombreCompleto;
    private String direccion;
    private String telefono;
    private String puesto;
    private String sueldo;
    private String nombreFoto;     // nombre del archivo de foto (o null si no se subió)

    // ─── Constructores ──────────────────────────────────────────────────────
    public Empleado() {}

    public Empleado(String nombreCompleto, String direccion, String telefono,
                    String puesto, String sueldo, String nombreFoto) {
        this.nombreCompleto = nombreCompleto;
        this.direccion      = direccion;
        this.telefono       = telefono;
        this.puesto         = puesto;
        this.sueldo         = sueldo;
        this.nombreFoto     = nombreFoto;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────
    public int    getId()                           { return id; }
    public void   setId(int id)                     { this.id = id; }

    public String getNombreCompleto()               { return nombreCompleto; }
    public void   setNombreCompleto(String v)       { this.nombreCompleto = v; }

    public String getDireccion()                    { return direccion; }
    public void   setDireccion(String v)            { this.direccion = v; }

    public String getTelefono()                     { return telefono; }
    public void   setTelefono(String v)             { this.telefono = v; }

    public String getPuesto()                       { return puesto; }
    public void   setPuesto(String v)               { this.puesto = v; }

    public String getSueldo()                       { return sueldo; }
    public void   setSueldo(String v)               { this.sueldo = v; }

    public String getNombreFoto()                   { return nombreFoto; }
    public void   setNombreFoto(String v)           { this.nombreFoto = v; }

    @Override
    public String toString() {
        return "Empleado{id=" + id + ", nombre='" + nombreCompleto
             + "', puesto='" + puesto + "', sueldo=" + sueldo + "}";
    }
}
