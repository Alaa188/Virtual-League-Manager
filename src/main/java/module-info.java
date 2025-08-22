module soccerApp {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.soccer to javafx.fxml;
    exports org.example.soccer;
    exports;
    opens to
}
