package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import com.empresa.tfgisma.modelo.Resultado;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultadoController {

    @FXML
    private Button btn_nuevo;
    @FXML
    private Button jugadoresBtn;
    @FXML
    private Button competicionBtn;
    @FXML
    private Button equipoBtn;
    @FXML
    private Button resultadosBtn;
    @FXML
    private Button btn_logout;

    @FXML
    private TableView<Resultado> tablaResultados;

    @FXML
    private TableColumn<Resultado, String> equipoLocalColumn;

    @FXML
    private TableColumn<Resultado, String> equipoVisitanteColumn;

    @FXML
    private TableColumn<Resultado, String> resultadoColumn;

    private int usuarioId;
    private int equipoId;
    private int idRol;

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setUserId(int usuarioId) {
        this.usuarioId = usuarioId;
        // Actualizar la carga de datos de resultados al establecer el usuario
        cargarDatosResultados();
    }

    @FXML
    public void initialize() {
        equipoLocalColumn.setCellValueFactory(new PropertyValueFactory<>("equipoLocal"));
        equipoVisitanteColumn.setCellValueFactory(new PropertyValueFactory<>("equipoVisitante"));
        resultadoColumn.setCellValueFactory(new PropertyValueFactory<>("resultado"));

        // Deshabilitar botones según el rol del usuario
        btn_nuevo.setDisable(true);
        if (idRol == 1) { // Supongamos que 1 es el rol de administrador
            btn_nuevo.setDisable(false);
        }
    }

    private void cargarDatosResultados() {
        // Obtener el ID del equipo asociado al usuario y cargar los resultados si es válido
        obtenerEquipoUsuario();
        if (equipoId != -1) {
            List<Resultado> resultados = obtenerResultadosDelEquipo();
            tablaResultados.getItems().clear();
            tablaResultados.getItems().addAll(resultados);
        } else {
            mostrarAlerta("Error", "No se encontró un equipo para el usuario especificado.", Alert.AlertType.ERROR);
        }
    }

    private void obtenerEquipoUsuario() {
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT id_equipo FROM Usuario WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuarioId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                equipoId = resultSet.getInt("id_equipo");
            } else {
                mostrarAlerta("Error", "No se encontró un equipo para el usuario especificado.", Alert.AlertType.ERROR);
                equipoId = -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al obtener el equipo del usuario.", Alert.AlertType.ERROR);
            equipoId = -1;
        }
    }

    private List<Resultado> obtenerResultadosDelEquipo() {
        List<Resultado> resultados = new ArrayList<>();
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT p.id, " +
                    "eLocal.nombre AS equipoLocal, " +
                    "eVisitante.nombre AS equipoVisitante, " +
                    "r.resultado AS resultado " +
                    "FROM Partido p " +
                    "INNER JOIN Equipo eLocal ON eLocal.id = p.id_equipo_local " +
                    "INNER JOIN Equipo eVisitante ON eVisitante.id = p.id_equipo_visitante " +
                    "INNER JOIN Resultado r ON r.id = p.id_resultado " +
                    "INNER JOIN Equipo usuarioEquipo ON usuarioEquipo.id = ? " +
                    "WHERE (p.id_equipo_visitante = usuarioEquipo.id AND p.id_equipo_local != usuarioEquipo.id) " +
                    "OR (p.id_equipo_local = usuarioEquipo.id AND p.id_equipo_visitante != usuarioEquipo.id)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, equipoId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String equipoLocal = resultSet.getString("equipoLocal");
                String equipoVisitante = resultSet.getString("equipoVisitante");
                String resultado = resultSet.getString("resultado");
                Resultado res = new Resultado(id, equipoLocal, equipoVisitante, resultado);
                resultados.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al cargar los resultados.", Alert.AlertType.ERROR);
        }
        return resultados;
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
        cambiarVentana("/fxml/ventanaResultados.fxml");
    }

    @FXML
    private void irAEquipo(ActionEvent event) {
        cambiarVentana("/fxml/ventanaEquipoDefoultAdmin.fxml");
    }

    @FXML
    private void irACompeticion(ActionEvent event) {
        cambiarVentana("/fxml/ventanaCompeticionAdminCliente.fxml");
    }

    @FXML
    private void irAJugadores(ActionEvent event) {
        cambiarVentana("/fxml/ventanaJugadoresAdmin.fxml");
    }

    @FXML
    private void crearResultado(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaCrearResultadoAdmin.fxml"));
            CrearResultadoController controller = new CrearResultadoController();
            controller.setUserId(usuarioId);
            controller.setResultadoController(this);
            loader.setController(controller);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear Resultado");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tablaResultados.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar los datos de la tabla de resultados
            cargarDatosResultados();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudo cargar la ventana de creación de resultados.", Alert.AlertType.ERROR);
        }
    }

    private void cambiarVentana(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener el controlador de la nueva ventana y pasar el ID del usuario, rol y equipo si es necesario
            Object controller = loader.getController();
            if (controller instanceof EquipoController) {
                ((EquipoController) controller).setUserId(usuarioId);
                ((EquipoController) controller).setIdRol(idRol);
                ((EquipoController) controller).setEquipoId(equipoId);
                ((EquipoController) controller).inicializarConUsuario(usuarioId, idRol, equipoId);
            } else if (controller instanceof ResultadoController) {
                ((ResultadoController) controller).setUserId(usuarioId);
                ((ResultadoController) controller).setIdRol(idRol);
                ((ResultadoController) controller).setEquipoId(equipoId);
            } else if (controller instanceof CompeticionController) {
                ((CompeticionController) controller).setUserId(usuarioId);
                ((CompeticionController) controller).setIdRol(idRol);
                ((CompeticionController) controller).setEquipoId(equipoId);
            } else if (controller instanceof JugadoresController) {
                ((JugadoresController) controller).setUserId(usuarioId);
                ((JugadoresController) controller).setIdRol(idRol);
                ((JugadoresController) controller).setEquipoId(equipoId);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana actual
            Stage stageActual = (Stage) tablaResultados.getScene().getWindow();
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