package co.edu.uniquindio.poo.proyectofinalcinemauq;

import co.edu.uniquindio.poo.proyectofinalcinemauq.controller.Navegador;
import javafx.application.Application;
import javafx.stage.Stage;

public class CinemaApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("CinemaUQ");
        stage.setMinWidth(1050);
        stage.setMinHeight(700);
        Navegador.iniciar(stage);
        Navegador.ir("login");
        stage.show();
    }
}
