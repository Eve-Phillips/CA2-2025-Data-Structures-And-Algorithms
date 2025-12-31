module elections {
    requires javafx.controls;
    requires javafx.fxml;
    requires xstream;

    // JavaFX
    opens elections to javafx.fxml;

    // XStream needs access to model + data structures
    opens elections.model to xstream;
    opens elections.structures to xstream;

    exports elections;
}
