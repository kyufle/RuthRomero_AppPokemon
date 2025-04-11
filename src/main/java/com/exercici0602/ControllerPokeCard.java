package com.exercici0602;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.utils.UtilsViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ControllerPokeCard implements Initializable {

    @FXML
    private Label lblHabilidad;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblTipo;
    @FXML
    private Label lblPeso;
    @FXML
    private Label lblCategoria;
    @FXML
    private Label lblAltura;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;

    private int actualNumber;
    private int numeroAnterior = -1;
    private int numeroSiguiente = -1;

    @FXML
    private ImageView imgBackArrow;
    @FXML
    private ImageView imgPokemon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            URL icono = getClass().getResource("/assets/images0601/arrow-back.png");
            if (icono != null) {
                imgBackArrow.setImage(new Image(icono.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Error loading image");
            e.printStackTrace();
        }
    }

    public void loadPokemon(int numero) {
        this.actualNumber = numero;
        AppData baseDatos = AppData.getInstance();

        ArrayList<HashMap<String, Object>> resultado = baseDatos.query(
                String.format("SELECT * FROM pokemons WHERE number = %d;", this.actualNumber)
        );

        if (!resultado.isEmpty()) {
            HashMap<String, Object> poke = resultado.get(0);

            lblHabilidad.setText((String) poke.get("ability"));
            lblNombre.setText(this.actualNumber + " " + poke.get("name"));
            lblTipo.setText((String) poke.get("type"));
            lblCategoria.setText((String) poke.get("category"));
            lblAltura.setText((String) poke.get("height"));
            lblPeso.setText((String) poke.get("weight"));

            try {
                String rutaImagen = (String) poke.get("image");
                File file = new File("./data/pokeImages/" + rutaImagen);
                Image image = new Image(file.toURI().toString());
                imgPokemon.setImage(image);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen del Pokémon: " + poke.get("image"));
                e.printStackTrace();
            }
        }

        // Busca l'anterior pokemón.
        resultado = baseDatos.query(
                String.format("SELECT * FROM pokemons WHERE number < %d ORDER BY number DESC LIMIT 1;", actualNumber)
        );
        //en el cas que no estigui buït significa que existeix, llavors agafa el número de la sentencia anterior.
        if (!resultado.isEmpty()) {
            numeroAnterior = (int) resultado.get(0).get("number");
            //i mostra el botó per poder-li donar.
            btnAnterior.setDisable(false);
        } else {
            //desactiva el botó perquè no hi ha anterior.
            numeroAnterior = -1;
            btnAnterior.setDisable(true);
        }

        // Buscar el següent pokemón.
        resultado = baseDatos.query(
                String.format("SELECT * FROM pokemons WHERE number > %d ORDER BY number ASC LIMIT 1;", actualNumber)
        );
        //lo mateix que l'anterior pero agafant el següent número.
        if (!resultado.isEmpty()) {
            numeroSiguiente = (int) resultado.get(0).get("number");
            btnSiguiente.setDisable(false);
        } else {
            //desactiva el botó perquè no hi ha següent.
            numeroSiguiente = -1;
            btnSiguiente.setDisable(true);
        }
    }

    @FXML
    public void editPokemon(ActionEvent evt) {
        ControllerPokeForm formCtrl = (ControllerPokeForm) UtilsViews.getController("ViewForm");
        formCtrl.setStatus(ControllerPokeForm.STATUS_EDIT, this.actualNumber);
        UtilsViews.setViewAnimating("ViewForm");
    }

    @FXML
    public void previous(ActionEvent evt) {
        if (this.numeroAnterior != -1) {
            loadPokemon(this.numeroAnterior);
        }
    }

    @FXML
    public void next(ActionEvent evt) {
        if (this.numeroSiguiente != -1) {
            loadPokemon(this.numeroSiguiente);
        }
    }

    @FXML
    public void goBack(MouseEvent evt) {
        ControllerPokeList ctrl = (ControllerPokeList) UtilsViews.getController("ViewTaula");
        ctrl.loadList();
        UtilsViews.setViewAnimating("ViewTaula");
    }
}
