package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;

import java.awt.*;
import java.util.Random;

public class Lightning extends Effect {

    public static final Random random = new Random();

    private volatile int brightness;
    private volatile Color color;

    private int lightningMaxGapMs = 60000;
    private int lightningMaxStrikes = 12;
    private int lightningMaxStrikeLengthMs = 300;
    private int lightningMaxStrikeGapMs = 600;

    private long startTime = System.currentTimeMillis();

    @Override
    public void show() {
        try {
            if (System.currentTimeMillis() - startTime > 20000 + (int)(random.nextFloat() * 40000)) {
                for (int strike = 0; strike < random.nextInt(lightningMaxStrikes); strike++) {
                    getLight()
                            .setOn(true)
                            .setBrightness(getBrightness())
                            .setColor(getColor())
                            .setTransitionTime(0)
                            .show();
                    Thread.sleep(random.nextInt(lightningMaxStrikeLengthMs));
                    getLight()
                            .setOn(false)
                            .setTransitionTime(0)
                            .show();
                    Thread.sleep(random.nextInt(lightningMaxGapMs));
                }
                startTime = System.currentTimeMillis();
            } else {
                getLight().setOn(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
