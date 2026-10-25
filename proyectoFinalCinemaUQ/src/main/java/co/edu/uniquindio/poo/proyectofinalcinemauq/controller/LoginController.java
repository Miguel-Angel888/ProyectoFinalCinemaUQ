package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.AutenticacionException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Administrador;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Usuario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasenia;
    @FXML private TextField txtNombreRegistro;
    @FXML private TextField txtCorreoRegistro;
    @FXML private PasswordField txtContraseniaRegistro;

    private final CinemaFacade facade = CinemaFacade.getInstance();

    @FXML
    private void onIngresar() {
        try {
            Usuario usuario = facade.iniciarSesion(txtCorreo.getText(), txtContrasenia.getText());
            if (usuario instanceof Administrador) {
                Navegador.ir("admin");
            } else {
                Navegador.ir("cartelera");
            }
        } catch (AutenticacionException e) {
            Alertas.error("No se pudo iniciar sesión", e.getMessage());
        }
    }

    @FXML
    private void onRegistrar() {
        try {
            Cliente cliente = facade.registrarCliente(txtNombreRegistro.getText(),
                    txtCorreoRegistro.getText(), txtContraseniaRegistro.getText());
            Alertas.info("¡Bienvenido, " + cliente.getNombre() + "!",
                    "Tu cuenta fue creada. Tu tarjeta virtual es " + cliente.getTarjetaVirtual().getNumeroTarjeta()
                            + " y empieza con saldo $0; puedes solicitar una recarga desde \"Mi tarjeta\".");
            Navegador.ir("cartelera");
        } catch (AutenticacionException e) {
            Alertas.error("No se pudo registrar", e.getMessage());
        }
    }
}
