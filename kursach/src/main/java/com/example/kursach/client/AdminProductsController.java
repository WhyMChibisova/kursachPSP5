package com.example.kursach.client;

import com.example.kursach.models.Operations;
import com.example.kursach.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class AdminProductsController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addUserButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private TableColumn<User, Integer> idUser;
    @FXML
    private TableColumn<User, String> loginUser;
    @FXML
    private TableView<User> tableUser;
    @FXML
    private TableColumn<User, String> nameUser;
    @FXML
    private TableColumn<User, String> passwordUser;
    @FXML
    private TableColumn<User, Integer> roleUser;
    @FXML
    private TableColumn<User, String> surnameUser;

    private ObservableList<User> usersData = FXCollections.observableArrayList();


    @FXML
    void initialize() {
        initUsersData();
        idUser.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        surnameUser.setCellValueFactory(new PropertyValueFactory<User, String>("surname"));
        nameUser.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        loginUser.setCellValueFactory(new PropertyValueFactory<User, String>("login"));
        passwordUser.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        roleUser.setCellValueFactory(new PropertyValueFactory<User, Integer>("role"));
        tableUser.setItems(usersData);

        tableUser.setEditable(true);
        surnameUser.setCellFactory(forTableColumn());
        nameUser.setCellFactory(forTableColumn());
        loginUser.setCellFactory(forTableColumn());

        addUserButton.setOnAction(actionEvent -> {
            addUserButton.getScene().getWindow().hide();
            OnAddUserButtonClicked(actionEvent);
        });

    }

    @FXML
    private void OnAddUserButtonClicked(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/addNewUser.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add new user");
        stage.show();
    }

    private void initUsersData() {
        try {
            client.sendObject(Operations.VIEW_USERS);
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                User user = (User) client.readObject();
                usersData.add(user);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void OnDeleteUserButtonClicked(MouseEvent mouseEvent) {
        client.sendObject(Operations.DELETE_USER);
        User user = tableUser.getSelectionModel().getSelectedItem();
        if (user.getRole() != 1) {
            client.sendObject(user);
            usersData.remove(user);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Невозможно удалить пользователя");
            alert.setContentText("Нельзя удалить администратора!");
            alert.showAndWait();
        }
    }

    public void OnEditUserButtonClicked(MouseEvent mouseEvent) {
        client.sendObject(Operations.EDIT_USER);
        User user = tableUser.getSelectionModel().getSelectedItem();
        client.sendObject(user);

        usersData.set(tableUser.getSelectionModel().getFocusedIndex(), user);
        tableUser.refresh();
    }


    public void editSurnameUser(TableColumn.CellEditEvent<User, String> userStringCellEditEvent) {
        User user = tableUser.getSelectionModel().getSelectedItem();
        user.setSurname(userStringCellEditEvent.getNewValue());
    }

    public void editNameUser(TableColumn.CellEditEvent<User, String> userStringCellEditEvent) {
        User user = tableUser.getSelectionModel().getSelectedItem();
        user.setName(userStringCellEditEvent.getNewValue());
    }

    public void editLoginUser(TableColumn.CellEditEvent<User, String> userStringCellEditEvent) {
        User user = tableUser.getSelectionModel().getSelectedItem();
        client.sendObject(Operations.EDIT_LOGIN_USER);
        User user1 = new User();
        user1.setLogin(userStringCellEditEvent.getNewValue());
        client.sendObject(user1);
        try {
            Operations serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case ERROR: {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Невозможно обновить пользователя");
                    alert.setContentText("Пользователь с таким логином уже есть!");
                    alert.showAndWait();
                    tableUser.refresh();
                    break;
                }
                case OK: {
                    user.setLogin(userStringCellEditEvent.getNewValue());
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
