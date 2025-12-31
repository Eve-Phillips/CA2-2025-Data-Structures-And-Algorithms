module com.example.ca22025dataalgorithmsandstructures {
    requires javafx.controls;
    requires javafx.fxml;


    opens elections to javafx.fxml;
    exports elections;
}