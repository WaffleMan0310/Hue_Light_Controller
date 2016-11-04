package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.java.com.kyleaheron.lights.EffectEnum;
import main.java.com.kyleaheron.lights.IEffect;
import net.beadsproject.beads.analysis.featureextractors.*;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Gain;

import java.util.concurrent.ConcurrentHashMap;

public class AltVisualizer implements IEffect{

    private EffectEnum effect = EffectEnum.AlT_VISUALIZER;
    private HueLight light;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    private static AudioContext audioContext = new AudioContext();
    private static UGen stereoInput = audioContext.getAudioInput();
    private static Gain masterGain = new Gain(audioContext, 2, 1.0f);
    private static ShortFrameSegmenter segmenter = new ShortFrameSegmenter(audioContext);
    private static FFT fft = new FFT();
    private static PowerSpectrum powerSpectrum = new PowerSpectrum();
    private static SpectralCentroid centroid = new SpectralCentroid(audioContext.getSampleRate());
    private static Frequency frequency = new Frequency(audioContext.getSampleRate());

    public AltVisualizer() {
        masterGain.addInput(stereoInput);
        segmenter.addInput(stereoInput);
        segmenter.addListener(fft);
        fft.addListener(powerSpectrum);
        powerSpectrum.addListener(centroid);
        powerSpectrum.addListener(frequency);
        audioContext.out.addDependent(segmenter);
    }

    private float fMinAvg = 0, fMaxAvg = 0, fMinSum = 0, fMaxSum = 0;
    private float pMinAvg = 0, pMaxAvg = 0, pMinSum = 0, pMaxSum = 0;
    private float cfMinAvg = 0, cfMaxAvg = 0, cfMinSum = 0, cfMaxSum = 0;
    private int numOfSamples = 1;

    @Override
    public void show() {
        if (!audioContext.isRunning()) {
            audioContext.start();
        }
        float[] powerSpectrumFeatures = powerSpectrum.getFeatures();
        if (powerSpectrumFeatures != null) {
            int idx = 1;
            float pMin = 100;
            float pMax = 0;
            float fMin = 20000;
            float fMax = 20;
            float cfMin = 20000;
            float cfMax = 20;
            for (float power : powerSpectrumFeatures) {
                //float freq = idx * (19980 / 256);
                if (power > pMax && power != pMax) {
                    pMax = power;
                    //fMax = freq;
                }
                if (power < pMin && power != pMin) {
                    pMin = power;
                    //fMin = freq;
                    idx++;
                }
            }

            if (frequency.getFeatures() != null) {
                float freq = frequency.getFeatures();
                if (freq > fMax) {
                    fMax = freq;
                }
                if (freq < fMin) {
                    fMin = freq;
                }
            }

            if (centroid.getFeatures() != null) {
                float cent = centroid.getFeatures();
                if (cent > cfMax) {
                    cfMax = cent;
                }
                if (cent < cfMin) {
                    cfMin = cent;
                }
            }

            pMinSum += pMin;
            pMaxSum += pMax;
            fMinSum += fMin;
            fMaxSum += fMax;
            cfMinSum += cfMin;
            cfMaxSum += cfMax;

            pMinAvg = pMinSum / numOfSamples;
            pMaxAvg = pMaxSum / numOfSamples;
            fMinAvg = fMinSum / numOfSamples;
            fMaxAvg = fMaxSum / numOfSamples;
            cfMinAvg = cfMinSum / numOfSamples;
            cfMaxAvg = cfMaxSum / numOfSamples;

            numOfSamples++;

            double freqInterval = (2 * Math.PI) / (fMaxAvg - fMinAvg);
            Color frequencyColor = Color.WHITE;
            if (frequency.getFeatures() != null) {
                frequencyColor = Color.rgb(
                        (int) (127 * Math.sin((freqInterval * frequency.getFeatures())) + 127),
                        (int) (127 * Math.sin((freqInterval * frequency.getFeatures()) + 2 * (Math.PI / 3))) + 127,
                        (int) (127 * Math.sin((freqInterval * frequency.getFeatures()) + 4 * (Math.PI / 3))) + 127
                );
            }

            double centroidInterval = (2 * Math.PI) / (cfMaxAvg - cfMinAvg);
            Color centroidColor = Color.WHITE;
            if (centroid.getFeatures() != null) {
                centroidColor = Color.rgb(
                        (int) (127 * Math.sin((centroidInterval * centroid.getFeatures())) + 127),
                        (int) (127 * Math.sin((centroidInterval * centroid.getFeatures()) + 2 * (Math.PI / 3)) + 127),
                        (int) (127 * Math.sin((centroidInterval * centroid.getFeatures()) + 4 * (Math.PI / 3)) + 127)
                );
            }

            Color weighedCentFreqAvg = Color.rgb(
                    (int) (255 * ((centroidColor.getRed() + (frequencyColor.getRed() * 3)) / 4)),
                    (int) (255 * ((centroidColor.getGreen() + (frequencyColor.getGreen() * 3)) / 4)),
                    (int) (255 * ((centroidColor.getBlue() + (frequencyColor.getBlue() * 3)) / 4))
            );

            /*
            System.out.printf("Red: %f, Green: %f, Blue: %f\n",
                    (centroidColor.getRed() + (frequencyColor.getRed() * 2)) / 3,
                    ((centroidColor.getGreen() + (frequencyColor.getGreen() * 2)) / 3), ((centroidColor.getBlue() + (frequencyColor.getBlue() * 2)) / 3));
            */

            getLight()
                    .setBrightness((int) (254 * (pMax / 12000)) < 255 ? (int) (254 * (pMax / 12000)) : 254)
                    .setColor(weighedCentFreqAvg)
                    .setTransitionTime(200)
                    .show();
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
    public VBox getControlPane() {
        return controlPane;
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
