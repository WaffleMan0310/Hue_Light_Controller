package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;

import java.awt.*;

public class Static extends Effect{

    private volatile int brightness;
    private volatile Color color;

    @Override
    public void show() {
        getLight()
                .setOn(true)
                .setBrightness(getBrightness())
                .setColor(getColor())
                .show();
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
