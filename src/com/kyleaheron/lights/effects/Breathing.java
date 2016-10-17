package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;

import java.awt.*;

public class Breathing extends Effect {

    private volatile int brightness;
    private volatile int speed;
    private volatile Color color;

    private int state = 0;

    @Override
    public void show() {
        try {
            if (state == 0) {
                getLight()
                        .setOn(true)
                        .setBrightness(getBrightness())
                        .setTransitionTime(getSpeed())
                        .setColor(getColor())
                        .show();
                Thread.sleep(getSpeed() - 80);
                state++;
            } else {
                getLight()
                        .setOn(true)
                        .setTransitionTime(getSpeed())
                        .setBrightness(0)
                        .show();
                Thread.sleep(getSpeed() - 80);
                state = 0;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
