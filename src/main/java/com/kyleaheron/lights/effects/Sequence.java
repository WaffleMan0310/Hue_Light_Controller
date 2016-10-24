package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import main.java.com.kyleaheron.lights.IEffect;
import main.java.com.kyleaheron.lights.EffectEnum;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.ConcurrentHashMap;

public class Sequence implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    public static PropertyKey<Color[]> sequenceKey;
    public static PropertyKey<Integer> speedKey;
    public static PropertyKey<Integer> intervalKey;

    public Sequence() {
        sequenceKey = createProperty("Sequence", Color[].class, new Color[]{Color.RED});
        speedKey = createProperty("Speed", Integer.class, 2000);
        intervalKey = createProperty("Interval", Integer.class, 2000);
    }

    @Override
    public void show() {
        try {
            for (Color color : getProperty(sequenceKey)) {
                getLight()
                        .setOn(true)
                        .setBrightness((int)(color.getBrightness() * 254))
                        .setColor(color)
                        .setTransitionTime(getProperty(speedKey))
                        .show();
                Thread.sleep(getProperty(intervalKey));
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
