package com.kyleaheron.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class ControllerSlider extends Slider{

    private Slider slider;
    private Label name;
    private Label value;

    public ControllerSlider(String name, double min, double max, double value, ChangeListener<Number> listener) {
        setMin(min);
        setMax(max);
        setValue(value);
        valueProperty().addListener(listener);
        setVisible(true);

    }
}
