package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.Effect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class Static implements Effect {

    private volatile HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Color> colorKey;

    public Static() {
        brightnessKey = createProperty("brightness", Integer.class, LightUtil.MAX_BRIGHTNESS);
        colorKey = createProperty("color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        if (getLight() != null) {
            getLight()
                    .setOn(true)
                    .setBrightness(getProperty(brightnessKey))
                    .setColor(getProperty(colorKey)) // Color Selector
                    .show();
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
    public void setEffect(EffectEnum effect) {
        this.effect = effect;
    }

    @Override
    public EffectEnum getEffect() {
        return effect;
    }
}
