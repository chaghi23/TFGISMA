package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import com.empresa.tfgisma.modelo.Equipo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrearResultadoController {

    @FXML
    private ComboBox<Equipo> comboRival;

    @FXML
    private ComboBox<String> comboResultado;

    @FXML
    private Label lblError;

    private int usuarioId;

    private ResultadoController resultadoController;

    public void setResultadoController(ResultadoController resultadoController) {
        this.resultadoController = resultadoController;
    }

    public void setUserId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @FXML
    private void initialize() {
        cargarEquiposEnComboBox();
        cargarResultadosEnComboBox();
    }

    private void cargarEquiposEnComboBox() {
        List<Equipo> equipos = obtenerEquiposExceptoUsuario();
        comboRival.getItems().addAll(equipos);
    }

    private List<Equipo> obtenerEquiposExceptoUsuario() {
        List<Equipo> equipos = new ArrayList<>();
        try (Connection connection = ConexionBD.getConnection()) {
            // Obtener la competición del equipo del usuario
            String queryCompeticion = "SELECT id_competicion FROM Equipo WHERE id_usuario = ?";
            PreparedStatement statementCompeticion = connection.prepareStatement(queryCompeticion);
            statementCompeticion.setInt(1, usuarioId);
            ResultSet resultSetCompeticion = statementCompeticion.executeQuery();
            if (!resultSetCompeticion.next()) {
                throw new SQLException("No se encontró el equipo del usuario.");
            }
            int idCompeticion = resultSetCompeticion.getInt("id_competicion");

            // Obtener los equipos de la misma competición excepto el del usuario
            String queryEquipos = "SELECT * FROM Equipo WHERE id_competicion = ? AND id_usuario != ?";
            PreparedStatement statementEquipos = connection.prepareStatement(queryEquipos);
            statementEquipos.setInt(1, idCompeticion);
            statementEquipos.setInt(2, usuarioId);
            ResultSet resultSetEquipos = statementEquipos.executeQuery();

            while (resultSetEquipos.next()) {
                int id = resultSetEquipos.getInt("id");
                String nombre = resultSetEquipos.getString("nombre");
                Equipo equipo = new Equipo(id, nombre);
                equipos.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al cargar los equipos.", Alert.AlertType.ERROR);
        }
        return equipos;
    }

    private void cargarResultadosEnComboBox() {
        comboResultado.getItems().addAll("Victoria", "Empate", "Derrota");
    }

    @FXML
    public void crearResultado() {
        Equipo rivalSeleccionado = comboRival.getValue();
        String resultadoSeleccionado = comboResultado.getValue();

        if (rivalSeleccionado == null || resultadoSeleccionado == null) {
            lblError.setText("Por favor, seleccione un equipo rival y un resultado");
            return;
        }

        int idResultado = 0;
        switch (resultadoSeleccionado) {
            case "Victoria":
                idResultado = 1;
                break;
            case "Empate":
                idResultado = 2;
                break;
            case "Derrota":
                idResultado = 3;
                break;
        }

        try (Connection connection = ConexionBD.getConnection()) {
            // Verificar que el usuarioId existe en la tabla Equipo
            String queryVerificarEquipo = "SELECT id FROM Equipo WHERE id_usuario = ?";
            PreparedStatement statementVerificarEquipo = connection.prepareStatement(queryVerificarEquipo);
            statementVerificarEquipo.setInt(1, usuarioId);
            ResultSet resultSetEquipo = statementVerificarEquipo.executeQuery();
            if (!resultSetEquipo.next()) {
                throw new SQLException("El equipo local con usuarioId " + usuarioId + " no existe.");
            }
            int equipoLocalId = resultSetEquipo.getInt("id");

            // Insertar el nuevo partido
            String query = "INSERT INTO Partido (id_equipo_local, id_equipo_visitante, id_resultado) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, equipoLocalId);  // Usa el id del equipo en lugar del usuarioId
            statement.setInt(2, rivalSeleccionado.getId());
            statement.setInt(3, idResultado);
            statement.executeUpdate();

            mostrarAlerta("Éxito", "El partido se ha creado correctamente.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al crear el partido. " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private ResultadoController obtenerResultadoController() {
        // Obtener la escena actual
        Scene scene = comboRival.getScene(); // Cambia comboRival por cualquier nodo presente en la ventana actual

        // Obtener el FXMLLoader de la ventana actual
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaResultadosAdmin.fxml"));

        // Intentar cargar la raíz y configurar el controlador
        try {
            Parent root = loader.load();
            ResultadoController resultadoController = loader.getController();
            return resultadoController;
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudo cargar la ventana de resultados.", Alert.AlertType.ERROR);
            return null;
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