package com.kyleaheron.lights;

import com.kyleaheron.HueLight;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Gain;

import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class LightController {

    static Logger logger = Logger.getLogger(LightController.class.getName());

    public enum Effect {
        STATIC("Static"),
        RAINBOW("Rainbow"),
        BREATHING("Breathing"),
        SEQUENCE("Sequence"),
        FLAME("Flame"),
        LIGHTNING("Lightning"),
        AUDIO_VISUALIZER("Audio Visualizer"),
        OFF("Off");

        String name;

        Effect(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private HueLight light;
    private static final Random random = new Random();

    // Variables for the 'STATIC' color effect
    private volatile int staticBrightness = 15;
    private volatile Color staticColor = Color.RED;
    // Variables for the 'RAINBOW' color effect
    private int rainbowHue = 0;
    private volatile boolean rainbowSmooth = true;
    private volatile int rainbowBrightness = 254;
    private volatile int rainbowSpeed = 500;
    private volatile int rainbowResolution = 2;
    // Variables for the 'BREATHING' color effect
    private volatile int breathingBrightness = 254;
    private volatile int breathingSpeed = 2000;
    private volatile Color breathingColor1 = Color.RED;
    private volatile Color breathingColor2 = Color.BLUE;
    // Variables for the 'SEQUENCE' color effect
    private volatile int sequenceBrightness;
    private volatile int sequenceSpeed;
    private volatile int sequenceInterval;
    private LinkedList<Color> sequenceColors = new LinkedList<>();
    // Variables for the 'FLAME' color effect
    private volatile int flameBrightness = 150;
    private volatile int flameMaxFlickerGap = 1000;
    private volatile float flameTurbulence; // Implement
    private volatile Color flameColor = new Color(254, 100, 0);
    // Variables for the 'LIGHTNING' color effect
    private volatile int lightningBrightness = 254;
    private volatile Color lightningColor = Color.WHITE;
    private volatile int lightningMaxGapMs = 60000;
    private volatile int lightningMaxStrikes = 12;
    private volatile int lightningMaxStrikeLengthMs = 300;
    private volatile int lightningMaxStrikeGapMs = 600;
    // Variables for the 'AUDIO_VISUALIZER' color effect
    private int visualizerHue = 0;
    private volatile int visualizerBrightness = 150;
    private volatile int visualizerBgBrightness = 100;
    private volatile double visualizerSensitivity = 0.4d;
    private volatile float visualizerGain = 0.25f;
    private volatile Color visualizerColor1 = Color.CYAN;
    private volatile Color visualizerColor2 = Color.BLUE;
    private volatile boolean visualizerUsingRainbow = false;
    // Variables creating the object needed from the Beans library to process sound.
    private static volatile AudioContext audioContext = new AudioContext();
    private static volatile UGen stereoMixInput = audioContext.getAudioInput();
    private static volatile Gain masterGain = new Gain(audioContext, 2, 0.25f);
    private static volatile ShortFrameSegmenter segmenter = new ShortFrameSegmenter(audioContext);
    private static volatile FFT fft = new FFT();
    private static volatile PowerSpectrum powerSpectrum = new PowerSpectrum();

    // Should the light control thread run?
    private volatile boolean shouldRun = true;
    // Current playing effect
    private volatile Effect currentEffect = Effect.STATIC;

    private Thread lightControlThread = new Thread(() -> {
        int breathingState = 0;
        long startTime = System.currentTimeMillis();
        while (isShouldRun()) {
            /*
            if (currentEffect != Effect.AUDIO_VISUALIZER && audioContext.isRunning()) {
                audioContext.stop();
            }
            */
            if (currentEffect == Effect.STATIC) {
                light
                        .setOn(true)
                        .setBrightness(getStaticBrightness())
                        .setColor(getStaticColor())
                        .show();
            } else if (currentEffect == Effect.RAINBOW) {
                try {
                    light
                            .setOn(true)
                            .setBrightness(getRainbowBrightness())
                            .setTransitionTime(isRainbowSmooth() ? getRainbowSpeed() : 0)
                            .setHue(this.rainbowHue)
                            .show();
                    Thread.sleep(getRainbowSpeed());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (this.rainbowHue > 65535) {
                        this.rainbowHue = 0;
                    } else {
                        this.rainbowHue += (13107 / getRainbowResolution());
                    }
                }
            } else if (currentEffect == Effect.BREATHING) {
                try {
                    if (breathingState == 0) {
                        light
                                .setOn(true)
                                .setBrightness(getBreathingBrightness())
                                .setTransitionTime(getBreathingSpeed())
                                .setColor(getBreathingColor1())
                                .show();
                        Thread.sleep(getBreathingSpeed() - 80);
                        breathingState++;
                    } else {
                        light
                                .setOn(true)
                                .setTransitionTime(getBreathingSpeed())
                                .setBrightness(0)
                                .show();
                        Thread.sleep(getBreathingSpeed() - 80);
                        breathingState = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (currentEffect == Effect.SEQUENCE) {
                try {
                    for (Color color : getSequenceColors()) {
                        light
                                .setOn(true)
                                .setBrightness(getSequenceBrightness())
                                .setColor(color)
                                .setTransitionTime(getSequenceSpeed())
                                .show();
                        Thread.sleep(getSequenceInterval());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (currentEffect == Effect.FLAME) {
                try {
                    if (System.currentTimeMillis() - startTime > random.nextFloat() * getFlameMaxFlickerGap()) {
                        light
                                .setOn(true)
                                .setBrightness(getFlameBrightness() - (int) (random.nextFloat() * (getFlameBrightness() / 2.5f)))
                                .setTransitionTime(200)
                                .setColor(getFlameColor())
                                .show();
                        startTime = System.currentTimeMillis();
                    } else {
                        light
                                .setOn(true)
                                .setBrightness(getFlameBrightness())
                                .setTransitionTime(200)
                                .setColor(getFlameColor())
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (currentEffect == Effect.LIGHTNING) {
                try {
                    if (System.currentTimeMillis() - startTime > 20000 + (int)(random.nextFloat() * 40000)) {
                        for (int strike = 0; strike < random.nextInt(getLightningMaxStrikes()); strike++) {
                            light
                                    .setOn(true)
                                    .setBrightness(getLightningBrightness())
                                    .setColor(getLightningColor())
                                    .setTransitionTime(0)
                                    .show();
                            Thread.sleep(random.nextInt(getLightningMaxStrikeLengthMs()));
                            light
                                    .setOn(false)
                                    .setTransitionTime(0)
                                    .show();
                            Thread.sleep(random.nextInt(getLightningMaxStrikeGapMs()));
                        }
                        startTime = System.currentTimeMillis();
                    } else {
                        light.setOn(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (currentEffect == Effect.AUDIO_VISUALIZER) {
                if (!audioContext.isRunning()) {
                    audioContext.start();
                }
                if (getVisualizerGain() != masterGain.getGain()) {
                    masterGain.setGain(getVisualizerGain());
                }
                float[] features = powerSpectrum.getFeatures();
                if (features != null) {
                    if (features[0] > 500 * (1 / getVisualizerSensitivity()) || features[1] > 350 * (1 / getVisualizerSensitivity()) || features[2] > 200 * (1 / getVisualizerSensitivity())) {
                        if (isVisualizerUsingRainbow()) {
                            this.light
                                    .setOn(true)
                                    .setBrightness(getVisualizerBrightness())
                                    .setHue(this.visualizerHue)
                                    .setTransitionTime(100)
                                    .show();
                            if (this.visualizerHue >= 65535) {
                                this.visualizerHue = 0;
                            } else {
                                this.visualizerHue += 13107;
                            }
                        } else {
                            this.light
                                    .setOn(true)
                                    .setBrightness(getVisualizerBrightness())
                                    .setColor(getVisualizerColor1())
                                    .setTransitionTime(0)
                                    .show();
                        }
                    } else {
                        if (isVisualizerUsingRainbow()) {
                            this.light
                                    .setOn(false)
                                    .setTransitionTime(100)
                                    .show();
                        } else { // Set Mode
                            this.light
                                    .setOn(true)
                                    .setBrightness(getVisualizerBgBrightness())
                                    .setColor(getVisualizerColor2())
                                    .setTransitionTime(0)
                                    .show();
                        }
                    }
                }
                // Why?
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (currentEffect == Effect.OFF) {
                light.setOn(false).show();
            }
        }
    });

    public LightController(HueLight light) {
        this.light = light;
        masterGain.addInput(stereoMixInput);
        segmenter.addInput(stereoMixInput);
        segmenter.addListener(fft);
        fft.addListener(powerSpectrum);
        audioContext.out.addDependent(segmenter);
        lightControlThread.start();

    }

    public Effect getCurrentEffect() {
        return currentEffect;
    }

    public void setCurrentEffect(Effect effect) {
        this.currentEffect = effect;
    }

    public boolean isShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public int getStaticBrightness() {
        return staticBrightness;
    }

    public void setStaticBrightness(int staticBrightness) {
        this.staticBrightness = staticBrightness;
    }

    public Color getStaticColor() {
        return staticColor;
    }

    public void setStaticColor(Color staticColor) {
        this.staticColor = staticColor;
    }

    public boolean isRainbowSmooth() {
        return rainbowSmooth;
    }

    public void setRainbowSmooth(boolean rainbowSmooth) {
        this.rainbowSmooth = rainbowSmooth;
    }

    public int getRainbowBrightness() {
        return rainbowBrightness;
    }

    public void setRainbowBrightness(int rainbowBrightness) {
        this.rainbowBrightness = rainbowBrightness;
    }

    public int getRainbowSpeed() {
        return rainbowSpeed;
    }

    public void setRainbowSpeed(int rainbowSpeed) {
        this.rainbowSpeed = rainbowSpeed;
    }

    public int getRainbowResolution() {
        return rainbowResolution;
    }

    public void setRainbowResolution(int rainbowResolution) {
        this.rainbowResolution = rainbowResolution;
    }

    public int getBreathingBrightness() {
        return breathingBrightness;
    }

    public void setBreathingBrightness(int breathingBrightness) {
        this.breathingBrightness = breathingBrightness;
    }

    public int getBreathingSpeed() {
        return breathingSpeed;
    }

    public void setBreathingSpeed(int breathingSpeed) {
        this.breathingSpeed = breathingSpeed;
    }

    public Color getBreathingColor1() {
        return breathingColor1;
    }

    public void setBreathingColor1(Color breathingColor1) {
        this.breathingColor1 = breathingColor1;
    }

    public Color getBreathingColor2() {
        return breathingColor2;
    }

    public void setBreathingColor2(Color breathingColor2) {
        this.breathingColor2 = breathingColor2;
    }

    public int getSequenceBrightness() {
        return sequenceBrightness;
    }

    public void setSequenceBrightness(int sequenceBrightness) {
        this.sequenceBrightness = sequenceBrightness;
    }

    public int getSequenceSpeed() {
        return sequenceSpeed;
    }

    public void setSequenceSpeed(int sequenceSpeed) {
        this.sequenceSpeed = sequenceSpeed;
    }

    public int getSequenceInterval() {
        return sequenceInterval;
    }

    public void setSequenceInterval(int sequenceInterval) {
        this.sequenceInterval = sequenceInterval;
    }

    public LinkedList<Color> getSequenceColors() {
        return sequenceColors;
    }

    public void setSequenceColors(Color... colors) {
        for (Color color : colors) {
            sequenceColors.add(color);
        }
    }

    public int getFlameBrightness() {
        return flameBrightness;
    }

    public void setFlameBrightness(int flameBrightness) {
        this.flameBrightness = flameBrightness;
    }

    public int getFlameMaxFlickerGap() {
        return flameMaxFlickerGap;
    }

    public void setFlameMaxFlickerGap(int flameMaxFlickerGap) {
        this.flameMaxFlickerGap = flameMaxFlickerGap;
    }

    public float getFlameTurbulence() {
        return flameTurbulence;
    }

    public void setFlameTurbulence(float flameTurbulence) {
        this.flameTurbulence = flameTurbulence;
    }

    public Color getFlameColor() {
        return flameColor;
    }

    public void setFlameColor(Color flameColor) {
        this.flameColor = flameColor;
    }

    public int getLightningBrightness() {
        return lightningBrightness;
    }

    public void setLightningBrightness(int lightningBrightness) {
        this.lightningBrightness = lightningBrightness;
    }

    public Color getLightningColor() {
        return lightningColor;
    }

    public void setLightningColor(Color lightningColor) {
        this.lightningColor = lightningColor;
    }

    public int getLightningMaxGapMs() {
        return lightningMaxGapMs;
    }

    public void setLightningMaxGapMs(int lightningMaxGapMs) {
        this.lightningMaxGapMs = lightningMaxGapMs;
    }

    public int getLightningMaxStrikes() {
        return lightningMaxStrikes;
    }

    public void setLightningMaxStrikes(int lightningMaxStrikes) {
        this.lightningMaxStrikes = lightningMaxStrikes;
    }

    public int getLightningMaxStrikeLengthMs() {
        return lightningMaxStrikeLengthMs;
    }

    public void setLightningMaxStrikeLengthMs(int lightningMaxStrikeLengthMs) {
        this.lightningMaxStrikeLengthMs = lightningMaxStrikeLengthMs;
    }

    public int getLightningMaxStrikeGapMs() {
        return lightningMaxStrikeGapMs;
    }

    public void setLightningMaxStrikeGapMs(int lightningMaxStrikeGapMs) {
        this.lightningMaxStrikeGapMs = lightningMaxStrikeGapMs;
    }

    public int getVisualizerBrightness() {
        return visualizerBrightness;
    }

    public void setVisualizerBrightness(int visualizerBrightness) {
        this.visualizerBrightness = visualizerBrightness;
    }

    public int getVisualizerBgBrightness() {
        return visualizerBgBrightness;
    }

    public void setVisualizerBgBrightness(int visualizerBgBrightness) {
        this.visualizerBgBrightness = visualizerBgBrightness;
    }

    public double getVisualizerSensitivity() {
        return visualizerSensitivity;
    }

    public void setVisualizerSensitivity(double visualizerSensitivity) {
        this.visualizerSensitivity = visualizerSensitivity;
    }

    public float getVisualizerGain() {
        return visualizerGain;
    }

    public void setVisualizerGain(float visualizerGain) {
        this.visualizerGain = visualizerGain;
    }

    public Color getVisualizerColor1() {
        return visualizerColor1;
    }

    public void setVisualizerColor1(Color visualizerColor1) {
        this.visualizerColor1 = visualizerColor1;
    }

    public Color getVisualizerColor2() {
        return visualizerColor2;
    }

    public void setVisualizerColor2(Color visualizerColor2) {
        this.visualizerColor2 = visualizerColor2;
    }

    public boolean isVisualizerUsingRainbow() {
        return visualizerUsingRainbow;
    }

    public void setVisualizerUsingRainbow(boolean visualizerUsingRainbow) {
        this.visualizerUsingRainbow = visualizerUsingRainbow;
    }
}
