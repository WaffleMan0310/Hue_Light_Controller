package com.kyleaheron.gui;

import com.kyleaheron.HueBridge;
import com.kyleaheron.gui.components.ControllerButton;
import com.kyleaheron.lights.Controller;
import com.kyleaheron.lights.EffectEnum;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable{

    public static final Font font = new Font("TypoGraphica", 20);
    final static Background background = new Background(new BackgroundImage(new Image("textures/blurredlights.jpg", 1500, 1000, true, true), null, null, null, null));

    private SimpleObjectProperty<HueBridge> bridge;
    private SimpleObjectProperty<Controller> currentController;

    @FXML
    private BorderPane topPane;
    @FXML
    private BorderPane bottomPane;
    @FXML
    private Pane leftPane;
    @FXML
    private Pane rightPane;
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox lightButtonPane;
    @FXML
    private VBox effectButtonPane;
    @FXML
    private VBox effectControlPane;

    public GuiController() {
        List<HueBridge> bridgeList = HueBridge.discover();
        assert bridgeList.size() > 0;
        bridgeList.get(0).authenticate();
        // If authenticated, set the property
        this.bridge = new SimpleObjectProperty<>(bridgeList.get(0));
        this.currentController = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert mainPane != null;
        mainPane.setBackground(background);
        assert lightButtonPane != null && getBridge() != null;
        getBridge().getLights().forEach(light -> lightButtonPane.getChildren().add(new ControllerButton(new Controller(light), light.getName(), e -> {
            ControllerButton source = (ControllerButton) e.getSource();
            Controller targetController = (Controller) source.getTarget();
            if (targetController != getCurrentController()) {
                lightButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setState(ControllerButton.State.DEFAULT));
                source.setState(ControllerButton.State.SELECTED);
                setCurrentController(targetController);
                effectButtonPane.getChildren().forEach(b -> {
                    ControllerButton button = (ControllerButton) b;
                    EffectEnum buttonEffect = (EffectEnum) button.getTarget();
                    if (buttonEffect.getEffectClass() == getCurrentController().getCurrentEffect().getClass()) {
                        button.setState(ControllerButton.State.SELECTED);
                        populateEffectControlPanel();
                    } else {
                        button.setState(ControllerButton.State.DEFAULT);
                    }
                });
            } else {
                source.setState(ControllerButton.State.DEFAULT);
                setCurrentController(null);
            }

        })));
        assert effectButtonPane != null;
        Arrays.stream(EffectEnum.values()).forEach(effect -> effectButtonPane.getChildren().add(new ControllerButton(effect, effect.getEffectName(), e -> {
            ControllerButton source = (ControllerButton) e.getSource();
            EffectEnum targetEffect = (EffectEnum) source.getTarget();
            if (targetEffect.getEffectClass() != getCurrentController().getCurrentEffect().getClass()) {
                effectButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setState(ControllerButton.State.DEFAULT));
                source.setState(ControllerButton.State.SELECTED);
                getCurrentController().setCurrentEffect(targetEffect);
                populateEffectControlPanel();
            } else {
                source.setState(ControllerButton.State.DEFAULT);
                getCurrentController().setCurrentEffect(null);
                populateEffectControlPanel();
            }
        })));
    }

    private void populateEffectControlPanel() {
        assert effectControlPane != null;
        effectControlPane.getChildren().clear();
        effectControlPane.getChildren().add(getCurrentController().getCurrentEffect().getControlPane());

    }

    public HueBridge getBridge() {
        return this.bridge.get();
    }

    public Controller getCurrentController() {
        return this.currentController.get();
    }

    public void setCurrentController(Controller controller) {
        this.currentController.set(controller);
    }
}
