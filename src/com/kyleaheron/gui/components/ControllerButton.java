package com.kyleaheron.gui.components;

import com.kyleaheron.gui.GuiController;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ControllerButton extends Button{

    private static final CornerRadii cornerRadii = new CornerRadii(7);
    private static final Background hoveredBackground = new Background(new BackgroundFill(Color.WHITE, cornerRadii, Insets.EMPTY));
    private static final Background selectedBackground = new Background(new BackgroundFill(Color.WHITE, cornerRadii, Insets.EMPTY));
    private static final Background defaultBackground = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Paint hoveredTextFill = Color.DARKGRAY;
    private static final Paint defaultTextFill = Color.WHITE;
    private static final DropShadow shadow = new DropShadow();

    private Object target;

    private boolean selected;

    public ControllerButton(Object target, String text, EventHandler handler) {
        this.setTarget(target);
        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            if (!isSelected()) {
                setTextFill(hoveredTextFill);
                setBackground(hoveredBackground);
            }
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            if (!isSelected()) {
                setTextFill(defaultTextFill);
                setBackground(defaultBackground);
            }
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, handler);
        setEffect(shadow);
        setFont(GuiController.font);
        setBackground(defaultBackground);
        setTextFill(defaultTextFill);
        setText(text);
        setVisible(true);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setTextFill(hoveredTextFill);
            setBackground(selectedBackground);
        } else {
            setEffect(null);
            setTextFill(defaultTextFill);
            setBackground(defaultBackground);
        }
        this.selected = selected;
    }
}
