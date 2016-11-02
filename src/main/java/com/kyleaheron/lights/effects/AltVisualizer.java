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

    public AltVisualizer() {
        masterGain.addInput(stereoInput);
        segmenter.addInput(stereoInput);
        segmenter.addListener(fft);
        fft.addListener(powerSpectrum);
        powerSpectrum.addListener(centroid);
        audioContext.out.addDependent(segmenter);
    }

    private float fMinAvg = 0, fMaxAvg = 0, fMinSum = 0, fMaxSum = 0;
    private float pMinAvg = 0, pMaxAvg = 0, pMinSum = 0, pMaxSum = 0;
    private int numOfSamples;

    @Override
    public void show() {
        if (!audioContext.isRunning()) audioContext.start();
        float[][] features = fft.getFeatures();
        if (features != null) {
            for (float[] sample : features) {
                int bin = 0;
                float pMin = 0;
                float pMax = 0;
                float fMin = 0;
                float fMax = 0;
                if (sample != null && sample == features[0]) {
                    for (float amplitude : sample) {
                        float frequency = FFT.binFrequency(audioContext.getBufferSize(), 1024, bin);
                        if (amplitude > pMax) {
                            pMax = amplitude;
                            fMax = frequency;
                        } else if (amplitude < pMin && amplitude > 0.5) {
                            pMin = amplitude;
                            fMin = frequency;
                        }
                        bin++;
                    }

                    fMaxSum += fMax;
                    fMinSum += fMin;
                    fMaxAvg = fMaxSum / numOfSamples;
                    fMinAvg = fMinSum / numOfSamples;

                    pMaxSum += pMax;
                    pMinSum += pMin;
                    pMaxAvg = pMaxSum / numOfSamples;
                    pMinAvg = pMinSum / numOfSamples;

                    if (numOfSamples < 300) {
                        numOfSamples++;
                    } else {
                        numOfSamples = 1;
                    }

                    //System.out.println(bin);

                    //System.out.printf("Min: %f, Max: %f, Min Avg: %f, Max Avg: %f\n", pMin, pMax, pMinAvg, pMaxAvg);
                    //System.out.printf("Min: %f, Max: %f, Min Avg: %f, Max Avg: %f\n", fMin, fMax, fMinAvg, fMaxAvg);
                    float centroidFeatures = 0;
                    if (centroid.getFeatures() > 0) {
                        centroidFeatures = centroid.getFeatures();
                    }

                    double interval = (2 * Math.PI) / (fMinAvg - fMaxAvg) > 0 ? (fMinAvg - fMaxAvg) : (fMaxAvg - fMinAvg);
                    Color color = Color.rgb(
                            (int) (127 * Math.sin((interval * centroidFeatures)) + 127),
                            (int) (127 * Math.sin((interval * centroidFeatures) + 2 * (Math.PI / 3))) + 127,
                            (int) (127 * Math.sin((interval * centroidFeatures) + 4 * (Math.PI / 3))) + 127
                    );

                    //System.out.printf("Red: %f, Green: %f, Blue: %f\n", color.getRed(), color.getGreen(), color.getBlue());

                    getLight()
                            .setBrightness((int)(254 * (pMax / 50)) < 255 ? (int)(254 * (pMax / 50)) : 254)
                            .setColor(color)
                            .setTransitionTime(200)
                            .show();
                }
            }
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
