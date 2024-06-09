module com.empresa.tfgisma {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.empresa.tfgisma.controlador to javafx.fxml;
    opens com.empresa.tfgisma.modelo to javafx.fxml;
    opens com.empresa.tfgisma.vista to javafx.fxml;
    opens com.empresa.tfgisma.conector to javafx.fxml;

    exports com.empresa.tfgisma.controlador;
    exports com.empresa.tfgisma.modelo;
    exports com.empresa.tfgisma.vista;
    exports com.empresa.tfgisma.conector;
}