package com.example.kursach.client;

import com.example.kursach.models.Operations;
import com.example.kursach.models.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static com.example.kursach.client.StartClient.client;

public class AddProductController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addProductButton;

    @FXML
    private TextField categoryProduct;

    @FXML
    private TextField nameProduct;

    @FXML
    private TextField priceProduct;

    @FXML
    private TextField ratingProduct;

    @FXML
    void initialize() {
        Pattern pDouble = Pattern.compile("(\\d+\\.?\\d*)?");
        Pattern pInt = Pattern.compile("(\\d+)?");
        priceProduct.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pDouble.matcher(newValue).matches()) priceProduct.setText(oldValue);
        });
        categoryProduct.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pInt.matcher(newValue).matches()) categoryProduct.setText(oldValue);
            if(Integer.parseInt(categoryProduct.getText()) > 17){
                categoryProduct.setText(oldValue);
            }
        });
        ratingProduct.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pDouble.matcher(newValue).matches()) ratingProduct.setText(oldValue);
        });
        addProductButton.setOnAction(actionEvent -> {
            addProductButtonClicked();
        });
    }

    private void addProductButtonClicked() {
        String name = nameProduct.getText().trim();
        String price = priceProduct.getText().trim();
        String category_id = categoryProduct.getText().trim();
        String rating = ratingProduct.getText().trim();

        if (!name.equals("") && !price.equals("") && !category_id.equals("") && !rating.equals("")) {
            client.sendObject(Operations.ADD_NEW_PRODUCT);
            Product product = new Product(name, Double.parseDouble(price), Integer.parseInt(category_id), Double.parseDouble(rating));
            addNewProduct(product);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Невозможно добавить продукт");
            alert.setContentText("Введены не все данные");
            alert.showAndWait();
        }
    }

    private void addNewProduct(Product product) {
        try {
            client.sendObject(product);

            Operations serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case OK: {
                    addProductButton.getScene().getWindow().hide();
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
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
