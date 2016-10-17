package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;

public abstract class Effect {

    private HueLight light;

    abstract public void show();

    final public HueLight getLight() {
        return light;
    }

    final public void setLight(HueLight light) {
        this.light = light;
    }
}
