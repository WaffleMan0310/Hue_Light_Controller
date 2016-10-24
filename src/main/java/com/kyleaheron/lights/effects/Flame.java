package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import main.java.com.kyleaheron.lights.IEffect;
import main.java.com.kyleaheron.lights.EffectEnum;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Flame implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    private static final Random random = new Random();

    public static PropertyKey<Double> turbulenceKey;
    public static PropertyKey<Color> colorKey;

    private long startTime = System.currentTimeMillis();

    public Flame() {
        turbulenceKey = createProperty("turbulence", Double.class, 0.5d);
        colorKey = createPropertyWithColorChooser("Color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        try {
            if (System.currentTimeMillis() - startTime > random.nextFloat() * 1000) {
                getLight()
                        .setOn(true)
                        .setBrightness((int)(getProperty(colorKey).getBrightness() * 254) - (int) (random.nextFloat() * (int)(getProperty(colorKey).getBrightness() * 254) / 2.5f))
                        .setHue((int)((getProperty(colorKey).getHue() * 65535) / 360))
                        .setSaturation((int)(getProperty(colorKey).getSaturation() * 254))
                        .setTransitionTime(200)
                        .show();
                startTime = System.currentTimeMillis();
            } else {
                getLight()
                        .setOn(true)
                        .setBrightness((int)(getProperty(colorKey).getBrightness() * 254))
                        .setHue((int)((getProperty(colorKey).getHue() * 65535) / 360))
                        .setSaturation((int)(getProperty(colorKey).getSaturation() * 254))
                        .setTransitionTime(200)
                        .show();
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
