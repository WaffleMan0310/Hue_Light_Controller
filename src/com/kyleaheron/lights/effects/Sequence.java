package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.IEffect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Sequence implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private HBox controlPane = new HBox();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Color[]> sequenceKey;
    public static PropertyKey<Integer> speedKey;
    public static PropertyKey<Integer> intervalKey;

    public Sequence() {
        brightnessKey = createPropertyWithSlider("brightness", Integer.class, LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS);
        sequenceKey = createProperty("sequence", Color[].class, new Color[]{Color.RED});
        speedKey = createProperty("speed", Integer.class, 2000);
        intervalKey = createProperty("interval", Integer.class, 2000);
    }

    @Override
    public void show() {
        try {
            for (Color color : getProperty(sequenceKey)) {
                getLight()
                        .setOn(true)
                        .setBrightness(getProperty(brightnessKey))
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
    public HBox getControlPane() {
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
