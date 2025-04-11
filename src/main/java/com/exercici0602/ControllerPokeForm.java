package com.exercici0602;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.utils.UtilsViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControllerPokeForm implements Initializable {

    public static final String STATUS_ADD = "add";
    public static final String STATUS_EDIT = "edit";
    private String modoActual = "";
    private int idPokemon = -1;
    private String rutaImagen = "";

    @FXML
    private Label etiquetaGuardado = new Label();

    @FXML
    private TextField inputNombre = new TextField();

    @FXML
    private TextField inputHabilidad = new TextField();

    @FXML
    private TextField inputCategoria = new TextField();

    @FXML
    private ChoiceBox<String> desplegableTipo = new ChoiceBox<String>();
    final String[] tiposDisponibles = {"Planta/Verí", "Foc", "Foc/Volador", "Aigua", "Insecte", "Insecte/Volador", "Insecte/Verí", "Elèctric"};

    @FXML
    private TextField inputAltura = new TextField();

    @FXML
    private TextField inputPeso = new TextField();

    @FXML
    private ImageView imgBackArrow;

    @FXML
    private ImageView imagenPokemon;

    @FXML
    private Button botonEliminar = new Button();

    @FXML
    private Button botonCrear = new Button();

    @FXML
    private Button botonActualizar = new Button();

    @FXML
    private Button botonSeleccionarImagen = new Button();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            URL iconoGoBack = getClass().getResource("/assets/images0601/arrow-back.png");
            Image icono = new Image(iconoGoBack.toExternalForm());
            imgBackArrow.setImage(icono);
        } catch (Exception e) {
            System.err.println("Error al cargar icono de volver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setStatus(String modo, int numero) {
        this.modoActual = modo;
        this.idPokemon = numero;

        desplegableTipo.getItems().clear();
        desplegableTipo.getItems().addAll(Arrays.asList(tiposDisponibles));

        etiquetaGuardado.setVisible(false);

        if (modoActual.equalsIgnoreCase(STATUS_ADD)) {
            botonEliminar.setVisible(false);
            botonCrear.setVisible(true);
            botonActualizar.setVisible(false);

            inputNombre.clear();
            inputHabilidad.clear();
            inputCategoria.clear();
            desplegableTipo.getSelectionModel().select(tiposDisponibles[0]);
            inputAltura.clear();
            inputPeso.clear();
            imagenPokemon.setImage(null);
            rutaImagen = "";
        }

        if (modoActual.equalsIgnoreCase(STATUS_EDIT)) {
            botonEliminar.setVisible(true);
            botonCrear.setVisible(false);
            botonActualizar.setVisible(true);

            AppData base = AppData.getInstance();
            String consulta = String.format("SELECT * FROM pokemons WHERE number = '%d';", this.idPokemon);
            ArrayList<HashMap<String, Object>> resultado = base.query(consulta);
            if (resultado.size() == 1) {
                HashMap<String, Object> datos = resultado.get(0);

                inputNombre.setText((String) datos.get("name"));
                inputHabilidad.setText((String) datos.get("ability"));
                inputCategoria.setText((String) datos.get("category"));
                desplegableTipo.getSelectionModel().select((String) datos.get("type"));
                inputAltura.setText((String) datos.get("height"));
                inputPeso.setText((String) datos.get("weight"));

                rutaImagen = (String) datos.get("image");
                try {
                    File archivo = new File(rutaImagen);
                    Image imagen = new Image(archivo.toURI().toString());
                    imagenPokemon.setImage(imagen);
                } catch (NullPointerException e) {
                    System.err.println("Error al mostrar imagen: " + rutaImagen);
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void goBack(MouseEvent evento) {
        if (modoActual.equalsIgnoreCase(STATUS_ADD)) {
            ControllerPokeList ctrl = (ControllerPokeList) UtilsViews.getController("ViewList");
            ctrl.loadList();
            UtilsViews.setViewAnimating("ViewList");
        }
        if (modoActual.equalsIgnoreCase(STATUS_EDIT)) {
            ControllerPokeCard ctrl = (ControllerPokeCard) UtilsViews.getController("ViewCard");
            ctrl.loadPokemon(idPokemon);
            UtilsViews.setViewAnimating("ViewCard");
        }
    }

    @FXML
    public void selectFile(ActionEvent evento) {
        Stage ventana = (Stage) botonSeleccionarImagen.getScene().getWindow();
        FileChooser selector = new FileChooser();
        selector.setInitialDirectory(new File(System.getProperty("user.dir")));
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File elegido = selector.showOpenDialog(ventana);
        if (elegido != null) {
            String nombreArchivo = elegido.getName();
            String destino = System.getProperty("user.dir") + "/data/pokeImages/" + nombreArchivo;
            File archivoDestino = new File(destino);
            try {
                Files.copy(elegido.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                rutaImagen = "data/pokeImages/" + nombreArchivo;

                File imagenFile = new File(rutaImagen);
                Image imagen = new Image(imagenFile.toURI().toString());
                imagenPokemon.setImage(imagen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void add(ActionEvent evento) {
        String nombre = inputNombre.getText();
        String tipo = desplegableTipo.getSelectionModel().getSelectedItem();
        String habilidad = inputHabilidad.getText();
        String altura = inputAltura.getText();
        String peso = inputPeso.getText();
        String categoria = inputCategoria.getText();
        String imagen = rutaImagen;

        AppData db = AppData.getInstance();
        String sql = String.format("INSERT INTO pokemons (name, type, ability, height, weight, category, image) VALUES ('%s','%s','%s','%s','%s','%s','%s')", nombre, tipo, habilidad, altura, peso, categoria, imagen);
        db.update(sql);

        setStatus(STATUS_ADD, -1);
        etiquetaGuardado.setVisible(true);
        setTimeout(2500, () -> {
            etiquetaGuardado.setVisible(false);
        });
    }

    @FXML
    public void update(ActionEvent evento) {
        String nombre = inputNombre.getText();
        String tipo = desplegableTipo.getSelectionModel().getSelectedItem();
        String habilidad = inputHabilidad.getText();
        String altura = inputAltura.getText();
        String peso = inputPeso.getText();
        String categoria = inputCategoria.getText();
        String imagen = rutaImagen;

        AppData db = AppData.getInstance();
        String sql = String.format("UPDATE pokemons SET name = '%s', type = '%s', ability = '%s', height = '%s', weight = '%s', category = '%s', image = '%s' WHERE number = '%d'", nombre, tipo, habilidad, altura, peso, categoria, imagen, idPokemon);
        db.update(sql);

        etiquetaGuardado.setVisible(true);
        setTimeout(2500, () -> {
            etiquetaGuardado.setVisible(false);
        });
    }

      private void setTimeout(int milliseconds, Runnable task) {
        Timer timer = new Timer();
        timer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            }, milliseconds
        );
    }

    @FXML
    public void delete(ActionEvent evento) {
        AppData db = AppData.getInstance();
        String sql = String.format("DELETE FROM pokemons WHERE number = '%d'", idPokemon);
        db.update(sql);

        setStatus(STATUS_ADD, -1);
        etiquetaGuardado.setVisible(true);
        setTimeout(2500, () -> {
            etiquetaGuardado.setVisible(false);
        });
    }
}
