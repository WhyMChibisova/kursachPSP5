package com.example.kursach.client;

import com.example.kursach.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static com.example.kursach.client.StartClient.client;

public class UserMenuController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button categoryDBButton;

    @FXML
    private Button productDBButton;

    @FXML
    private Button saleDBButton;

    @FXML
    private StackPane contentArea;

    @FXML
    private ComboBox<String> comboBoxFieldSort;
    @FXML
    private ComboBox<String> comboBoxTableSort;
    @FXML
    private Button exitButton;
    @FXML
    private TextField fromSort;
    @FXML
    private Button sortButton;
    @FXML
    private TextField toSort;

    @FXML
    private ComboBox<String> comboBoxFieldSearch;
    @FXML
    private ComboBox<String> comboBoxTableSearch;
    @FXML
    private Button searchButton;
    @FXML
    private TextField valueSearch;

    @FXML
    private Button pieChartButton;

    @FXML
    private Button barChartButton;

    @FXML
    private Button lineChartButton;

    @FXML
    private ComboBox<String> comboBoxTableFile;
    @FXML
    private Button fileButton;

    private ObservableList<String> tableName = FXCollections.observableArrayList("products", "category", "sales");
    private ObservableList<String> productsFieldName = FXCollections.observableArrayList("idproducts", "price", "category_id", "rating");
    private ObservableList<String> categoryFieldName = FXCollections.observableArrayList("idcategory");
    private ObservableList<String> salesFieldName = FXCollections.observableArrayList("idsales", "product_id", "date", "count");
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data> barChartData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data> lineChartData = FXCollections.observableArrayList();



    @FXML
    void initialize() {
        comboBoxTableSearch.setItems(tableName);
        comboBoxTableSort.setItems(tableName);
        comboBoxTableFile.setItems(tableName);

        comboBoxTableSort.setOnAction(actionEvent -> {
            String tableName = comboBoxTableSort.getValue();
            switch (tableName) {
                case "products": {
                    comboBoxFieldSort.setItems(productsFieldName);
                    break;
                }
                case "category": {
                    comboBoxFieldSort.setItems(categoryFieldName);
                    break;
                }
                case "sales": {
                    comboBoxFieldSort.setItems(salesFieldName);
                    break;
                }
            }
        });

        comboBoxTableSearch.setOnAction(actionEvent -> {
            String tableName = comboBoxTableSearch.getValue();
            switch (tableName) {
                case "products": {
                    comboBoxFieldSearch.setItems(productsFieldName);
                    break;
                }
                case "category": {
                    comboBoxFieldSearch.setItems(categoryFieldName);
                    break;
                }
                case "sales": {
                    comboBoxFieldSearch.setItems(salesFieldName);
                    break;
                }
            }
        });
        comboBoxFieldSort.setOnAction(actionEvent -> {
            String fieldName = comboBoxFieldSort.getValue();
        });
    }

    @FXML
    void OnSearchButtonClicked(ActionEvent event) {
        String tableName = comboBoxTableSearch.getValue();
        String fieldName = comboBoxFieldSearch.getValue();
        String value = valueSearch.getText().trim();

        switch (tableName) {
            case "products": {
                try {
                    client.sendObject(Operations.VIEW_PRODUCTS);
                    client.sendObject(Operations.SEARCH);
                    ComboBoxList comboBoxList = new ComboBoxList(tableName, fieldName, value);
                    client.sendObject(comboBoxList);
                    Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/productDB.fxml"));
                    contentArea.getChildren().removeAll();
                    contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case "category": {
                try {
                    client.sendObject(Operations.VIEW_CATEGORIES);
                    client.sendObject(Operations.SEARCH);
                    ComboBoxList comboBoxList = new ComboBoxList(tableName, fieldName, value);
                    client.sendObject(comboBoxList);
                    Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/categoryDB.fxml"));
                    contentArea.getChildren().removeAll();
                    contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case "sales": {
                try {
                    client.sendObject(Operations.VIEW_SALES);
                    client.sendObject(Operations.SEARCH);
                    ComboBoxList comboBoxList = new ComboBoxList(tableName, fieldName, value);
                    client.sendObject(comboBoxList);
                    Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/saleDB.fxml"));
                    contentArea.getChildren().removeAll();
                    contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    @FXML
    void OnSortButtonClicked(ActionEvent event) {
        String tableName = comboBoxTableSort.getValue();
        String fieldName = comboBoxFieldSort.getValue();
        String from = fromSort.getText().trim();
        String to = toSort.getText().trim();

        switch (tableName) {
            case "products": {
                try {
                    client.sendObject(Operations.VIEW_PRODUCTS);
                    client.sendObject(Operations.SORT);
                    ComboBoxList comboBoxList = new ComboBoxList(tableName, fieldName, from, to);
                    client.sendObject(comboBoxList);
                    Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/productDB.fxml"));
                    contentArea.getChildren().removeAll();
                    contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case "category": {
                try {
                    client.sendObject(Operations.VIEW_CATEGORIES);
                    client.sendObject(Operations.SORT);
                    ComboBoxList comboBoxList = new ComboBoxList(tableName, fieldName, from, to);
                    client.sendObject(comboBoxList);
                    Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/categoryDB.fxml"));
                    contentArea.getChildren().removeAll();
                    contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case "sales": {
                try {
                    client.sendObject(Operations.VIEW_SALES);
                    client.sendObject(Operations.SORT);
                    ComboBoxList comboBoxList = new ComboBoxList(tableName, fieldName, from, to);
                    client.sendObject(comboBoxList);
                    Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/saleDB.fxml"));
                    contentArea.getChildren().removeAll();
                    contentArea.getChildren().setAll(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }

    }

    @FXML
    void OnCategoryDBButtonClicked(ActionEvent event) {
        try {
            client.sendObject(Operations.VIEW_CATEGORIES);
            client.sendObject(Operations.OK);
            Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/categoryDB.fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnProductDBButtonClicked(ActionEvent event) {
        try {
            client.sendObject(Operations.VIEW_PRODUCTS);
            client.sendObject(Operations.OK);
            Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/productDB.fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnSaleDBButtonClicked(ActionEvent event) {
        try {
            client.sendObject(Operations.VIEW_SALES);
            client.sendObject(Operations.OK);
            Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/kursach/saleDB.fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnPieChartButtonClicked(ActionEvent event) {
        initPieChartData();
        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Категория - количество продаж");
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(chart);

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Значение");
                        alert.setHeaderText(null);
                        alert.setContentText("Значение = " + data.getPieValue());
                        alert.showAndWait();
                    });
        }
    }

    private void initPieChartData() {
        if (!pieChartData.isEmpty()) {
            pieChartData.clear();
        }
        try {
            client.sendObject(Operations.PIE_CHART_DATA);
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                Sale sale = (Sale) client.readObject();
                Category category = (Category) client.readObject();
                pieChartData.add(new PieChart.Data(category.getName(), sale.getCount()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnBarChartButtonClicked(ActionEvent event) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc =
                new BarChart<String, Number>(xAxis, yAxis);
        bc.setTitle("Дата - количество продаж");
        bc.getData().clear();
        xAxis.setLabel("Дата");
        yAxis.setLabel("Количество продаж");

        initBarChartData();
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().clear();
        series1.setName("Количество продаж");
        series1.setData(barChartData);
        bc.getData().addAll(series1);

        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(bc);
    }

    private void initBarChartData() {
        barChartData.clear();
        try {
            client.sendObject(Operations.BAR_CHART_DATA);
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                Sale sale = (Sale) client.readObject();

                barChartData.add(new XYChart.Data(sale.getDate().toString(), sale.getCount()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnLineChartButtonClicked(ActionEvent event) {
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        xAxis.setLabel("Рейтинг");
        yAxis.setLabel("Продукт");

        final LineChart<Number,String> lineChart =
                new LineChart<Number,String>(xAxis,yAxis);

        lineChart.setTitle("Рейтинг - продукт");
        initLineChartData();

        XYChart.Series series = new XYChart.Series();

        series.setData(lineChartData);

        lineChart.getData().add(series);
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(lineChart);
    }

    private void initLineChartData() {
        lineChartData.clear();
        try {
            client.sendObject(Operations.LINE_CHART_DATA);
            while (true) {
                Operations serverMsg = (Operations) client.readObject();
                if (serverMsg.equals(Operations.EXIT)) {
                    break;
                }
                Product product = (Product) client.readObject();
                lineChartData.add(new XYChart.Data(product.getRating(), product.getName()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void OnFileButtonClicked(ActionEvent event) {
        String tableName = comboBoxTableFile.getValue();
        client.sendObject(Operations.SAVE_FILE);
        ComboBoxList comboBoxList = new ComboBoxList();
        comboBoxList.setTableName(tableName);
        client.sendObject(comboBoxList);
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
