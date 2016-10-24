package main.java.com.kyleaheron.gui.components;

import main.java.com.kyleaheron.gui.GuiController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ControllerColorPicker extends VBox{

    private static final Background background = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(7), Insets.EMPTY));

    private static DropShadow shadow = new DropShadow();
    private static Insets padding = new Insets(5, 0, 5, 0);

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
        getColorPicker().setPadding(padding);
        getColorPicker().setBackground(background);
        getColorPicker().getStyleClass().add("button");
        getColorPicker().setPrefWidth(50);
        getColorPicker().setStyle("-fx-color-label-visible: false;");

        setAlignment(Pos.CENTER_LEFT);
        setPadding(padding);
        getChildren().addAll(getNameLabel(), getColorPicker());
        setVisible(true);
    }

    public ColorPicker getColorPicker() {
        return colorPicker.get();
    }

    public Label getNameLabel() {
        return name.get();
    }
}
