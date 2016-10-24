package main.java.com.kyleaheron.gui.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import main.java.com.kyleaheron.gui.GuiController;

public class ControllerToggle extends HBox {

    private static final Background defaultBackground = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(7), Insets.EMPTY));
    private static final Background selectetdBackground = new Background(new BackgroundFill(Color.GREEN, new CornerRadii(7), Insets.EMPTY));
    private static final DropShadow shadow = new DropShadow();
    private static final Insets padding = new Insets(5, 5, 5, 5);

    private SimpleObjectProperty<Label> label;
    private SimpleObjectProperty<ToggleButton> toggle;

    public ControllerToggle(String name) {
        label = new SimpleObjectProperty<>(new Label(name));
        toggle = new SimpleObjectProperty<>(new ToggleButton("Disabled"));

        getNameLabel().setFont(GuiController.font);
        getNameLabel().setEffect(shadow);
        getNameLabel().setTextFill(Color.WHITE);
        getNameLabel().setLabelFor(getToggle());
        getNameLabel().setPadding(padding);

        getToggle().setBackground(defaultBackground);
        getToggle().setEffect(shadow);
        getToggle().setPadding(padding);
        getToggle().setFont(GuiController.font);
        getToggle().selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                getToggle().setText("Enabled"); // Resource key
                getToggle().setBackground(selectetdBackground);
            } else {
                getToggle().setText("Disabled");
                getToggle().setBackground(defaultBackground);
            }
        }));

        setAlignment(Pos.CENTER_LEFT);
        setPadding(padding);
        getChildren().addAll(getNameLabel(), getToggle());
        setVisible(true);
    }

    public Label getNameLabel() {
        return label.get();
    }

    public ToggleButton getToggle() {
        return toggle.get();
    }
}
