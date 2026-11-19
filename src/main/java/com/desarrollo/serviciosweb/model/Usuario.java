package com.desarrollo.serviciosweb.model;

/**
 * Cliente devuelto por la tabla "customers" de Supabase (REST), con los campos que muestra la tabla.
 * Los nombres de las propiedades coinciden con los PropertyValueFactory del FXML.
 */
public class Usuario {
    private Integer id;
    private String nombre;     // first_name
    private String apellido;   // last_name
    private String email;      // email
    private String telefono;   // phone
    private String ciudad;     // city
    private String pais;       // country
    private String estado;     // status

    public Usuario() {
    }

    public Usuario(Integer id, String nombre, String apellido, String email,
                   String telefono, String ciudad, String pais, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + " " + apellido + "', email='" + email + "'}";
    }
}
