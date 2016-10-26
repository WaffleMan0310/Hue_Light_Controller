package main.java.com.kyleaheron.lights;

import com.kyleaheron.HueLight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    static Logger logger = Logger.getLogger(Controller.class.getName());

    private final int updateDelayMs = 100;

    private HueLight light;

    private List<IEffect> effects = new ArrayList<>();

    private volatile IEffect currentEffect;
    private volatile boolean shouldRun = true;
    private volatile boolean on;

    private long lastTime;
    private volatile Thread controllerThread = new Thread(() -> {
        while (shouldRun) {
            if (isOn()) {
                if (getCurrentEffect() != null) {
                    getCurrentEffect().show();
                }
            } else {
                if (getLight().isOn()) {
                    System.out.println("Light turned off");
                    getLight().setOn(false).show();
                }
            }
            try {
                double changeInMs = ((double)(System.nanoTime() - lastTime)) / 1000000;
                if (changeInMs < updateDelayMs) {
                    int sleepDuration = (int)(updateDelayMs - ((double)(System.nanoTime() - lastTime) / 1000000));
                    if (sleepDuration > 0) Thread.sleep(sleepDuration);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lastTime = System.nanoTime();
        }
    });

    public Controller(HueLight light) {
        this.light = light;
        this.on = true;
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
        controllerThread.start();
    }

    public void setLight(HueLight light) {
        this.light = light;
    }

    public HueLight getLight() {
        return light;
    }

    public IEffect getCurrentEffect() {
        return currentEffect;
    }

    public void setCurrentEffect(EffectEnum newEffect) {
        if (effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().isPresent()) {
            this.currentEffect = effects.stream().filter(effect -> effect.getEffect() == newEffect).findFirst().get();
        }
    }

    public synchronized boolean isOn() {
        return on;
    }

    public synchronized void setOn(boolean on) {
        this.on = on;
    }

    public void shutDown() {
        logger.log(Level.INFO, String.format("Controller for: %s shutting down...", getLight().getName()));
        try {
            shouldRun = false;
            controllerThread.join();
            getLight().setOn(false).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //if (getLight().isOn()) getLight().setOn(false).show();
    }
}
