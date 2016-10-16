package com.kyleaheron.gui;

import com.kyleaheron.HueBridge;
import com.kyleaheron.gui.components.ControllerButton;
import com.kyleaheron.gui.components.ControllerSlider;
import com.kyleaheron.lights.LightController;
import com.kyleaheron.util.LightUtil;
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
    private SimpleObjectProperty<LightController> currentController;

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
        getBridge().getLights().forEach(light -> lightButtonPane.getChildren().add(new ControllerButton(new LightController(light), light.getName(), e -> {
            ControllerButton source = (ControllerButton) e.getSource();
            LightController targetController = (LightController) source.getTarget();
            if (getCurrentLightController() != targetController) {
                lightButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setState(ControllerButton.State.DEFAULT));
                source.setState(ControllerButton.State.SELECTED);
                setCurrentLightController(targetController);
                effectButtonPane.getChildren().forEach(b -> {
                    ControllerButton button = (ControllerButton) b;
                    if (button.getTarget() == getCurrentLightController().getCurrentEffect()) {
                        button.setState(ControllerButton.State.SELECTED);
                        createEffectControlPanel((LightController.Effect) button.getTarget());
                    } else {
                        button.setState(ControllerButton.State.DEFAULT);
                    }
                });
            } else {
                source.setState(ControllerButton.State.DEFAULT);
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
                    effectButtonPane.getChildren().forEach(button -> ((ControllerButton) button).setState(ControllerButton.State.DEFAULT));
                    source.setState(ControllerButton.State.SELECTED);
                    getCurrentLightController().setCurrentEffect(targetEffect);
                    createEffectControlPanel(targetEffect);
                } else {
                    source.setState(ControllerButton.State.DEFAULT);
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
                ControllerSlider stBriSlider = new ControllerSlider("Brightness", LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, getCurrentLightController().getStaticBrightness(), (observable, oldValue, newValue) -> {
                    getCurrentLightController().setStaticBrightness(newValue.intValue());
                });
                effectControlPane.getChildren().add(stBriSlider);
                break;
            case RAINBOW:
                // Smooth toggle
                ControllerSlider rbBriSlider = new ControllerSlider("Brightness", LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, getCurrentLightController().getRainbowBrightness(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setRainbowBrightness(newValue.intValue());
                }));
                ControllerSlider rbSpdSlider = new ControllerSlider("Speed", 0.0d, 1.0d, getCurrentLightController().getRainbowSpeed(), ((observable, oldValue, newValue) -> {
                    // Fix.
                    //getCurrentLightController().setRainbowSpeed(newValue.doubleValue());
                }));
                ControllerSlider rbResSlider = new ControllerSlider("Resolution", 0.0d, 1.0d, getCurrentLightController().getRainbowResolution(), ((observable, oldValue, newValue) -> {
                    // Fix.
                    // getCurrentLightController().setRainbowResolution(newValue.doubleValue());
                }));
                effectControlPane.getChildren().addAll(rbBriSlider, rbSpdSlider, rbResSlider);
                break;
            case BREATHING:
                ControllerSlider brBriSlider = new ControllerSlider("Brightness", LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, getCurrentLightController().getBreathingBrightness(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setBreathingBrightness(newValue.intValue());
                }));
                ControllerSlider brSpdSlider = new ControllerSlider("Speed", 0.0d, 1.0d, getCurrentLightController().getBreathingSpeed(), ((observable, oldValue, newValue) -> {
                    // Fix.
                    // getCurrentLightController().setBreathingSpeed(newValue.doubleValue());
                }));
                // Color selector
                effectControlPane.getChildren().addAll(brBriSlider, brSpdSlider);
                break;
            case SEQUENCE:
                ControllerSlider sqBriSlider = new ControllerSlider("Brightness", 0.0d, 1.0d, getCurrentLightController().getSequenceBrightness(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setSequenceBrightness(newValue.intValue());
                }));
                ControllerSlider sqSpdSlider = new ControllerSlider("Speed", 0.0d, 1.0d, getCurrentLightController().getSequenceSpeed(), ((observable, oldValue, newValue) -> {
                    // Fix.
                    // getCurrentLightController().setSequenceSpeed(newValue.doubleValue());
                }));
                ControllerSlider sqIntSlider = new ControllerSlider("Interval", 0.0d, 1.0d, getCurrentLightController().getSequenceInterval(), ((observable, oldValue, newValue) -> {
                    // Fix.
                    // getCurrentLightController().setSequenceInterval(newValue.doubleValue());
                }));
                // Color list selector
                effectControlPane.getChildren().addAll(sqBriSlider, sqSpdSlider, sqIntSlider);
                break;
            case FLAME:
                ControllerSlider flBriSlider = new ControllerSlider("Brightness", 0.0d, 1.0d, getCurrentLightController().getFlameBrightness(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setFlameBrightness(newValue.intValue());
                }));
                ControllerSlider flTurbSlider = new ControllerSlider("Turbulence", 0.0d, 1.0d, getCurrentLightController().getFlameTurbulence(), ((observable, oldValue, newValue) -> {
                    // Fix.
                    // getCurrentLightController().setFlameTurbulence(newValue.doubleValue());
                }));
                // Color selector
                effectControlPane.getChildren().addAll(flBriSlider, flTurbSlider);
                break;
            case LIGHTNING:
                ControllerSlider lnBriSlider = new ControllerSlider("Brightness", 0.0d, 1.0d, getCurrentLightController().getLightningBrightness(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setLightningBrightness(newValue.intValue());
                }));
                // Wind speed
                // Color selector
                effectControlPane.getChildren().addAll(lnBriSlider);
                break;
            case AUDIO_VISUALIZER:
                // Use rainbow toggle
                ControllerSlider avBriSlider = new ControllerSlider("Brightness", 0.0d, 1.0d, getCurrentLightController().getVisualizerBrightness(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setVisualizerBrightness(newValue.intValue());
                }));
                ControllerSlider avBriBgSlider = null;
                if (!getCurrentLightController().isVisualizerUsingRainbow()) {
                    avBriBgSlider = new ControllerSlider("Background Brightness", 0.0d, 1.0d, getCurrentLightController().getVisualizerBgBrightness(), ((observable, oldValue, newValue) -> {
                        getCurrentLightController().setVisualizerBgBrightness(newValue.intValue());
                    }));
                }
                ControllerSlider avGainSlider = new ControllerSlider("Gain", 0.0d, 1.0d, getCurrentLightController().getVisualizerGain(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setVisualizerGain(newValue.floatValue()); // Use double
                }));
                ControllerSlider avSensSlider = new ControllerSlider("Sensitivity", 0.0d, 1.0d, getCurrentLightController().getVisualizerSensitivity(), ((observable, oldValue, newValue) -> {
                    getCurrentLightController().setVisualizerSensitivity(newValue.doubleValue());
                }));
                // Color selector
                // Color selector
                // Visualizer
                effectControlPane.getChildren().addAll(avBriSlider, avBriBgSlider != null ? avBriBgSlider : null, avGainSlider, avSensSlider);
                break;
        }
    }

    public HueBridge getBridge() {
        return this.bridge.get();
    }

    public LightController getCurrentLightController() {
        return this.currentController.get();
    }

    public void setCurrentLightController(LightController controller) {
        this.currentController.set(controller);
    }
}
