package main.java.com.kyleaheron.lights;

import main.java.com.kyleaheron.lights.effects.*;

public enum EffectEnum {
    STATIC("Static", Static.class),
    RAINBOW("Rainbow", Rainbow.class),
    BREATHING("Breathing", Breathing.class),
    SEQUENCE("Sequence", Sequence.class),
    FLAME("Flame", Flame.class),
    LIGHTNING("Lightning", Lightning.class),
    VISUALIZER("Visualizer", Visualizer.class),
    AlT_VISUALIZER("Alt-Visualizer", AltVisualizer.class),
    CLOUDS("Clouds", Clouds.class);

    String effectName;
    Class<? extends IEffect> effectClass;

    EffectEnum(String effectName, Class<? extends IEffect> effectClass) {
        this.effectName = effectName;
        this.effectClass = effectClass;
    }

    public String getEffectName() {
        return effectName;
    }

    public Class<? extends IEffect> getEffectClass() {
        return effectClass;
    }
}
