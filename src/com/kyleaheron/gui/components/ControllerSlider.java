package com.kyleaheron.gui.components;

import com.kyleaheron.gui.GuiController;
import com.kyleaheron.gui.IComponent;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ControllerSlider extends VBox implements IComponent {

    private static final Insets padding = new Insets(5, 0, 5, 0);
    private static final DropShadow shadow = new DropShadow();

    private SimpleObjectProperty<Slider> slider;
    private SimpleObjectProperty<Label> name;

    public ControllerSlider(String name, double min, double max, double value) {
        this.slider = new SimpleObjectProperty<>(new Slider(min, max, value));
        this.name = new SimpleObjectProperty<>(new Label(name));

        getNameLabel().setFont(GuiController.font);
        getNameLabel().setEffect(shadow);
        getNameLabel().setTextFill(Color.WHITE);
        getNameLabel().setLabelFor(getSlider());

        getSlider().setEffect(shadow);
        getSlider().setPadding(padding);

        setPadding(padding);
        getChildren().addAll(getNameLabel(), getSlider());
        setVisible(true);

    }

    public Slider getSlider() {
        return this.slider.get();
    }

    public Label getNameLabel() {
        return this.name.get();
    }

    @Override
    public Pane getComponent() {
        return this;
    }
}
