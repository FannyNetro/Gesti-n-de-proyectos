package com.realestate.models;

/**
 * Modelo: Proveedor (Constructora)
 *
 * Representa a una empresa constructora registrada en el sistema.
 *
 * FUTURO - TABLA SQL SERVER:
 * CREATE TABLE Proveedores (
 *     id           INT IDENTITY(1,1) PRIMARY KEY,
 *     nombre       NVARCHAR(200) NOT NULL,
 *     rfc          NVARCHAR(13)  NOT NULL UNIQUE,
 *     nombreContacto NVARCHAR(150) NOT NULL,
 *     telefono     NVARCHAR(15)  NOT NULL,
 *     fechaRegistro DATETIME DEFAULT GETDATE()
 * );
 */
public class Proveedor {

    private int    id;
    private String nombre;
    private String rfc;
    private String nombreContacto;
    private String telefono;

    // ─── Constructores ──────────────────────────────────────────────────────
    public Proveedor() {}

    public Proveedor(String nombre, String rfc, String nombreContacto, String telefono) {
        this.nombre         = nombre;
        this.rfc            = rfc;
        this.nombreContacto = nombreContacto;
        this.telefono       = telefono;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNombre()               { return nombre; }
    public void setNombre(String nombre)    { this.nombre = nombre; }

    public String getRfc()                  { return rfc; }
    public void setRfc(String rfc)          { this.rfc = rfc; }

    public String getNombreContacto()                       { return nombreContacto; }
    public void setNombreContacto(String nombreContacto)    { this.nombreContacto = nombreContacto; }

    public String getTelefono()                 { return telefono; }
    public void setTelefono(String telefono)    { this.telefono = telefono; }

    @Override
    public String toString() {
        return "Proveedor{id=" + id + ", nombre='" + nombre + "', rfc='" + rfc + "'}";
    }
}
