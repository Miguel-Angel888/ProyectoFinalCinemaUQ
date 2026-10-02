module co.edu.uniquindio.poo.proyectofinalcinemauq {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;


    opens co.edu.uniquindio.poo.proyectofinalcinemauq to javafx.fxml;
    exports co.edu.uniquindio.poo.proyectofinalcinemauq;
}