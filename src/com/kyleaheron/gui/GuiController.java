package com.kyleaheron.gui;

import com.kyleaheron.HueBridge;
import com.kyleaheron.gui.components.ControllerButton;
import com.kyleaheron.lights.LightController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable{

    public static final Font font = new Font("TypoGraphica", 20);
    final static Background background = new Background(new BackgroundImage(new Image("textures/blurredlights.jpg", 1500, 1000, true, true), null, null, null, null));

    private List<HueBridge> bridgeList = new ArrayList<>();

    private HueBridge currentBridge;
    private LightController currentLightController;

    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox bridgeButtonPane;
    @FXML
    private VBox lightButtonPane;
    @FXML
    private VBox effectButtonPane;

    public GuiController() {
        bridgeList = HueBridge.discover();
        assert bridgeList.size() > 0;
        bridgeList.forEach(HueBridge::authenticate);
        setCurrentBridge(getBridgeList().stream().findFirst().get());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert mainPane != null;
        mainPane.setBackground(background);
        assert bridgeButtonPane != null;
        bridgeList.forEach(bridge -> bridgeButtonPane.getChildren().add(new ControllerButton(bridge, bridge.getName(), e -> {
            assert e.getSource() instanceof ControllerButton;
            ControllerButton source = (ControllerButton) e.getSource();
            assert source.getTarget() instanceof HueBridge;
            HueBridge targetBridge = (HueBridge) source.getTarget();
            if (getCurrentBridge() != targetBridge) {
                bridgeButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setSelected(false));
                source.setSelected(true);
                setCurrentBridge(targetBridge);
            } else {
                source.setSelected(false);
                setCurrentBridge(null);
            }
        })));
        assert lightButtonPane != null && getCurrentBridge() != null;
        getCurrentBridge().getLights().forEach(light -> lightButtonPane.getChildren().add(new ControllerButton(new LightController(light), light.getName(), e -> {
            assert e.getSource() instanceof ControllerButton;
            ControllerButton source = (ControllerButton) e.getSource();
            assert source.getTarget() instanceof LightController;
            LightController targetController = (LightController) source.getTarget();
            if (getCurrentLightController() != targetController) {
                lightButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setSelected(false));
                source.setSelected(true);
                setCurrentLightController(targetController);
                effectButtonPane.getChildren().forEach(b -> {
                    ControllerButton button = (ControllerButton) b;
                    if (button.getTarget() == getCurrentLightController().getCurrentEffect()) {
                        button.setSelected(true);
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
                } else {
                    source.setSelected(false);
                    getCurrentLightController().setCurrentEffect(null);
                }
            }
        })));
    }

    public List<HueBridge> getBridgeList() {
        return bridgeList;
    }

    public HueBridge getCurrentBridge() {
        return currentBridge;
    }

    public void setCurrentBridge(HueBridge currentBridge) {
        this.currentBridge = currentBridge;
    }

    public LightController getCurrentLightController() {
        return currentLightController;
    }

    public void setCurrentLightController(LightController currentLightController) {
        this.currentLightController = currentLightController;
    }
}
