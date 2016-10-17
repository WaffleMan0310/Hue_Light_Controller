package com.kyleaheron.lights;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.effects.Static;

import java.util.logging.Logger;

public class Controller {

    static Logger logger = Logger.getLogger(Controller.class.getName());

    private HueLight light;



    // Should the light control thread run?
    private volatile boolean shouldRun = true;

    private Thread lightControlThread = new Thread(() -> {
        while (shouldRun) {
            // Code
        }
    });

    public Controller(HueLight light) {
        this.light = light;

    }

    public HueLight getLight() {
        return light;
    }
}
