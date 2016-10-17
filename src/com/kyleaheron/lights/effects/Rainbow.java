package com.kyleaheron.lights.effects;


import com.kyleaheron.HueLight;

public class Rainbow extends Effect{

    private volatile int brightness;
    private volatile int speed;
    private volatile int resolution;
    private volatile boolean flashing;

    private int hue = 0;

    @Override
    public void show() {
        try {
            getLight()
                    .setOn(true)
                    .setBrightness(getBrightness())
                    .setTransitionTime(isFlashing() ? 0 : getSpeed())
                    .setHue(this.hue)
                    .show();
            Thread.sleep(getSpeed());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.hue > 65535) {
                this.hue = 0;
            } else {
                this.hue += (13107 / getResolution());
            }
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

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public boolean isFlashing() {
        return flashing;
    }

    public void setFlashing(boolean flashing) {
        this.flashing = flashing;
    }
}
