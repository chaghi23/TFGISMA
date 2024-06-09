package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usuarioTextField;

    @FXML
    private PasswordField contrasenaPasswordField;

    @FXML
    private Button loginButton;

    @FXML
    public void ingresar(ActionEvent event) {
        String email = usuarioTextField.getText();
        String contrasenia = contrasenaPasswordField.getText();

        // Verificar si el campo de email o contraseña está vacío
        if (email.isEmpty() || contrasenia.isEmpty()) {
            mostrarAlerta("Inicio de sesión fallido", "Por favor, complete todos los campos.", Alert.AlertType.ERROR);
            return;
        }

        if (validarLogin(email, contrasenia)) {
            // Redirigir a la ventana por defecto del equipo
            int idUsuario = obtenerIdUsuarioPorEmail(email);
            int rolUsuario = obtenerRolUsuarioPorEmail(email);
            if (idUsuario != -1 && rolUsuario != -1) {
                abrirVentanaEquipo(idUsuario, rolUsuario);
            } else {
                mostrarAlerta("Error", "ID o rol de usuario no encontrado.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Inicio de sesión fallido", "Correo electrónico o contraseña incorrectos.", Alert.AlertType.ERROR);
        }
    }

    private boolean validarLogin(String email, String contrasenia) {
        try (Connection conexion = ConexionBD.getConnection()) {
            String consulta = "SELECT * FROM Usuario WHERE email = ? AND contrasenia = ?";
            PreparedStatement declaracion = conexion.prepareStatement(consulta);
            declaracion.setString(1, email);
            declaracion.setString(2, contrasenia);
            ResultSet resultado = declaracion.executeQuery();

            return resultado.next();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al conectar con la base de datos.", Alert.AlertType.ERROR);
            return false;
        }
    }

    private int obtenerIdUsuarioPorEmail(String email) {
        try (Connection conexion = ConexionBD.getConnection()) {
            String consulta = "SELECT id FROM Usuario WHERE email = ?";
            PreparedStatement declaracion = conexion.prepareStatement(consulta);
            declaracion.setString(1, email);
            ResultSet resultado = declaracion.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al conectar con la base de datos.", Alert.AlertType.ERROR);
            return -1;
        }
    }

    private int obtenerRolUsuarioPorEmail(String email) {
        try (Connection conexion = ConexionBD.getConnection()) {
            String consulta = "SELECT id_rol FROM Usuario WHERE email = ?";
            PreparedStatement declaracion = conexion.prepareStatement(consulta);
            declaracion.setString(1, email);
            ResultSet resultado = declaracion.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("id_rol");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al conectar con la base de datos.", Alert.AlertType.ERROR);
            return -1;
        }
    }

    private void abrirVentanaEquipo(int idUsuario, int rolUsuario) {
        try {
            // Obtener el ID del equipo asignado al usuario
            int idEquipo = obtenerIdEquipoAsignadoAlUsuario(usuarioTextField.getText());

            // Cargar el archivo FXML y configurar la escena
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaEquipoDefoultAdmin.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la ventana de equipo y pasar el idUsuario, rolUsuario y idEquipo
            EquipoController controller = loader.getController();
            controller.inicializarConUsuario(idUsuario, rolUsuario, idEquipo);

            // Configurar el escenario
            Stage stage = new Stage();
            stage.setTitle("Ventana de Equipo");
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana de inicio de sesión
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "Error al cargar la ventana del equipo.", Alert.AlertType.ERROR);
        }
    }

    private int obtenerIdEquipoAsignadoAlUsuario(String email) {
        try (Connection conexion = ConexionBD.getConnection()) {
            String consulta = "SELECT id_equipo FROM Usuario WHERE email = ?";
            PreparedStatement declaracion = conexion.prepareStatement(consulta);
            declaracion.setString(1, email);
            ResultSet resultado = declaracion.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("id_equipo");
            } else {
                return -1; // O cualquier valor que indique que no se encontró un equipo asignado
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al conectar con la base de datos.", Alert.AlertType.ERROR);
            return -1;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
