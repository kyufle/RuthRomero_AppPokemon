package com.exercici0602;

import java.awt.event.MouseEvent;
import java.io.File;

import com.utils.UtilsViews;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class ControllerPokeItem {
    //declarar les diferents variables que estàn dintre de la vista de viewPokeItem.
    @FXML
    private Label title, subtitle;

    @FXML
    private ImageView img;

    //creació de id, per poder verificar després quin es el pokemon.
    private int id;

    //creació de títol, subtítol, imatge i número, que después s'utilitzarà dintre de ControllerPokeList.
    public void setTitle(String title){
        this.title.setText(title);
    }
    
    public void setSubtitle(String subtitle){
        this.subtitle.setText(subtitle);
    }

    public void setImatge(String imagePath) {
        try {
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            this.img.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Error loading image asset: " + imagePath);
            e.printStackTrace();
        }
    }

    public void setId(int idNumber){
        this.id = idNumber;
    }

    //en el cas de que li donin click a un pokemon ens emportarà fins aquell pokemón.
    @FXML
    public void toViewCard(MouseEvent event) {
        ControllerPokeCard ctrl = (ControllerPokeCard) UtilsViews.getController("ViewCard");
        ctrl.loadPokemon(this.id);
        UtilsViews.setViewAnimating("ViewCard");
    }

}