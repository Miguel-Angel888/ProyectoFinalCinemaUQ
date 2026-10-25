package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.CinemaApp;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.AutenticacionException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Cambia la vista que se muestra en la ventana principal
public final class Navegador {

    private static Stage stage;

    private Navegador() { }

    public static void iniciar(Stage ventana) {
        stage = ventana;
    }

    public static void ir(String vista) {
        try {
            FXMLLoader loader = new FXMLLoader(CinemaApp.class.getResource("view/" + vista + ".fxml"));
            Parent root = loader.load();
            if (stage.getScene() == null) {
                Scene scene = new Scene(root, 1150, 720);
                scene.getStylesheets().add(CinemaApp.class.getResource("estilos.css").toExternalForm());
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + vista, e);
        }
    }

    // Las vistas del cliente solo se abren después de iniciar sesión
    static Cliente clienteActual() {
        try {
            return CinemaFacade.getInstance().getClienteActual();
        } catch (AutenticacionException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
