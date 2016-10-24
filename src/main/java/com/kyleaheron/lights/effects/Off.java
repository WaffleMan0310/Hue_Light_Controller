package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import main.java.com.kyleaheron.lights.IEffect;
import main.java.com.kyleaheron.lights.EffectEnum;
import javafx.scene.layout.VBox;

import java.util.concurrent.ConcurrentHashMap;

public class Off implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    @Override
    public void setLight(HueLight light) {
        this.light = light;
    }

    @Override
    public HueLight getLight() {
        return light;
    }

    @Override
    public void show() {
        getLight()
                .setOn(false)
                .show();
    }

    @Override
    public ConcurrentHashMap<PropertyKey<?>, Object> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public VBox getControlPane() {
        return controlPane;
    }

    @Override
    public void setEffect(EffectEnum effect) {
        this.effect = effect;
    }

    @Override
    public EffectEnum getEffect() {
        return effect;
    }
}
