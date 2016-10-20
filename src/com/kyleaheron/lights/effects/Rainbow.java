package com.kyleaheron.lights.effects;


import com.kyleaheron.HueLight;
import com.kyleaheron.lights.IEffect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Rainbow implements IEffect {

    private volatile HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private HBox controlPane = new HBox();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Integer> speedKey;
    public static PropertyKey<Integer> resolutionKey;
    public static PropertyKey<Boolean> flashingKey;

    private int hue = 0;

    public Rainbow() {
        brightnessKey = createPropertyWithSlider("brightness", Integer.class, LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS);
        speedKey = createProperty("speed", Integer.class, 2000);
        resolutionKey = createProperty("resolution", Integer.class, 3);
        flashingKey = createProperty("flashing", Boolean.class, false);
    }

    @Override
    public void show() {
        try {
            getLight()
                    .setOn(true)
                    .setBrightness(getProperty(brightnessKey))
                    .setTransitionTime(!getProperty(flashingKey) ? getProperty(speedKey) : 0)
                    .setHue(this.hue)
                    .show();
            Thread.sleep(getProperty(speedKey));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.hue > 65535) {
                this.hue = 0;
            } else {
                this.hue += (13107 / getProperty(resolutionKey));
            }
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
