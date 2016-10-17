package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;

import java.awt.*;

public class Sequence extends Effect {


    private volatile int brightness;
    private volatile int interval;
    private volatile int speed;
    private volatile Color[] sequence;

    @Override
    public void show() {
        try {
            for (Color color : getSequence()) {
                getLight()
                        .setOn(true)
                        .setBrightness(getBrightness())
                        .setColor(color)
                        .setTransitionTime(getSpeed())
                        .show();
                Thread.sleep(getInterval());
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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Color[] getSequence() {
        return sequence;
    }

    public void setSequence(Color[] sequence) {
        this.sequence = sequence;
    }
}
