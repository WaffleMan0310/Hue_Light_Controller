package com.kyleaheron.gui.components;

import javafx.event.EventHandler;
import javafx.scene.control.Slider;

public class ControllerSlider extends Slider{

    public ControllerSlider(String name, double min, double max, double value, EventHandler handler) {
        setMin(min);
        setMax(max);
        setValue(value);
        setOnDragDetected(handler);
        setVisible(true);

    }
}
