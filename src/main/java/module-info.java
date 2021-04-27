module core {
    requires javafx.controls;
    requires javafx.fxml;

    opens by.bsuir.m0rk4.it.task.third.controller to javafx.fxml;
    opens by.bsuir.m0rk4.it.task.third.entity to javafx.base;
    exports by.bsuir.m0rk4.it.task.third;
}
