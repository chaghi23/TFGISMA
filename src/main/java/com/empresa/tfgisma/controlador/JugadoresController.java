package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import com.empresa.tfgisma.modelo.Jugador;
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

public class JugadoresController {

    public Button resultadosBtn;
    public Button equipoBtn;
    public Button competicionBtn;
    public Button jugadoresBtn;
    @FXML
    private TableView<Jugador> tablaJugadores;

    @FXML
    private TableColumn<Jugador, String> colNombre;

    @FXML
    private TableColumn<Jugador, String> colApellido;

    @FXML
    private TableColumn<Jugador, Integer> colGoles;

    @FXML
    private Button btn_eliminar;

    @FXML
    private Button btn_nuevo;

    @FXML
    private Button btn_modificar;

    @FXML
    private Button btn_logout;

    private int usuarioId;
    private int idRol;
    private int equipoId;

    public void setUserId(int usuarioId) {
        this.usuarioId = usuarioId;
        cargarDatosJugadores();
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
        ajustarBotonesSegunRol();
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    private void ajustarBotonesSegunRol() {
        if (idRol == 1) {
            btn_eliminar.setVisible(true);
            btn_modificar.setVisible(true);
            btn_nuevo.setVisible(true);
        } else if (idRol == 2) {
            btn_eliminar.setVisible(false);
            btn_modificar.setVisible(false);
            btn_nuevo.setVisible(false);
        }
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colGoles.setCellValueFactory(new PropertyValueFactory<>("golesTotales"));
    }

    private void cargarDatosJugadores() {
        List<Jugador> jugadores = obtenerJugadoresDelUsuario();
        tablaJugadores.getItems().clear();
        tablaJugadores.getItems().addAll(jugadores);
    }

    private List<Jugador> obtenerJugadoresDelUsuario() {
        List<Jugador> jugadores = new ArrayList<>();
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT j.id, j.nombre, j.apellido, j.posicion, j.goles_totales, j.tarjetas_amarillas, j.tarjetas_rojas, j.fecha_nacimiento FROM Jugador j " +
                    "WHERE j.id_equipo = (SELECT id_equipo FROM Usuario WHERE id = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                String posicion = resultSet.getString("posicion");
                int golesTotales = resultSet.getInt("goles_totales");
                int tarjetasAmarillas = resultSet.getInt("tarjetas_amarillas");
                int tarjetasRojas = resultSet.getInt("tarjetas_rojas");
                String fechaNacimiento = resultSet.getString("fecha_nacimiento");
                Jugador jugador = new Jugador(id, nombre, apellido, posicion, golesTotales, tarjetasAmarillas, tarjetasRojas, fechaNacimiento);
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al cargar los jugadores.", Alert.AlertType.ERROR);
        }
        return jugadores;
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
    private void crearJugador(ActionEvent event) {
        int equipoId = obtenerEquipoIdPorUsuario(usuarioId);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaCrearJugadorAdmin.fxml"));
            Parent root = loader.load();

            CrearJugadorController controller = loader.getController();
            controller.inicializar(equipoId);

            Stage stage = new Stage();
            stage.setTitle("Crear Jugador");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tablaJugadores.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarDatosJugadores();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudo cargar la ventana de creación de jugadores.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarJugador(ActionEvent event) {
        Jugador jugadorSeleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
        if (jugadorSeleccionado != null) {
            try (Connection connection = ConexionBD.getConnection()) {
                String query = "DELETE FROM Jugador WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, jugadorSeleccionado.getId());
                int filasAfectadas = statement.executeUpdate();

                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", "Jugador eliminado correctamente.", Alert.AlertType.INFORMATION);
                    cargarDatosJugadores();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el jugador.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error de base de datos", "Error al eliminar el jugador.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección requerida", "Por favor, seleccione un jugador para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void modificarEquipo(ActionEvent event) {
        Jugador jugadorSeleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
        if (jugadorSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventanaModificarJugadorAdmin.fxml"));
                Parent root = loader.load();

                ModificarJugadorController controller = loader.getController();
                controller.setJugador(jugadorSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Modificar Jugador");
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(tablaJugadores.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.showAndWait();

                cargarDatosJugadores();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error de carga", "No se pudo cargar la ventana de modificación del jugador.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección requerida", "Por favor, seleccione un jugador para modificar.", Alert.AlertType.WARNING);
        }
    }

    private void cambiarVentana(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof EquipoController) {
                ((EquipoController) controller).setUserId(usuarioId);
                ((EquipoController) controller).setIdRol(idRol);
                ((EquipoController) controller).inicializarConUsuario(usuarioId, equipoId, idRol);
            } else if (controller instanceof ResultadoController) {
                ((ResultadoController) controller).setUserId(usuarioId);
                ((ResultadoController) controller).setIdRol(idRol);
            } else if (controller instanceof CompeticionController) {
                ((CompeticionController) controller).setUserId(usuarioId);
                ((CompeticionController) controller).setIdRol(idRol);
            } else if (controller instanceof JugadoresController) {
                ((JugadoresController) controller).setUserId(usuarioId);
                ((JugadoresController) controller).setIdRol(idRol);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage stageActual = (Stage) tablaJugadores.getScene().getWindow();
            stageActual.close();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudo cargar la ventana: " + rutaFXML, Alert.AlertType.ERROR);
        }
    }

    private int obtenerEquipoIdPorUsuario(int usuarioId) {
        int equipoId = 0;
        try (Connection connection = ConexionBD.getConnection()) {
            String query = "SELECT id_equipo FROM Usuario WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                equipoId = resultSet.getInt("id_equipo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al obtener el ID del equipo.", Alert.AlertType.ERROR);
        }
        return equipoId;
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