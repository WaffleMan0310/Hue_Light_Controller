package com.kyleaheron.lights;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.effects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Controller {

    static Logger logger = Logger.getLogger(Controller.class.getName());

    private HueLight light;

    private List<Effect> effects = new ArrayList<>();

    private volatile Effect currentEffect;
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
                Effect effectInstance = effect.effectClass.newInstance();
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

    public synchronized Effect getCurrentEffect() {
        return currentEffect;
    }

    public synchronized void setCurrentEffect(EffectEnum newEffect) {
        if (effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().isPresent()) {
            System.out.println(effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().get().getClass());
            this.currentEffect = effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().get();
        }
    }
}
