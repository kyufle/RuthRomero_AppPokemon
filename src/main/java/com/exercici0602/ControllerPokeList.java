package com.exercici0602;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class ControllerPokeList implements Initializable {

    @FXML
    private VBox list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadList();
    }

    public void loadList() {
        AppData db = AppData.getInstance();
        db.connect("./data/pokemons.sqlite");

        ArrayList<HashMap<String, Object>> llistaPokemons = db.query("SELECT * FROM pokemons;");
        System.out.println("Número de pokemons: " + llistaPokemons.size());

        try {
            setPokemons(llistaPokemons);
        } catch (Exception e) {
            System.out.println("Error al cargar pokemons:");
            e.printStackTrace();
        }
    }

    private void setPokemons(ArrayList<HashMap<String, Object>> llistaPokemons) throws IOException {
        URL resource = getClass().getResource("/assets/viewPokeItem.fxml");
        list.getChildren().clear();

        for (HashMap<String, Object> pokemon : llistaPokemons) {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent itemTemplate = loader.load();
            ControllerPokeItem itemController = loader.getController();

            int id = (int) pokemon.get("number");
            String name = (String) pokemon.get("name");
            String type = (String) pokemon.get("type");
            String imagePath = (String) pokemon.get("image");

            itemController.setId(id);
            itemController.setTitle(name);
            itemController.setSubtitle(type);
            itemController.setImatge("./data/pokeImages/"+imagePath);

            list.getChildren().add(itemTemplate);
        }
    }

    @FXML
    private void addPokemon(ActionEvent event) {
        System.out.println("Botón 'Add' presionado");
    }
}
