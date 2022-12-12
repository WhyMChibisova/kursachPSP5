package com.example.kursach.client;

import com.example.kursach.animations.Shake;
import com.example.kursach.models.Operations;
import com.example.kursach.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;

public class RegistrationController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signUpButton;

    @FXML
    void initialize() {
        signUpButton.setOnAction(actionEvent -> {
            signUpButtonClicked();
        });
    }

    @FXML
    void signUpButtonClicked() {
        String surname = surnameField.getText().trim();
        String name = nameField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (!surname.equals("") && !name.equals("") && !login.equals("") && !password.equals("")) {
            client.sendObject(Operations.REGISTRATION);
            User user = new User(surname, name, login, password, 2);
            registrationUser(user);
        } else {
            Shake shakeSurname = new Shake(surnameField);
            Shake shakeName = new Shake(nameField);
            Shake shakeLogin = new Shake(loginField);
            Shake shakePassword = new Shake(passwordField);
            shakeSurname.playAnimation();
            shakeName.playAnimation();
            shakeLogin.playAnimation();
            shakePassword.playAnimation();
        }
    }

    private void registrationUser(User user) {
        try {
            client.sendObject(user);

            Operations serverMsg = null;
            serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case ERROR: {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно зарегистрировать пользователя");
                    alert.setContentText("Такой пользователь уже есть!");
                    alert.showAndWait();
                    break;
                }
                case OK: {
                    signUpButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/authorization.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Authorization");
                    stage.show();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void OnExit(ActionEvent actionEvent) {
        client.sendObject(Operations.EXIT);
        try {
            if (client.readObject().equals(Operations.OK)) {
                //client.close();
                System.exit(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
