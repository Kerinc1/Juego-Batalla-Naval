module com.batallanaval.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.batallanaval.batallanaval to javafx.fxml;
    exports com.batallanaval.batallanaval;
}