package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import main.java.com.kyleaheron.lights.EffectEnum;
import main.java.com.kyleaheron.lights.IEffect;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.ConcurrentHashMap;

public class Static implements IEffect {

    private volatile HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    public static PropertyKey<Color> colorKey;

    public Static() {
        colorKey = createPropertyWithColorChooser("Color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        getLight()
                .setOn(true)
                .setBrightness((int)(getProperty(colorKey).getBrightness() * 254))
                .setColor(getProperty(colorKey)) // Color Selector
                .show();
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
