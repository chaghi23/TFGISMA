package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import com.empresa.tfgisma.modelo.Jugador;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModificarJugadorController {

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

    private Jugador jugador;

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
        cargarDatosJugador();
    }

    private void cargarDatosJugador() {
        nombreField.setText(jugador.getNombre());
        apellidoField.setText(jugador.getApellido());
        posicionField.setText(jugador.getPosicion());
        golesTotalesField.setText(String.valueOf(jugador.getGolesTotales()));
        tarjetasAmarillasField.setText(String.valueOf(jugador.getTarjetasAmarillas()));
        tarjetasRojasField.setText(String.valueOf(jugador.getTarjetasRojas()));
        fechaNacimientoField.setText(jugador.getFechaNacimiento());
    }

    @FXML
    private void handleModificarJugador() {
        if (validarCampos()) {
            jugador.setNombre(nombreField.getText());
            jugador.setApellido(apellidoField.getText());
            jugador.setPosicion(posicionField.getText());
            jugador.setGolesTotales(Integer.parseInt(golesTotalesField.getText()));
            jugador.setTarjetasAmarillas(Integer.parseInt(tarjetasAmarillasField.getText()));
            jugador.setTarjetasRojas(Integer.parseInt(tarjetasRojasField.getText()));
            jugador.setFechaNacimiento(fechaNacimientoField.getText());

            if (actualizarJugadorEnBD(jugador)) {
                mostrarAlerta("Éxito", "Jugador modificado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo modificar el jugador.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validarCampos() {
        // Aquí puedes añadir validaciones adicionales si es necesario
        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || posicionField.getText().isEmpty()
                || golesTotalesField.getText().isEmpty() || tarjetasAmarillasField.getText().isEmpty()
                || tarjetasRojasField.getText().isEmpty() || fechaNacimientoField.getText().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, rellene todos los campos.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private boolean actualizarJugadorEnBD(Jugador jugador) {
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "UPDATE Jugador SET nombre = ?, apellido = ?, posicion = ?, goles_totales = ?, tarjetas_amarillas = ?, tarjetas_rojas = ?, fecha_nacimiento = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, jugador.getNombre());
            statement.setString(2, jugador.getApellido());
            statement.setString(3, jugador.getPosicion());
            statement.setInt(4, jugador.getGolesTotales());
            statement.setInt(5, jugador.getTarjetasAmarillas());
            statement.setInt(6, jugador.getTarjetasRojas());
            statement.setString(7, jugador.getFechaNacimiento());
            statement.setInt(8, jugador.getId());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
}