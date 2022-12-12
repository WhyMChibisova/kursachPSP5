package com.example.kursach.client;

import com.example.kursach.models.Operations;
import com.example.kursach.models.User;
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

public class AddUserController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addUserButton;

    @FXML
    private TextField loginUser;

    @FXML
    private TextField nameUser;

    @FXML
    private PasswordField passwordUser;

    @FXML
    private TextField surnameUser;

    @FXML
    void initialize() {
        addUserButton.setOnAction(actionEvent -> {
            addUserButtonClicked();
        });
    }

    private void addUserButtonClicked() {
        String surname = surnameUser.getText().trim();
        String name = nameUser.getText().trim();
        String login = loginUser.getText().trim();
        String password = passwordUser.getText().trim();

        if (!surname.equals("") && !name.equals("") && !login.equals("") && !password.equals("")) {
            client.sendObject(Operations.ADD_NEW_USER);
            User user = new User(surname, name, login, password);
            addNewUser(user);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Невозможно добавить пользователя");
            alert.setContentText("Введены не все данные");
            alert.showAndWait();
        }
    }

    private void addNewUser(User user) {
        try {
            client.sendObject(user);

            Operations serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case ERROR: {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно добавить пользователя");
                    alert.setContentText("Такой пользователь уже есть!");
                    alert.showAndWait();
                    break;
                }
                case OK: {
                    addUserButton.getScene().getWindow().hide();
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
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
