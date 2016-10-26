package main.java.com.kyleaheron.gui;

import com.kyleaheron.HueBridge;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import main.java.com.kyleaheron.gui.components.ControllerButton;
import main.java.com.kyleaheron.gui.components.ControllerPowerButton;
import main.java.com.kyleaheron.lights.Controller;
import main.java.com.kyleaheron.lights.EffectEnum;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable{

    public static final Font font = new Font("TypoGraphica", 20);

    final static Insets topBarPadding = new Insets(0, 0, 10, 0);
    final static Insets bottomBarPadding = new Insets(10, 0, 0, 0);

    final static Background background = new Background(new BackgroundImage(new Image("main/resources/com/kyleaheron/textures/blurredlights.jpg", 1500, 1000, true, true), null, null, null, null));
    final static Background topBarBackground = new Background(new BackgroundFill(new Color(0, 0, 0, 0.65), CornerRadii.EMPTY, topBarPadding));
    final static Background bottomBarBackground = new Background(new BackgroundFill(new Color(0, 0, 0, 0.65), CornerRadii.EMPTY, bottomBarPadding));

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
        assert topPane != null;
        topPane.setBackground(topBarBackground);
        ControllerPowerButton powerButton = new ControllerPowerButton();
        powerButton.getToggle().selectedProperty().addListener(((observable, oldValue, newValue) -> {
            getCurrentController().setOn(newValue);
        }));
        topPane.rightProperty().setValue(powerButton);

        assert bottomPane != null;
        bottomPane.setBackground(bottomBarBackground);

        assert mainPane != null;
        mainPane.setBackground(background);
        System.out.println(mainPane.getScene());

        assert getLightButtonPane() != null && getBridge() != null;
        getLightButtonPane().setAlignment(Pos.CENTER_LEFT);
        getBridge().getLights().forEach(light -> getLightButtonPane().getChildren().add(new ControllerButton(new Controller(light), light.getName(), e -> {
            ControllerButton source = (ControllerButton) e.getSource();
            Controller targetController = (Controller) source.getTarget();
            if (targetController != getCurrentController()) {
                getLightButtonPane().getChildren().forEach(button -> ((ControllerButton) button).setState(ControllerButton.State.DEFAULT));
                source.setState(ControllerButton.State.SELECTED);
                setCurrentController(targetController);
                powerButton.getToggle().setSelected(getCurrentController().isOn());
                getEffectButtonPane().getChildren().forEach(b -> {
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
        assert getEffectButtonPane() != null;
        getEffectButtonPane().setAlignment(Pos.CENTER_LEFT);
        Arrays.stream(EffectEnum.values()).forEach(effect -> getEffectButtonPane().getChildren().add(new ControllerButton(effect, effect.getEffectName(), e -> {
            ControllerButton source = (ControllerButton) e.getSource();
            EffectEnum targetEffect = (EffectEnum) source.getTarget();
            if (getCurrentController() != null) {
                if (targetEffect.getEffectClass() != getCurrentController().getCurrentEffect().getClass()) {
                    getEffectButtonPane().getChildren().forEach(button -> ((ControllerButton) button).setState(ControllerButton.State.DEFAULT));
                    source.setState(ControllerButton.State.SELECTED);
                    getCurrentController().setCurrentEffect(targetEffect);
                    populateEffectControlPanel();
                }
            }
        })));
    }

    private void populateEffectControlPanel() {
        assert getEffectControlPane() != null;
        getEffectControlPane().getChildren().clear();
        getEffectControlPane().setAlignment(Pos.CENTER_LEFT);
        getEffectControlPane().getChildren().add(getCurrentController().getCurrentEffect().getControlPane());
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

    public VBox getLightButtonPane() {
        return lightButtonPane;
    }

    public VBox getEffectButtonPane() {
        return effectButtonPane;
    }

    public VBox getEffectControlPane() {
        return effectControlPane;
    }
}
