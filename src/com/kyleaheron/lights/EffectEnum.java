package com.kyleaheron.lights;

import com.kyleaheron.lights.effects.*;

public enum EffectEnum {
    STATIC("Static", Static.class),
    RAINBOW("Rainbow", Rainbow.class),
    BREATHING("Breathing", Breathing.class),
    SEQUENCE("Sequence", Sequence.class),
    FLAME("Flame", Flame.class),
    LIGHTNING("Lightning", Lightning.class),
    VISUALIZER("Visualizer", Visualizer.class);

    String effectName;
    Class<? extends Effect> effectClass;

    EffectEnum(String effectName, Class<? extends Effect> effectClass) {
        this.effectName = effectName;
        this.effectClass = effectClass;
    }

    public String getEffectName() {
        return effectName;
    }

    public Class<? extends Effect> getEffectClass() {
        return effectClass;
    }
}
