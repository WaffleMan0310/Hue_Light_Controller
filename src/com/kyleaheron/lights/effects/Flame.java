package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;

import java.awt.*;
import java.util.Random;

public class Flame extends Effect{

    private static final Random random = new Random();

    private volatile int brightness;
    private volatile double turbulence;
    private volatile Color color;

    private long startTime = System.currentTimeMillis();

    @Override
    public void show() {
        try {
            if (System.currentTimeMillis() - startTime > random.nextFloat() * 1000) {
                getLight()
                        .setOn(true)
                        .setBrightness(getBrightness() - (int) (random.nextFloat() * (getBrightness() / 2.5f)))
                        .setTransitionTime(200)
                        .setColor(getColor())
                        .show();
                startTime = System.currentTimeMillis();
            } else {
                getLight()
                        .setOn(true)
                        .setBrightness(getBrightness())
                        .setTransitionTime(200)
                        .setColor(getColor())
                        .show();
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

    public double getTurbulence() {
        return turbulence;
    }

    public void setTurbulence(double turbulence) {
        this.turbulence = turbulence;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
