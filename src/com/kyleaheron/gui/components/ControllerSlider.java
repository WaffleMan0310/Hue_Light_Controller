package com.kyleaheron.gui.components;

import com.kyleaheron.gui.GuiController;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ControllerSlider extends VBox {

    private static final Insets padding = new Insets(5, 0, 5, 0);
    private static final DropShadow shadow = new DropShadow();

    private Slider slider;
    private Label name;

    public ControllerSlider(String name, double min, double max, double value, ChangeListener<Number> listener) {
        this.slider = new Slider(min, max, value);
        this.name = new Label(name);

        this.name.setFont(GuiController.font);
        this.name.setEffect(shadow);
        this.name.setTextFill(Color.WHITE);
        this.name.setLabelFor(this.slider);

        this.slider.setEffect(shadow);
        this.slider.setPadding(padding);
        this.slider.valueProperty().addListener(listener);

        setPadding(padding);
        getChildren().addAll(this.name, this.slider);
        setVisible(true);

    }
}
