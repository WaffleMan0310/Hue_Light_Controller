package main.java.com.kyleaheron.gui;

import javafx.scene.layout.Pane;

public interface IComponent<T extends Pane> {

    T getComponent();

    default void addToPane(Pane parent) {
        parent.getChildren().add(getComponent());
    }

}
