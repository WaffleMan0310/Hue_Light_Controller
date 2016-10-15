package com.kyleaheron.gui;

import com.kyleaheron.HueBridge;
import com.kyleaheron.gui.components.ControllerButton;
import com.kyleaheron.gui.components.ControllerSlider;
import com.kyleaheron.lights.LightController;
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

    private HueBridge bridge;
    private LightController currentLightController;

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
        this.bridge = bridgeList.get(0);
        getBridge().authenticate();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert mainPane != null;
        mainPane.setBackground(background);
        assert lightButtonPane != null && getBridge() != null;
        getBridge().getLights().forEach(light -> lightButtonPane.getChildren().add(new ControllerButton(new LightController(light), light.getName(), e -> {
            ControllerButton source = (ControllerButton) e.getSource();
            LightController targetController = (LightController) source.getTarget();
            if (getCurrentLightController() != targetController) {
                lightButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setSelected(false));
                source.setSelected(true);
                setCurrentLightController(targetController);
                effectButtonPane.getChildren().forEach(b -> {
                    ControllerButton button = (ControllerButton) b;
                    if (button.getTarget() == getCurrentLightController().getCurrentEffect()) {
                        button.setSelected(true);
                        createEffectControlPanel((LightController.Effect) button.getTarget());
                    } else {
                        button.setSelected(false);
                    }
                });
            } else {
                source.setSelected(false);
                setCurrentLightController(null);
            }

        })));
        assert effectButtonPane != null;
        Arrays.stream(LightController.Effect.values()).forEach(effect -> effectButtonPane.getChildren().add(new ControllerButton(effect, effect.getName(), e -> {
            assert e.getSource() instanceof ControllerButton;
            ControllerButton source = (ControllerButton) e.getSource();
            assert source.getTarget() instanceof LightController.Effect;
            LightController.Effect targetEffect = (LightController.Effect) source.getTarget();
            if (getCurrentLightController() != null) {
                if (getCurrentLightController().getCurrentEffect() != targetEffect) {
                    effectButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setSelected(false));
                    source.setSelected(true);
                    getCurrentLightController().setCurrentEffect(targetEffect);
                    createEffectControlPanel(targetEffect);
                } else {
                    source.setSelected(false);
                    getCurrentLightController().setCurrentEffect(null);
                    createEffectControlPanel(null);
                }
            }
        })));
    }

    private void createEffectControlPanel(LightController.Effect effect) {
        assert effectControlPane != null;
        effectControlPane.getChildren().clear();
        switch (effect) {
            case STATIC:
                ControllerSlider stBriSlider = new ControllerSlider("Brightness", 1d, 254d, getCurrentLightController().getStaticBrightness(), (observableVal, prevNum, num) -> {
                    if (prevNum != num) {
                        getCurrentLightController().setStaticBrightness(num.intValue());
                    }
                });
                effectControlPane.getChildren().add(stBriSlider);
                break;
        }
    }

    public HueBridge getBridge() {
        return bridge;
    }

    public LightController getCurrentLightController() {
        return currentLightController;
    }

    public void setCurrentLightController(LightController currentLightController) {
        this.currentLightController = currentLightController;
    }
}
