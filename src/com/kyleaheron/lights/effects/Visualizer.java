package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import com.kyleaheron.lights.Effect;
import com.kyleaheron.lights.EffectEnum;
import com.kyleaheron.util.LightUtil;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Gain;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class Visualizer implements Effect {

    private HueLight light;
    private EffectEnum effect;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();

    public static PropertyKey<Integer> brightnessKey;
    public static PropertyKey<Integer> backgroundBrightnessKey;
    public static PropertyKey<Double> sensitivityKey;
    public static PropertyKey<Double> gainKey;
    public static PropertyKey<Boolean> rainbowKey;
    public static PropertyKey<Color> colorKey;
    public static PropertyKey<Color> backgroundColorKey;

    private static AudioContext audioContext = new AudioContext();
    private static UGen stereoMixInput = audioContext.getAudioInput();
    private static Gain masterGain = new Gain(audioContext, 2, 0.25f);
    private static ShortFrameSegmenter segmenter = new ShortFrameSegmenter(audioContext);
    private static FFT fft = new FFT();
    private static PowerSpectrum powerSpectrum = new PowerSpectrum();

    private int hue;

    public Visualizer() {
        brightnessKey = createProperty("brightness", Integer.class, LightUtil.MAX_BRIGHTNESS);
        backgroundBrightnessKey = createProperty("backgroundBrightness", Integer.class, LightUtil.MAX_BRIGHTNESS);
        sensitivityKey = createProperty("sensitivity", Double.class, 0.5d);
        gainKey = createProperty("gain", Double.class, 0.25d);
        rainbowKey = createProperty("rainbow", Boolean.class, true);
        colorKey = createProperty("color", Color.class, Color.RED);
        backgroundColorKey = createProperty("backgroundColor", Color.class, Color.ORANGE);

        masterGain.addInput(stereoMixInput);
        segmenter.addInput(stereoMixInput);
        segmenter.addListener(fft);
        fft.addListener(powerSpectrum);
        audioContext.out.addDependent(segmenter);
    }

    @Override
    public void show() {
        if (!audioContext.isRunning()) {
            audioContext.start();
        }
        if (getProperty(gainKey) != masterGain.getGain()) {
            masterGain.setGain(getProperty(gainKey).floatValue());
        }
        float[] features = powerSpectrum.getFeatures();
        if (features != null) {
            if (features[0] > 500 * (1 / getProperty(sensitivityKey)) || features[1] > 350 * (1 / getProperty(sensitivityKey)) || features[2] > 200 * (1 / getProperty(sensitivityKey))) {
                if (getProperty(rainbowKey)) {
                    getLight()
                            .setOn(true)
                            .setBrightness(getProperty(brightnessKey))
                            .setHue(hue)
                            .setTransitionTime(100)
                            .show();
                    if (hue >= 65535) {
                        hue = 0;
                    } else {
                        hue += 13107;
                    }
                } else {
                    getLight()
                            .setOn(true)
                            .setBrightness(getProperty(brightnessKey))
                            .setColor(getProperty(colorKey))
                            .setTransitionTime(0)
                            .show();
                }
            } else {
                if (getProperty(rainbowKey)) {
                    getLight()
                            .setOn(false)
                            .setTransitionTime(100)
                            .show();
                } else { // Set Mode
                    getLight()
                            .setOn(true)
                            .setBrightness(getProperty(backgroundBrightnessKey))
                            .setColor(getProperty(backgroundColorKey))
                            .setTransitionTime(0)
                            .show();
                }
            }
        }
        // Why?
        try {
            Thread.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLight(HueLight light) {
        this.light = light;
    }

    @Override
    public HueLight getLight() {
        return light;
    }

    @Override
    public ConcurrentHashMap<PropertyKey<?>, Object> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public void setEffect(EffectEnum effect) {
        this.effect = effect;
    }

    @Override
    public EffectEnum getEffect() {
        return effect;
    }
}
