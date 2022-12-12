package com.example.kursach.client;

import com.example.kursach.models.Operations;
import com.example.kursach.models.Product;
import com.example.kursach.models.Sale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;
import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class SaleDBController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addSaleButton;

    @FXML
    private TableColumn<Sale, Integer> countSale;

    @FXML
    private TableColumn<Sale, LocalDate> dateSale;

    @FXML
    private Button deleteProdutButton;

    @FXML
    private Button editSaleButton;

    @FXML
    private TableColumn<Sale, Integer> idSale;

    @FXML
    private TableColumn<Sale, Integer> product_idSale;

    @FXML
    private TableView<Sale> tableSales;

    private ObservableList<Sale> salesData = FXCollections.observableArrayList();


    @FXML
    void initialize() {
        initSalesData();
        idSale.setCellValueFactory(new PropertyValueFactory<Sale, Integer>("id"));
        product_idSale.setCellValueFactory(new PropertyValueFactory<Sale, Integer>("product_id"));
        dateSale.setCellValueFactory(new PropertyValueFactory<Sale, LocalDate>("date"));
        countSale.setCellValueFactory(new PropertyValueFactory<Sale, Integer>("count"));
        tableSales.setItems(salesData);

        tableSales.setEditable(true);
        product_idSale.setCellFactory(forTableColumn(new IntegerStringConverter()));
        countSale.setCellFactory(forTableColumn(new IntegerStringConverter()));

        addSaleButton.setOnAction(actionEvent -> {
            addSaleButton.getScene().getWindow().hide();
            OnAddSaleButtonClicked(actionEvent);
        });
    }

    private void initSalesData() {
        try {
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                Sale sale = (Sale) client.readObject();
                salesData.add(sale);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnAddSaleButtonClicked(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursach/addNewSale.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add new sale");
        stage.show();
    }

    @FXML
    void OnDeleteSaleButtonClicked(MouseEvent event) {
        client.sendObject(Operations.DELETE_SALE);
        Sale sale = tableSales.getSelectionModel().getSelectedItem();
        client.sendObject(sale);
        salesData.remove(sale);
    }

    @FXML
    void OnEditSaleButtonClicked(MouseEvent event) {
        client.sendObject(Operations.EDIT_SALE);
        Sale sale = tableSales.getSelectionModel().getSelectedItem();
        client.sendObject(sale);

        salesData.set(tableSales.getSelectionModel().getFocusedIndex(), sale);
        tableSales.refresh();
    }

    @FXML
    void editCountSale(TableColumn.CellEditEvent<Sale, Integer> saleIntegerCellEditEvent) {
        Sale sale = tableSales.getSelectionModel().getSelectedItem();
        sale.setCount(saleIntegerCellEditEvent.getNewValue());
    }

    @FXML
    void editProductIDSale(TableColumn.CellEditEvent<Sale, Integer> saleIntegerCellEditEvent) {
        Sale sale = tableSales.getSelectionModel().getSelectedItem();
        sale.setProduct_id(saleIntegerCellEditEvent.getNewValue());
    }
}
