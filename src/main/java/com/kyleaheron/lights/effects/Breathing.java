package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import main.java.com.kyleaheron.lights.IEffect;
import main.java.com.kyleaheron.lights.EffectEnum;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.ConcurrentHashMap;

public class Breathing implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    public static PropertyKey<Integer> speedKey;
    public static PropertyKey<Color> colorKey;

    private int state = 0;

    public Breathing() {
        createProperty("speed", Integer.class, 2000);
        createPropertyWithColorChooser("Color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        try {
            if (state == 0) {
                getLight()
                        .setOn(true)
                        .setBrightness((int)(getProperty(colorKey).getBrightness() * 254))
                        .setHue((int)((getProperty(colorKey).getHue() * 65535) / 360))
                        .setSaturation((int)(getProperty(colorKey).getSaturation() * 254))
                        .setTransitionTime(getProperty(speedKey))
                        .show();
                Thread.sleep(getProperty(speedKey) - 80);
                state++;
            } else {
                getLight()
                        .setOn(true)
                        .setTransitionTime(getProperty(speedKey))
                        .setBrightness(0)
                        .show();
                Thread.sleep(getProperty(speedKey) - 80);
                state = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
