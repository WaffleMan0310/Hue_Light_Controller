package main.java.com.kyleaheron.gui.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.java.com.kyleaheron.gui.GuiController;

public class ControllerPowerButton extends HBox{

    private static final DropShadow shadow = new DropShadow();
    private static final Insets padding = new Insets(0, 40, 0, 40);

    private static final Background selectedBackground = new Background(new BackgroundFill(Color.GREEN, new CornerRadii(7), Insets.EMPTY));
    private static final Background defaultBackground = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(7), Insets.EMPTY));

    private SimpleObjectProperty<ToggleButton> toggle;

    public ControllerPowerButton() {
        toggle = new SimpleObjectProperty<>(new ToggleButton());

        getToggle().setFont(GuiController.font);
        getToggle().setTextFill(Color.GRAY);
        getToggle().setText("-");
        getToggle().setEffect(shadow);
        getToggle().setBackground(defaultBackground);
        getToggle().selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                getToggle().setText("On");
                getToggle().setBackground(selectedBackground);
            } else {
                getToggle().setText("Off");
                getToggle().setBackground(defaultBackground);
            }
        }));

        setPadding(padding);
        setAlignment(Pos.CENTER);
        getChildren().add(getToggle());
    }

    public ToggleButton getToggle() {
        return toggle.get();
    }
}
