package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

import java.time.LocalDate;

public class Administrador extends Usuario {

    //Atributos
    private String codigoEmpleado;

    //Constructor
    public Administrador(String id, String nombre, String correo, String contrasenia,
                         LocalDate fechaRegistro, String codigoEmpleado) {
        super(id, nombre, correo, contrasenia, fechaRegistro);
        this.codigoEmpleado = codigoEmpleado;
    }

    //Getters y setters


    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }
}
