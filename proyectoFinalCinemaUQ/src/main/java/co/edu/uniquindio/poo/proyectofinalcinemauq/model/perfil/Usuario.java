package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

import java.time.LocalDate;

public abstract class Usuario {

    //Atributos
    private String id;
    private String nombre;
    private String correo;
    private String contrasenia;
    private LocalDate fechaRegistro;

    //Constructor
    public Usuario(String id, String nombre, String correo, String contrasenia, LocalDate fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.fechaRegistro = fechaRegistro;
    }

    //getters y setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
