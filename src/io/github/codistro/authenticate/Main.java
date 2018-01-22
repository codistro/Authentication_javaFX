package io.github.codistro.authenticate;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    public static Stage window;
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setScene(new Login().getLoginScene());
        window.setResizable(false);
        window.show();
    }
}
