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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;

public class AuthorizationController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registrationButton;

    @FXML
    private Button signInButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button passwordRecoveryButton;

    @FXML
    void initialize() {
        passwordRecoveryButton.setDisable(true);
        registrationButton.setOnAction(actionEvent -> {
            registrationButton.getScene().getWindow().hide();
            OnRegistrationButtonClicked(actionEvent);
        });
        passwordRecoveryButton.setOnAction(actionEvent -> {
            passwordRecoveryButton.getScene().getWindow().hide();
            OnPasswordRecoveryButtonClicked(actionEvent);
        });
    }

    @FXML
    void OnRegistrationButtonClicked(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/registration.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Registration");
        stage.show();
    }

    @FXML
    void OnSignInButtonClicked(MouseEvent event) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (!login.equals("") && !password.equals("")) {
            client.sendObject(Operations.AUTHORIZATION);
            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            loginUser(user);
        } else {
            Shake shakeLogin = new Shake(loginField);
            Shake shakePassword = new Shake(passwordField);
            shakeLogin.playAnimation();
            shakePassword.playAnimation();
        }
    }

    private void loginUser(User user) {
        try {
            client.sendObject(user);

            Operations serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case ERROR_USER: {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно авторизовать пользователя");
                    alert.setContentText("Такого пользователя нет!");
                    alert.showAndWait();
                    break;
                }
                case ERROR_PASS: {
                    passwordRecoveryButton.setDisable(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно авторизовать пользователя");
                    alert.setContentText("Неверный пароль!");
                    alert.showAndWait();
                    break;
                }
                case OK: {
                    user.setRole(((User)client.readObject()).getRole());
                    if (user.getRole() == 1) {
                        signInButton.getScene().getWindow().hide();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/adminProducts.fxml"));
                        try {
                            loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Admin");
                        stage.show();
                    } else {
                        signInButton.getScene().getWindow().hide();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/userMenu.fxml"));
                        try {
                            loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("User");
                        stage.show();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void OnPasswordRecoveryButtonClicked(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/passwordRecovery.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Password recovery");
        stage.show();
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
