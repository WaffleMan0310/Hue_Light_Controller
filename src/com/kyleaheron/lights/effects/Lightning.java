package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.Effect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Lightning implements Effect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();

    public static final Random random = new Random();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Color> colorKey;

    private int lightningMaxStrikes = 12;
    private int lightningMaxStrikeLengthMs = 300;
    private int lightningMaxStrikeGapMs = 600;

    private long startTime = System.currentTimeMillis();

    public Lightning() {
        brightnessKey = createProperty("brightness", Integer.class, LightUtil.MAX_BRIGHTNESS);
        colorKey = createProperty("color", Color.class, Color.WHITE);
    }

    @Override
    public void show() {
        try {
            if (System.currentTimeMillis() - startTime > 20000 + (int)(random.nextFloat() * 40000)) {
                for (int strike = 0; strike < random.nextInt(lightningMaxStrikes); strike++) {
                    getLight()
                            .setOn(true)
                            .setBrightness(getProperty(brightnessKey))
                            .setColor(getProperty(colorKey))
                            .setTransitionTime(0)
                            .show();
                    Thread.sleep(random.nextInt(lightningMaxStrikeLengthMs));
                    getLight()
                            .setOn(false)
                            .setTransitionTime(0)
                            .show();
                    Thread.sleep(random.nextInt(lightningMaxStrikeGapMs));
                }
                startTime = System.currentTimeMillis();
            } else {
                getLight().setOn(false);
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
