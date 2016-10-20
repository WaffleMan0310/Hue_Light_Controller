package com.kyleaheron.lights;

import com.kyleaheron.HueLight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Controller {

    static Logger logger = Logger.getLogger(Controller.class.getName());

    private HueLight light;

    private List<IEffect> effects = new ArrayList<>();

    private volatile IEffect currentEffect;
    private boolean shouldRun;

    private Thread lightControlThread = new Thread(() -> {
        while (shouldRun) {
            if (getCurrentEffect() != null) {
                getCurrentEffect().show();
            }
        }
    });

    public Controller(HueLight light) {
        this.light = light;
        this.shouldRun = true;
        Arrays.stream(EffectEnum.values()).forEach(effect -> {
            try {
                IEffect effectInstance = effect.effectClass.newInstance();
                effectInstance.setEffect(effect);
                effectInstance.setLight(light);
                effects.add(effectInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        setCurrentEffect(EffectEnum.STATIC);
        lightControlThread.start();
    }

    public void setLight(HueLight light) {
        this.light = light;
    }

    public HueLight getLight() {
        return light;
    }

    public synchronized IEffect getCurrentEffect() {
        return currentEffect;
    }

    public synchronized void setCurrentEffect(EffectEnum newEffect) {
        if (effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().isPresent()) {
            this.currentEffect = effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().get();
        }
    }
}
