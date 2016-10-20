package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.IEffect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;
import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Breathing implements IEffect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private HBox controlPane = new HBox();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Integer> speedKey;
    public static PropertyKey<Color> colorKey;

    private int state = 0;

    public Breathing() {
        createPropertyWithSlider("brightness", Integer.class, LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS);
        createProperty("speed", Integer.class, 2000);
        createProperty("color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        try {
            if (state == 0) {
                getLight()
                        .setOn(true)
                        .setBrightness(getProperty(brightnessKey))
                        .setTransitionTime(getProperty(speedKey))
                        .setColor(getProperty(colorKey))
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
