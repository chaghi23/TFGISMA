package com.empresa.tfgisma.controlador;

import com.empresa.tfgisma.conector.ConexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class EquipoController {

    public Button resultadosBtn;
    public Button equipoBtn;
    public Button competicionBtn;
    public Button jugadoresBtn;
    public Button btn_logout;
    @FXML
    public Button btn_modificar;
    @FXML
    private TextField nombreEquipoTextField;

    @FXML
    private TextField entrenadorTextField;

    @FXML
    private TextField competicionTextField;

    @FXML
    private TextField tipoTerrenoTextField;

    @FXML
    private TextField golesFavorTextField;

    @FXML
    private TextField golesContraTextField;

    @FXML
    private TextField ciudadTextField;


    @FXML
    private Button btn_eliminar;

    @FXML
    private Button btn_nuevo;

    private int usuarioId;
    private int equipoId;

    private int idRol;

    public void inicializarConUsuario(int idUsuario, int rolUsuario, int idEquipo) {
        this.usuarioId = idUsuario;
        this.idRol = rolUsuario;
        this.equipoId = idEquipo;
        obtenerRolUsuario();
        configurarVisibilidadBotones();
        cargarDatosEquipo();
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public void setUserId(int id) {
        this.usuarioId = id;
    }
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    private void configurarVisibilidadBotones() {
        if (idRol == 1) { // Administrador
            btn_modificar.setDisable(false);
            btn_eliminar.setDisable(false);
            btn_nuevo.setDisable(equipoId != -1); // Habilitar solo si idEquipo es -1
            btn_logout.setDisable(false);
            System.out.println("Administrador: botones habilitados"); // Depuración
        } else if (idRol == 2) { // Usuario regular
            btn_modificar.setDisable(true);
            btn_eliminar.setDisable(true);
            btn_nuevo.setDisable(true);
            btn_logout.setDisable(false);
            System.out.println("Usuario regular: solo logout habilitado"); // Depuración
        } else {
            System.out.println("Rol desconocido: todos los botones deshabilitados"); // Depuración
        }
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

    private void cargarDatosEquipo() {
        try (Connection connection = ConexionBD.getConnection()) {
            String query;
            if (idRol == 1) {
                query = "SELECT * FROM Equipo WHERE id_usuario = ?";
            } else {
                query = "SELECT * FROM Equipo e " +
                        "INNER JOIN Usuario u " +
                        "ON u.id_equipo = e.id " +
                        "WHERE u.id = ?";
            }
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, usuarioId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                equipoId = resultSet.getInt("id");
                nombreEquipoTextField.setText(resultSet.getString("nombre"));
                competicionTextField.setText(obtenerNombreCompeticion(resultSet.getInt("id_competicion"), connection));
                tipoTerrenoTextField.setText(obtenerNombreTipoTerreno(resultSet.getInt("id_tipo_terreno"), connection));
                entrenadorTextField.setText(obtenerNombreUsuario(resultSet.getInt("id_usuario"), connection));
                golesFavorTextField.setText(resultSet.getString("goles_favor"));
                golesContraTextField.setText(resultSet.getString("goles_contra"));
                ciudadTextField.setText(obtenerNombreCiudad(resultSet.getInt("id_ciudad"), connection));


                btn_modificar.setVisible(true);
                btn_eliminar.setVisible(true);
                btn_nuevo.setVisible(false);
            } else {
                limpiarFormulario();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al cargar los datos del equipo.", Alert.AlertType.ERROR);
        }
    }

    private String obtenerNombreCompeticion(int idCompeticion, Connection connection) throws SQLException {
        String query = "SELECT nombre FROM Competicion WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idCompeticion);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getString("nombre") : "";
    }

    private String obtenerNombreTipoTerreno(int idTipoTerreno, Connection connection) throws SQLException {
        String query = "SELECT nombre_terreno FROM tipo_terreno WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idTipoTerreno);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getString("nombre_terreno") : "";
    }

    private String obtenerNombreUsuario(int idUsuario, Connection connection) throws SQLException {
        String query = "SELECT nombre FROM usuario WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idUsuario);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getString("nombre") : "";
    }

    private String obtenerNombreCiudad(int idCiudad, Connection connection) throws SQLException {
        String query = "SELECT nombre FROM ciudad WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idCiudad);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getString("nombre") : "";
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

    private void cambiarVentana(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof EquipoController) {
                ((EquipoController) controller).inicializarConUsuario(usuarioId, equipoId, idRol);
            } else if (controller instanceof ResultadoController) {
                ((ResultadoController) controller).setUserId(usuarioId);
            } else if (controller instanceof CompeticionController) {
                ((CompeticionController) controller).setUserId(usuarioId);
            } else if (controller instanceof JugadoresController) {
                ((JugadoresController) controller).setUserId(usuarioId);
                ((JugadoresController) controller).setIdRol(idRol);
                ((JugadoresController) controller).setEquipoId(equipoId);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana actual
            Stage stageActual = (Stage) nombreEquipoTextField.getScene().getWindow();
            stageActual.close();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de carga", "No se pudo cargar la ventana: " + rutaFXML, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarEquipo(ActionEvent event) {
        try (Connection connection = ConexionBD.getConnection()) {
            int entrenadorId = obtenerIdUsuario(entrenadorTextField.getText(), connection);
            int competicionId = obtenerIdCompeticion(competicionTextField.getText(), connection);
            int tipoTerrenoId = obtenerIdTipoTerreno(tipoTerrenoTextField.getText(), connection);
            int ciudadId = obtenerIdCiudad(ciudadTextField.getText(), connection);

            String updateQuery = "UPDATE Equipo SET nombre = ?, id_usuario = ?, id_competicion = ?, id_tipo_terreno = ?, goles_favor = ?, goles_contra = ?, id_ciudad = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, nombreEquipoTextField.getText());
            statement.setInt(2, entrenadorId);
            statement.setInt(3, competicionId);
            statement.setInt(4, tipoTerrenoId);
            statement.setInt(5, Integer.parseInt(golesFavorTextField.getText()));
            statement.setInt(6, Integer.parseInt(golesContraTextField.getText()));
            statement.setInt(7, ciudadId);
            statement.setInt(8, equipoId);
            statement.executeUpdate();

            mostrarAlerta("Éxito", "Equipo modificado correctamente.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al modificar el equipo.", Alert.AlertType.ERROR);
        }
    }

    private int obtenerIdUsuario(String nombreUsuario, Connection connection) throws SQLException {
        String query = "SELECT id FROM usuario WHERE nombre = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nombreUsuario);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            throw new SQLException("No se encontró el usuario: " + nombreUsuario);
        }
    }

    private int obtenerIdCompeticion(String nombreCompeticion, Connection connection) throws SQLException {
        String query = "SELECT id FROM Competicion WHERE nombre = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nombreCompeticion);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            throw new SQLException("No se encontró la competición: " + nombreCompeticion);
        }
    }

    private int obtenerIdTipoTerreno(String nombreTipoTerreno, Connection connection) throws SQLException {
        String query = "SELECT id FROM tipo_terreno WHERE nombre_terreno = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nombreTipoTerreno);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id");
        } else {
            throw new SQLException("No se encontró el tipo de terreno: " + nombreTipoTerreno);
        }
    }

    private int obtenerIdCiudad(String nombreCiudad, Connection connection) throws SQLException {
        int idCiudad = -1;

        String consulta = "SELECT id FROM ciudad WHERE nombre = ?";
        try (PreparedStatement statement = connection.prepareStatement(consulta)) {
            statement.setString(1, nombreCiudad);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    idCiudad = resultSet.getInt("id");
                } else {
                    idCiudad = crearCiudad(nombreCiudad, "España", connection);
                }
            }
        }
        return idCiudad;
    }

    private int crearCiudad(String nombreCiudad, String pais, Connection connection) throws SQLException {
        String consulta = "INSERT INTO ciudad (nombre, pais) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, nombreCiudad);
            statement.setString(2, pais);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la ciudad recién creada");
                }
            }
        }
    }

    @FXML
    private void eliminarEquipo(ActionEvent event) {
        try (Connection connection = ConexionBD.getConnection()) {
            String deleteQuery = "DELETE FROM Equipo WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setInt(1, equipoId);
            statement.executeUpdate();

            actualizarIdEquipoUsuario(-1, connection);

            mostrarAlerta("Éxito", "Equipo eliminado correctamente.", Alert.AlertType.INFORMATION);

            limpiarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al eliminar el equipo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void crearEquipo(ActionEvent event) {
        try (Connection connection = ConexionBD.getConnection()) {
            String insertQuery = "INSERT INTO Equipo (nombre, id_usuario, id_competicion, id_tipo_terreno, goles_favor, goles_contra, id_ciudad) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, nombreEquipoTextField.getText());
            statement.setInt(2, usuarioId);
            statement.setInt(3, obtenerIdCompeticion(competicionTextField.getText(), connection));
            statement.setInt(4, obtenerIdTipoTerreno(tipoTerrenoTextField.getText(), connection));
            statement.setInt(5, Integer.parseInt(golesFavorTextField.getText()));
            statement.setInt(6, Integer.parseInt(golesContraTextField.getText()));
            statement.setInt(7, obtenerIdCiudad(ciudadTextField.getText(), connection));
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                equipoId = generatedKeys.getInt(1);
            }

            actualizarIdEquipoUsuario(equipoId, connection);

            mostrarAlerta("Éxito", "Equipo creado correctamente.", Alert.AlertType.INFORMATION);

            cargarDatosEquipo();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de base de datos", "Error al crear el equipo.", Alert.AlertType.ERROR);
        }
    }

    private void actualizarIdEquipoUsuario(int idEquipo, Connection connection) throws SQLException {
        String updateQuery = "UPDATE usuario SET id_equipo = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, idEquipo);
            statement.setInt(2, usuarioId);
            statement.executeUpdate();
        }
    }

    private void limpiarFormulario() {
        nombreEquipoTextField.clear();
        entrenadorTextField.clear();
        competicionTextField.clear();
        tipoTerrenoTextField.clear();
        golesFavorTextField.clear();
        golesContraTextField.clear();
        ciudadTextField.clear();

        btn_modificar.setVisible(false);
        btn_eliminar.setVisible(false);
        btn_nuevo.setVisible(idRol == 1);
        configurarVisibilidadBotones();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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
