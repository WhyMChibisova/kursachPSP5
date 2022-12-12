package com.example.kursach.client;

import com.example.kursach.models.Category;
import com.example.kursach.models.Operations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;

public class CategoryDBController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Category> tableCategory;
    @FXML
    private TableColumn<Category, Integer> idCategory;
    @FXML
    private TableColumn<Category, String> nameCategory;
    private ObservableList<Category> categoriesData = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        initCategoriesData();
        idCategory.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));
        nameCategory.setCellValueFactory(new PropertyValueFactory<Category, String>("name"));
        tableCategory.setItems(categoriesData);
    }

    private void initCategoriesData() {
        try {
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                Category category = (Category) client.readObject();
                categoriesData.add(category);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
