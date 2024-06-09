package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import com.empresa.tfgisma.modelo.Competicion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompeticionController {

    @FXML
    private TableView<Competicion> tablaCompeticion;

    @FXML
    private TableColumn<Competicion, String> colNombreEquipo;

    private int usuarioId;
    private int idRol;
    private int equipoId;
    private int competicionId;

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setUserId(int usuarioId) {
        this.usuarioId = usuarioId;
        obtenerEquipoIdYCompeticionId();
        obtenerRolUsuario();
        cargarDatosCompeticion();
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }


    @FXML
    public void initialize() {
        colNombreEquipo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void obtenerRolUsuario() {
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT id_rol FROM usuario WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.idRol = resultSet.getInt("id_rol");
            } else {
                throw new SQLException("Usuario no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al obtener el rol del usuario.", Alert.AlertType.ERROR);
        }
    }

    private void obtenerEquipoIdYCompeticionId() {
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT id_equipo FROM usuario WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.equipoId = resultSet.getInt("id_equipo");

                String competicionQuery = "SELECT id_competicion FROM equipo WHERE id = ?";
                PreparedStatement competicionStatement = connection.prepareStatement(competicionQuery);
                competicionStatement.setInt(1, equipoId);
                ResultSet competicionResultSet = competicionStatement.executeQuery();

                if (competicionResultSet.next()) {
                    this.competicionId = competicionResultSet.getInt("id_competicion");
                } else {
                    throw new SQLException("Equipo no encontrado.");
                }
            } else {
                throw new SQLException("Usuario no tiene equipo asociado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al obtener el equipo o la competición del usuario.", Alert.AlertType.ERROR);
        }
    }

    private void cargarDatosCompeticion() {
        try (Connection connection = ConexionBD.getConnection()) {
            String query;
            query = "SELECT e.nombre FROM Equipo e WHERE e.id_competicion = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, competicionId);
            ResultSet resultSet = statement.executeQuery();

            List<Competicion> competiciones = new ArrayList<>();
            while (resultSet.next()) {
                Competicion competicion = new Competicion();
                competicion.setNombre(resultSet.getString("nombre"));
                competiciones.add(competicion);
            }
            tablaCompeticion.getItems().clear();
            tablaCompeticion.getItems().addAll(competiciones);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al cargar los datos de la competición.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void irAResultados(ActionEvent event) {
        cambiarVentana("/fxml/ventanaResultados.fxml", event);
    }

    @FXML
    private void irAEquipo(ActionEvent event) {
        cambiarVentana("/fxml/ventanaEquipoDefoultAdmin.fxml", event);
    }

    @FXML
    private void irACompeticion(ActionEvent event) {
        cambiarVentana("/fxml/ventanaCompeticionAdminCliente.fxml", event);
    }

    @FXML
    private void irAJugadores(ActionEvent event) {
        cambiarVentana("/fxml/ventanaJugadoresAdmin.fxml", event);
    }

    private void cambiarVentana(String rutaFXML, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener el controlador de la nueva ventana y pasar el ID del usuario si es necesario
            Object controller = loader.getController();
            if (controller instanceof EquipoController) {
                ((EquipoController) controller).inicializarConUsuario(usuarioId, equipoId, idRol);
            } else if (controller instanceof ResultadoController) {
                ((ResultadoController) controller).setUserId(usuarioId);
            } else if (controller instanceof CompeticionController) {
                ((CompeticionController) controller).setUserId(usuarioId);
            } else if (controller instanceof JugadoresController) {
                ((JugadoresController) controller).setUserId(usuarioId);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana actual
            Stage stageActual = (Stage) tablaCompeticion.getScene().getWindow();
            stageActual.close();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudo cargar la ventana: " + rutaFXML, Alert.AlertType.ERROR);
        }
    }

    public void logout(ActionEvent actionEvent) {
        try {
            Parent loginParent = FXMLLoader.load(getClass().getResource("/fxml/ventanaLogin.fxml"));
            Scene loginScene = new Scene(loginParent, 700, 600);
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

