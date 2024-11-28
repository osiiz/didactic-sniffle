module org.example.p5_grafico {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.rmi;
    requires java.sql;

    opens org.example.p5_grafico to javafx.fxml;
    exports org.example.p5_grafico;
    exports org.example.p5_grafico.db;
    opens org.example.p5_grafico.db to javafx.fxml;
}