package io.github.codistro.authenticate;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;


public class Dashboard {
    public Dashboard(String user){
        Label text = new Label("Welcome "+user);
        text.setFont(Font.font("Verdana", 20));
        StackPane sp = new StackPane();
        sp.getChildren().add(text);
        Scene scene = new Scene(sp,500,450);
        Main.window.setScene(scene);
    }
}
