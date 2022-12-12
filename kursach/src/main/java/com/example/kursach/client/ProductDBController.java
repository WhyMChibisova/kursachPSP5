package com.example.kursach.client;

import com.example.kursach.models.Operations;
import com.example.kursach.models.Product;
import com.example.kursach.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class ProductDBController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addProductButton;

    @FXML
    private TableColumn<Product, Integer> categoryProduct;

    @FXML
    private Button deleteProdutButton;

    @FXML
    private Button editProductButton;

    @FXML
    private TableColumn<Product, Integer> idProduct;

    @FXML
    private TableColumn<Product, String> nameProduct;

    @FXML
    private TableColumn<Product, Double> priceProduct;

    @FXML
    private TableColumn<Product, Double> ratingProduct;

    @FXML
    private TableView<Product> tableProducts;

    private ObservableList<Product> productsData = FXCollections.observableArrayList();


    @FXML
    void initialize() {
        initProductsData();
        idProduct.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        nameProduct.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        priceProduct.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        categoryProduct.setCellValueFactory(new PropertyValueFactory<Product, Integer>("category_id"));
        ratingProduct.setCellValueFactory(new PropertyValueFactory<Product, Double>("rating"));
        tableProducts.setItems(productsData);

        tableProducts.setEditable(true);
        nameProduct.setCellFactory(forTableColumn());
        priceProduct.setCellFactory(forTableColumn(new DoubleStringConverter()));
        categoryProduct.setCellFactory(forTableColumn(new IntegerStringConverter()));
        ratingProduct.setCellFactory(forTableColumn(new DoubleStringConverter()));

        addProductButton.setOnAction(actionEvent -> {
            addProductButton.getScene().getWindow().hide();
            OnAddProductButtonClicked(actionEvent);
        });

    }

    private void initProductsData() {
        try {
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                Product product = (Product) client.readObject();
                productsData.add(product);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void OnAddProductButtonClicked(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/addNewProduct.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add new product");
        stage.show();
    }

    public void OnEditProductButtonClicked(MouseEvent event) {
        client.sendObject(Operations.EDIT_PRODUCT);
        Product product = tableProducts.getSelectionModel().getSelectedItem();
        client.sendObject(product);

        productsData.set(tableProducts.getSelectionModel().getFocusedIndex(), product);
        tableProducts.refresh();
    }

    public void OnDeleteProductButtonClicked(MouseEvent event) {
        client.sendObject(Operations.DELETE_PRODUCT);
        Product product = tableProducts.getSelectionModel().getSelectedItem();
        client.sendObject(product);
        productsData.remove(product);
    }

    public void editNameProduct(TableColumn.CellEditEvent<Product, String> productStringCellEditEvent) {
        Product product = tableProducts.getSelectionModel().getSelectedItem();
        product.setName(productStringCellEditEvent.getNewValue());
    }

    public void editPriceProduct(TableColumn.CellEditEvent<Product, Double> productDoubleCellEditEvent) {
        Product product = tableProducts.getSelectionModel().getSelectedItem();
        product.setPrice(productDoubleCellEditEvent.getNewValue());
    }

    public void editCategoryProduct(TableColumn.CellEditEvent<Product, Integer> productIntegerCellEditEvent) {
        Product product = tableProducts.getSelectionModel().getSelectedItem();
        if (productIntegerCellEditEvent.getNewValue() > 17) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Невозможно обновить продукт");
            alert.setContentText("Категория должна быть в диапозоне от 1 до 17");
            alert.showAndWait();
            tableProducts.refresh();
        } else
            product.setCategory_id(productIntegerCellEditEvent.getNewValue());
    }

    public void editRatingProduct(TableColumn.CellEditEvent<Product, Double> productDoubleCellEditEvent) {
        Product product = tableProducts.getSelectionModel().getSelectedItem();
        product.setRating(productDoubleCellEditEvent.getNewValue());
    }

}
