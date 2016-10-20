package com.kyleaheron.gui.components;

import com.kyleaheron.gui.GuiController;
import com.kyleaheron.gui.IComponent;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ControllerButton extends Button{

    public enum State {
        DEFAULT, HOVERED, SELECTED
    }

    private static final CornerRadii cornerRadii = new CornerRadii(7);
    private static final Background hoveredBackground = new Background(new BackgroundFill(Color.WHITE, cornerRadii, Insets.EMPTY));
    private static final Background selectedBackground = new Background(new BackgroundFill(Color.WHITE, cornerRadii, Insets.EMPTY));
    private static final Background defaultBackground = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Paint hoveredTextFill = Color.DARKGRAY;
    private static final Paint defaultTextFill = Color.WHITE;
    private static final DropShadow shadow = new DropShadow();

    private SimpleObjectProperty<Object> target;
    private SimpleObjectProperty<State> state;

    public ControllerButton(Object target, String text, EventHandler handler) {
        this.target = new SimpleObjectProperty<>(target);
        this.state = new SimpleObjectProperty<>(State.DEFAULT);
        this.state.addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                switch (newValue) {
                    case SELECTED:
                        setTextFill(hoveredTextFill);
                        setBackground(selectedBackground);
                        break;
                    case HOVERED:
                        setTextFill(hoveredTextFill);
                        setBackground(hoveredBackground);
                        break;
                    default:
                        setTextFill(defaultTextFill);
                        setBackground(defaultBackground);
                        break;
                }
            }
        });
        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            if (getState() != State.SELECTED) {
                setState(State.HOVERED);
            }
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            if (getState() != State.SELECTED) {
                setState(State.DEFAULT);
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

    public void setTarget(Object target) {
        this.target.set(target);
    }

    public Object getTarget() {
        return this.target.get();
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public State getState() {
        return this.state.get();
    }
}
