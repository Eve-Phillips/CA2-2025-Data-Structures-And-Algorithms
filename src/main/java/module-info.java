module elections {
    requires javafx.controls;
    requires javafx.fxml;


    opens elections to javafx.fxml;
    exports elections;
}