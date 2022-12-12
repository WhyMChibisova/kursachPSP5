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

public class PasswordRecoveryController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button exitButton;

    @FXML
    private TextField loginField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    private Button restoreButton;

    @FXML
    private TextField surnameField;

    @FXML
    void initialize() {
        restoreButton.setOnAction(actionEvent -> {
            OnRestoreButtonClicked(actionEvent);
        });
    }

    @FXML
    void OnRestoreButtonClicked(ActionEvent event) {
        String surname = surnameField.getText().trim();
        String name = nameField.getText().trim();
        String login = loginField.getText().trim();
        String password1 = passwordField1.getText().trim();
        String password2 = passwordField2.getText().trim();

        int role = 2;

        if (!surname.equals("") && !name.equals("") && !login.equals("") && !password1.equals("") && !password2.equals("")) {
            client.sendObject(Operations.RESTORE_USER);
            User user1 = new User(surname, name, login, password1);
            User user2 = new User(surname, name, login, password2);
            restoreUser(user1, user2);
        } else {
            Shake shakeSurname = new Shake(surnameField);
            Shake shakeName = new Shake(nameField);
            Shake shakeLogin = new Shake(loginField);
            Shake shakePassword1 = new Shake(passwordField1);
            Shake shakePassword2 = new Shake(passwordField2);
            shakeSurname.playAnimation();
            shakeName.playAnimation();
            shakeLogin.playAnimation();
            shakePassword1.playAnimation();
            shakePassword2.playAnimation();
        }
    }

    private void restoreUser(User user1, User user2) {
        try {
            client.sendObject(user1);
            client.sendObject(user2);

            Operations serverMsg = null;
            serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case ERROR: {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно восстановить пароль");
                    alert.setContentText("Такого пользователя нет!");
                    alert.showAndWait();
                    break;
                }
                case ERROR_PASS: {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно восстановить пароль");
                    alert.setContentText("Пароли не совпадают!");
                    alert.showAndWait();
                    break;
                }
                case OK: {
                    restoreButton.getScene().getWindow().hide();
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
