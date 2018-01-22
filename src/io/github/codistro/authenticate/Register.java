package io.github.codistro.authenticate;


import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register {

    private Scene registerScene;
    private String data;
    private static final String SEPARATOR = "break";
    private TextField user, first, last, emailField, answer;
    private PasswordField pass, confirmPass;
    private ComboBox<String> question;
    private Button register, login;
    private DatePicker date;
    public Register() {
        //Title
        Text title = new Text("Register Here");
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

        //Confirm Password
        Label confirmPassword = new Label("Confirm Password:");
        confirmPassword.setFont(Font.font("Verdana", 20));

        confirmPass = new PasswordField();

        //First Name
        Label firstName = new Label("First Name:");
        firstName.setFont(Font.font("Verdana", 20));

        first = new TextField();

        //Last Name
        Label lastName = new Label("Last Name:");
        lastName.setFont(Font.font("Verdana", 20));

        last = new TextField();

        //E-mail
        Label email = new Label("E-mail:");
        email.setFont(Font.font("Verdana", 20));

        emailField = new TextField();

        //DOB
        Label dob = new Label("DOB:");
        dob.setFont(Font.font("Verdana", 20));
        date = new DatePicker();

        //Security Question
        Label ques = new Label("Security Question:");
        ques.setFont(Font.font("Verdana", 20));

        question = new ComboBox<>();
        question.getItems().addAll("Place of Birth?",
                "First pet's name?");
        question.setValue("Select:");

        //Answer
        Label ans = new Label("Answer:");
        ans.setFont(Font.font("Verdana", 20));

        answer = new TextField();

        //Register
        register = new Button("Register");
        register.setFont(Font.font("Verdana", 16));

        //Login
        login = new Button("Login");
        login.setFont(Font.font("Verdana", 16));

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
        root.add(confirmPassword, 0, 3);
        root.add(confirmPass, 1, 3, 2, 1);
        root.add(firstName, 0, 4);
        root.add(first, 1, 4, 2, 1);
        root.add(lastName, 0, 5);
        root.add(last, 1, 5, 2, 1);
        root.add(email, 0, 6);
        root.add(emailField, 1, 6, 2, 1);
        root.add(dob, 0, 7);
        root.add(date, 1, 7, 2, 1);
        root.add(ques, 0, 8);
        root.add(question, 1, 8, 2, 1);
        root.add(ans, 0, 9);
        root.add(answer, 1, 9, 2, 1);
        root.add(register, 1, 10);
        root.add(login,2,10);

        registerScene = new Scene(root, 500, 600);

        //setting actions for buttons
        login.setOnAction( v -> Main.window.setScene(new Login().getLoginScene()));

        //Getting data from the fields
        register.setOnAction(v -> {

            if(validate()) { //if form data is valid then only send request
                //Generating data Sequence
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

                if (result.equals("true") && server) {
                    AlertBox.displaySuccess("Registeration Successful", "Redirecting to Login Page");
                    Main.window.setScene(new Login().getLoginScene());
                } else if(server) {
                    AlertBox.displayWarning("Registeration Unsuccessful", "User Already Exists");
                }
            }
        });
    }

    public boolean validate(){
        StringBuilder content = new StringBuilder();
        boolean flag = true;
        if(user.getText().trim().equals("") || user.getText().trim().length() < 3){
            content.append("-Username must be atleat 3 characters.\n");
            flag = false;
        }
        if(pass.getText().trim().length() <= 5){
            content.append("-Password must be atleast 5 characters.\n");
            flag = false;
        }
        if(!pass.getText().trim().equals(confirmPass.getText().trim())){
            content.append("-Password didn't matched.\n");
            flag = false;
        }
        if(first.getText().trim().equals("")){
            content.append("-First Name cannot be empty.\n");
            flag = false;
        }
        if(!validateEmail()){
            content.append("-Invalid Email.\n");
            flag = false;
        }
        if(date.getValue() == null){
            content.append("-Invalid DOB.\n");
            flag = false;
        }
        if(question.getSelectionModel().getSelectedItem().toLowerCase().equals("select:")){
            content.append("-Select a security question.\n");
            flag = false;
        }
        if(answer.getText().trim().equals("")){
            content.append("-Enter the answer for security question.");
            flag = false;
        }

        if(flag)
            return true;
        else {
            AlertBox.displayError("Invalid Data",content.toString());
            return false;
        }
    }

    public boolean validateEmail(){
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailField.getText().trim());
        return matcher.find();
    }

    public String buildData(){
        StringBuilder sb = new StringBuilder();
        //getting dob
        LocalDate ld = date.getValue();

        //break is the separator
        sb.append(user.getText()).append(SEPARATOR)
                .append(pass.getText()).append(SEPARATOR)
                .append(confirmPass.getText()).append(SEPARATOR)
                .append(first.getText()).append(SEPARATOR)
                .append(last.getText()).append(SEPARATOR)
                .append(emailField.getText()).append(SEPARATOR)
                .append(ld.getDayOfMonth()).append(SEPARATOR)
                .append(ld.getMonthValue()).append(SEPARATOR)
                .append(ld.getYear()).append(SEPARATOR)
                .append(question.getSelectionModel().getSelectedItem()).append(SEPARATOR)
                .append(answer.getText()).append(SEPARATOR)
                .append("0").append(SEPARATOR);

        return  sb.toString();
    }

    public Scene getRegisterScene() {
        return registerScene;
    }

    public String getData() {
        return data;
    }
}
