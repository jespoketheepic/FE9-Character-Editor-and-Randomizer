module FE9Randomizer{
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    opens contents.gui to javafx.fxml;
    exports contents;
    exports contents.gui;
}


