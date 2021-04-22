module core {
    requires javafx.controls;
    requires javafx.fxml;

    opens by.bsuir.m0rk4.it.task.third.controller to javafx.fxml;
    exports by.bsuir.m0rk4.it.task.third;
}