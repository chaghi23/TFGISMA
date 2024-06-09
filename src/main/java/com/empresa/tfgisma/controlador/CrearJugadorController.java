package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CrearJugadorController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidoField;

    @FXML
    private TextField posicionField;

    @FXML
    private TextField golesTotalesField;

    @FXML
    private TextField tarjetasAmarillasField;

    @FXML
    private TextField tarjetasRojasField;

    @FXML
    private TextField fechaNacimientoField;

    private int equipoId;

    public void inicializar(int equipoId) {
        this.equipoId = equipoId;
    }

    @FXML
    private void handleCrearJugador(ActionEvent event) {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String posicion = posicionField.getText();
        String golesTotales = golesTotalesField.getText();
        String tarjetasAmarillas = tarjetasAmarillasField.getText();
        String tarjetasRojas = tarjetasRojasField.getText();
        String fechaNacimiento = fechaNacimientoField.getText();

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || posicion.isEmpty() || golesTotales.isEmpty() || tarjetasAmarillas.isEmpty() || tarjetasRojas.isEmpty() || fechaNacimiento.isEmpty()) {
            mostrarAlerta("Campos Vacíos", "Por favor, complete todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        if (!esNumeroValido(golesTotales)) {
            mostrarAlerta("Error en Goles Totales", "Por favor, introduzca un número válido en Goles Totales.", Alert.AlertType.WARNING);
            return;
        }

        if (!esNumeroValido(tarjetasAmarillas)) {
            mostrarAlerta("Error en Tarjetas Amarillas", "Por favor, introduzca un número válido en Tarjetas Amarillas.", Alert.AlertType.WARNING);
            return;
        }

        if (!esNumeroValido(tarjetasRojas)) {
            mostrarAlerta("Error en Tarjetas Rojas", "Por favor, introduzca un número válido en Tarjetas Rojas.", Alert.AlertType.WARNING);
            return;
        }

        if (!esFechaValida(fechaNacimiento)) {
            mostrarAlerta("Error en Fecha de Nacimiento", "Por favor, introduzca una fecha válida en el formato YYYY-MM-DD.", Alert.AlertType.WARNING);
            return;
        }

        // Obtener el siguiente ID
        int nuevoId = obtenerSiguienteId();

        // Insertar en la base de datos
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "INSERT INTO Jugador (id, nombre, apellido, posicion, goles_totales, id_equipo, tarjetas_amarillas, tarjetas_rojas, fecha_nacimiento) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, nuevoId);
            statement.setString(2, nombre);
            statement.setString(3, apellido);
            statement.setString(4, posicion);
            statement.setInt(5, Integer.parseInt(golesTotales));
            statement.setInt(6, equipoId);
            statement.setInt(7, Integer.parseInt(tarjetasAmarillas));
            statement.setInt(8, Integer.parseInt(tarjetasRojas));
            statement.setString(9, fechaNacimiento);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta("Éxito", "Jugador creado correctamente.", Alert.AlertType.INFORMATION);
                Stage stage = (Stage) nombreField.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("Error", "No se pudo crear el jugador.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al crear el jugador.", Alert.AlertType.ERROR);
        }
    }

    private int obtenerSiguienteId() {
        int ultimoId = 0;
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT MAX(id) AS max_id FROM Jugador";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ultimoId = resultSet.getInt("max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al obtener el último ID.", Alert.AlertType.ERROR);
        }
        return ultimoId + 1;
    }

    private boolean esNumeroValido(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean esFechaValida(String fecha) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(fecha, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
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