package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.IEffect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Flame implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();

    private static final Random random = new Random();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Double> turbulenceKey;
    public static PropertyKey<Color> colorKey;

    private long startTime = System.currentTimeMillis();

    public Flame() {
        brightnessKey = createProperty("brightness", Integer.class, LightUtil.MAX_BRIGHTNESS);
        turbulenceKey = createProperty("turbulence", Double.class, 0.5d);
        colorKey = createProperty("color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        try {
            if (System.currentTimeMillis() - startTime > random.nextFloat() * 1000) {
                getLight()
                        .setOn(true)
                        .setBrightness(getProperty(brightnessKey) - (int) (random.nextFloat() * (getProperty(brightnessKey) / 2.5f)))
                        .setTransitionTime(200)
                        .setColor(getProperty(colorKey))
                        .show();
                startTime = System.currentTimeMillis();
            } else {
                getLight()
                        .setOn(true)
                        .setBrightness(getProperty(brightnessKey))
                        .setTransitionTime(200)
                        .setColor(getProperty(colorKey))
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
    public void setEffect(EffectEnum effect) {
        this.effect = effect;
    }

    @Override
    public EffectEnum getEffect() {
        return effect;
    }
}
