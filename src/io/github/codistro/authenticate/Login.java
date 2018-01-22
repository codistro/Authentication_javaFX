package io.github.codistro.authenticate;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

public class Login {
    private Scene loginScene;
    private TextField user;
    private PasswordField pass;
    private static final String SEPARATOR = "break";
    public Login(){
        //Title
        Text title = new Text("Login");
        title.setFont(Font.font("Verdana", 28));
        title.setFill(Color.RED);

        //Username
        Label userName = new Label("Username:");
        userName.setFont(Font.font("Verdana", 20));

        user = new TextField();

        //Password
        Label password = new Label("Password:");
        password.setFont(Font.font("Verdana", 20));

        pass = new PasswordField();

        //Login
        Button login = new Button("Login");
        login.setFont(Font.font("Verdana", 16));

        //Register
        Button register = new Button("Register");
        register.setFont(Font.font("Verdana", 16));

        Label forgot = new Label("Forgot Username or Password?");
        forgot.setFont(Font.font("Verdana", 12));

        //RootNode
        GridPane root = new GridPane();
        root.setPadding(new Insets(80, 20, 20, 80));
        root.setHgap(10);
        root.setVgap(10);

        //adding controls to GridPane
        root.add(title, 0, 0, 2, 1);
        root.add(userName, 0, 1);
        root.add(user, 1, 1, 2, 1);
        root.add(password, 0, 2);
        root.add(pass, 1, 2, 2, 1);
        root.add(login,1,3);
        root.add(register, 2, 3);
        root.add(forgot,1,4,2,1);

        loginScene = new Scene(root,500,450);

        //setting actions for buttons
        register.setOnAction( v -> Main.window.setScene(new Register().getRegisterScene()));

        //
        forgot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String recoveryEmail = AlertBox.displayTextInput("Enter Details","Enter your e-mail to recover" +
                                                                    " username and password");
                recoverData(recoveryEmail);
            }
        });

        //On click login button
        login.setOnAction( v -> {
            if(isValidData()){
                String data = buildData();

                boolean server = true;
                String result = "";
                try {
                    Client c = new Client();
                    result = c.request(data);
                    String[] ret = result.split(SEPARATOR);
                    result = ret[0];
                } catch (IOException e) {
                    AlertBox.displayError("Server Error","Server went down!");
                    server = false;
                }

                if(result.equals("true") && server){
                    new Dashboard(user.getText().trim());
                }
                else if(server){
                    AlertBox.displayError("Invlaid data","Invalid Username or Password");
                    server = false;
                }
            }
        });
    }

    public void recoverData(String email)  {
        StringBuilder sb = new StringBuilder();
        sb.append(email).append(SEPARATOR).append("2");

        Client c = null;
        String resultData = "";
        try {
            c = new Client();
            resultData = c.request(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        String[] ret = resultData.split(SEPARATOR);
        resultData = ret[0];

        if(resultData.equals("true")){
            String answer = AlertBox.displayTextInput("Answer Security Question",ret[7]);
            if(answer.toLowerCase().equals(ret[8].toLowerCase()))
                AlertBox.displaySuccess("Password Recovered","Your Username is: "+ret[1]+" and Password is: "+ret[2]);
            else
                AlertBox.displayError("Invalid Data","Wrong ANswer!");
        }
        else{
            AlertBox.displayError("Invalid Data","Email doesn't exits.");
        }
    }

    public boolean isValidData(){
        if(user.getText().trim().length() < 3 && pass.getText().length() < 5) {
            AlertBox.displayError("Invalid Data","Invalid Username or Password");
            return false;
        }
        else
            return true;
    }

    public String buildData(){
        StringBuilder sb = new StringBuilder();
        //break is the separator
        sb.append(user.getText()).append(SEPARATOR)
                .append(pass.getText()).append(SEPARATOR)
                .append("1").append(SEPARATOR);

        return sb.toString();
    }

    public Scene getLoginScene() {
        return loginScene;
    }
}
