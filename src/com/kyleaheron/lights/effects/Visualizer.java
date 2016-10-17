package com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Gain;

import java.awt.*;

public class Visualizer extends Effect{

    private volatile int brightness = 150;
    private volatile int backgroundBrightness = 100;
    private volatile double sensitivity = 0.4d;
    private volatile float gain = 0.25f;
    private volatile boolean usingRainbow = false;
    private volatile Color color = Color.CYAN;
    private volatile Color backgroundColor = Color.BLUE;

    private static volatile AudioContext audioContext = new AudioContext();
    private static volatile UGen stereoMixInput = audioContext.getAudioInput();
    private static volatile Gain masterGain = new Gain(audioContext, 2, 0.25f);
    private static volatile ShortFrameSegmenter segmenter = new ShortFrameSegmenter(audioContext);
    private static volatile FFT fft = new FFT();
    private static volatile PowerSpectrum powerSpectrum = new PowerSpectrum();

    private int hue;

    Visualizer() {
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
        if (getGain() != masterGain.getGain()) {
            masterGain.setGain(getGain());
        }
        float[] features = powerSpectrum.getFeatures();
        if (features != null) {
            if (features[0] > 500 * (1 / getSensitivity()) || features[1] > 350 * (1 / getSensitivity()) || features[2] > 200 * (1 / getSensitivity())) {
                if (isUsingRainbow()) {
                    getLight()
                            .setOn(true)
                            .setBrightness(getBrightness())
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
                            .setBrightness(getBrightness())
                            .setColor(getColor())
                            .setTransitionTime(0)
                            .show();
                }
            } else {
                if (isUsingRainbow()) {
                    getLight()
                            .setOn(false)
                            .setTransitionTime(100)
                            .show();
                } else { // Set Mode
                    getLight()
                            .setOn(true)
                            .setBrightness(getBackgroundBrightness())
                            .setColor(getBackgroundColor())
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

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getBackgroundBrightness() {
        return backgroundBrightness;
    }

    public void setBackgroundBrightness(int backgroundBrightness) {
        this.backgroundBrightness = backgroundBrightness;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public boolean isUsingRainbow() {
        return usingRainbow;
    }

    public void setUsingRainbow(boolean usingRainbow) {
        this.usingRainbow = usingRainbow;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
