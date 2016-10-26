package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import javafx.scene.layout.VBox;
import main.java.com.kyleaheron.lights.EffectEnum;
import main.java.com.kyleaheron.lights.IEffect;

import java.util.concurrent.ConcurrentHashMap;

public class Clouds implements IEffect{

    private HueLight light;
    private EffectEnum effect;

    private VBox controlPane = new VBox();
    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();


    public Clouds () {
        // Initialize properties
    }

    @Override
    public void show() {
        // Clouds algorithm
    }

    @Override
    public void setLight(HueLight light) {
        this.light = light;
    }

    @Override
    public HueLight getLight() {
        return light;
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
