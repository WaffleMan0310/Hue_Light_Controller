package com.kyleaheron.gui.components;

import com.kyleaheron.gui.GuiController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ControllerColorPicker extends VBox{

    private static DropShadow shadow = new DropShadow();
    private static Insets padding;

    private SimpleObjectProperty<ColorPicker> colorPicker;
    private SimpleObjectProperty<Label> name;

    public ControllerColorPicker(String name, Color color) {
        this.colorPicker = new SimpleObjectProperty<>(new ColorPicker(color));
        this.name = new SimpleObjectProperty<>(new Label(name));

        getNameLabel().setFont(GuiController.font);
        getNameLabel().setEffect(shadow);
        getNameLabel().setTextFill(Color.WHITE);
        getNameLabel().setLabelFor(getColorPicker());

        getColorPicker().setEffect(shadow);

        getChildren().addAll(getColorPicker(), getNameLabel());
    }

    public ColorPicker getColorPicker() {
        return colorPicker.get();
    }

    public Label getNameLabel() {
        return name.get();
    }
}
