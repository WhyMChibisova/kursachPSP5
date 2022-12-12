module com.example.kursach {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.example.kursach.database;
    opens com.example.kursach.database to javafx.fxml;
    exports com.example.kursach.client;
    opens com.example.kursach.client to javafx.fxml;
    exports com.example.kursach.models;
    opens com.example.kursach.models to javafx.fxml;
}