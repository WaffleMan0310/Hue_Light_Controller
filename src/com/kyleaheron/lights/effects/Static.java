package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.IEffect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.concurrent.ConcurrentHashMap;

public class Static implements IEffect {

    private volatile HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Color> colorKey;

    public Static() {
        brightnessKey = createPropertyWithSlider("Brightness", Integer.class, LightUtil.MIN_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS, LightUtil.MAX_BRIGHTNESS);
        colorKey = createPropertyWithColorChooser("Color", Color.class, Color.RED);
    }

    @Override
    public void show() {
        getLight()
                .setOn(true)
                .setBrightness(getProperty(brightnessKey))
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
