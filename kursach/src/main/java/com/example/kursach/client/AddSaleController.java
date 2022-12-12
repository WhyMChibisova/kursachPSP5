package com.example.kursach.client;

import com.example.kursach.models.Operations;
import com.example.kursach.models.Sale;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static com.example.kursach.client.StartClient.client;

public class AddSaleController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addSaleButton;

    @FXML
    private TextField countSale;

    @FXML
    private DatePicker dateSale;

    @FXML
    private TextField productIDSale;

    @FXML
    void initialize() {
        Pattern pInt = Pattern.compile("(\\d+)?");
        productIDSale.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pInt.matcher(newValue).matches()) productIDSale.setText(oldValue);
        });
        countSale.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!pInt.matcher(newValue).matches()) countSale.setText(oldValue);
        });

        addSaleButton.setOnAction(actionEvent -> {
            addSaleButtonClicked();
        });
    }

    private void addSaleButtonClicked() {
        String product_id = productIDSale.getText().trim();
        String datestr = dateSale.getValue().toString();
        String count = countSale.getText().trim();

        if (!productIDSale.equals("") && !datestr.equals("") && !count.equals("")) {
            client.sendObject(Operations.ADD_NEW_SALE);
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(datestr, format);
            Sale sale = new Sale(Integer.parseInt(product_id), date, Integer.parseInt(count));
            addNewSale(sale);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Невозможно добавить продажу");
            alert.setContentText("Введены не все данные");
            alert.showAndWait();
        }
    }

    private void addNewSale(Sale sale) {
        try {
            client.sendObject(sale);

            Operations serverMsg = (Operations) client.readObject();
            switch (serverMsg) {
                case OK: {
                    addSaleButton.getScene().getWindow().hide();
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
