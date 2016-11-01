package main.java.com.kyleaheron.lights.effects;

import com.kyleaheron.HueLight;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.java.com.kyleaheron.lights.EffectEnum;
import main.java.com.kyleaheron.lights.IEffect;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.Frequency;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Gain;

import java.util.concurrent.ConcurrentHashMap;

public class AltVisualizer implements IEffect{

    private EffectEnum effect = EffectEnum.AlT_VISUALIZER;
    private HueLight light;

    private static final int NUM_OF_FEATURES = 256;
    private static final float FEATURE_FREQ_INT = (20000 - 20) / NUM_OF_FEATURES;
    private static final int COLOR_FREQ_MAX = 380;
    private static final int COLOR_FREQ_MIN = 780;

    private ConcurrentHashMap<PropertyKey<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private VBox controlPane = new VBox();

    private static AudioContext audioContext = new AudioContext();
    private static UGen stereoInput = audioContext.getAudioInput();
    private static Gain masterGain = new Gain(audioContext, 2, 1.0f);
    private static ShortFrameSegmenter segmenter = new ShortFrameSegmenter(audioContext);
    private static FFT fft = new FFT();
    private static PowerSpectrum powerSpectrum = new PowerSpectrum();

    public AltVisualizer() {
        masterGain.addInput(stereoInput);
        segmenter.addInput(stereoInput);
        segmenter.addListener(fft);
        fft.addListener(powerSpectrum);
        audioContext.out.addDependent(segmenter);
    }


    private int numOfSamples = 1;
    private float minFreqAvg = 0, maxFreqAvg = 0, minFreqSum = 0, maxFreqSum = 0;

    @Override
    public void show() {
        if (!audioContext.isRunning()) audioContext.start();
        float[] features = powerSpectrum.getFeatures();
        if (features != null) {
            float min = 10;
            float minFreq = 10;
            float max = 0;
            float maxFreq = 0;
            for (int f = 0; f < features.length; f++) {
                if (features[f] > max) {
                    max = features[f];
                    maxFreq = f * FEATURE_FREQ_INT;
                }
                if (features[f] < min && features[f] > 1) {
                    min = features[f];
                    minFreq = f * FEATURE_FREQ_INT;
                }
            }

            maxFreqSum += maxFreq;
            minFreqSum += minFreq;
            maxFreqAvg = maxFreqSum / numOfSamples;
            minFreqAvg = minFreqSum / numOfSamples;
            numOfSamples++;

            System.out.printf("Max: %f, Min: %f, Avg Max: %f, Avg Min: %f, Color: %f\n", maxFreq, minFreq, maxFreqAvg, minFreqAvg, audioToColor(maxFreq));

            int[] rgbValues = waveLengthToRGB(audioToColor(maxFreq));
            if (rgbValues[0] != 0 || rgbValues[1] != 0 || rgbValues[2] != 0) {
                getLight()
                        .setOn(true)
                        .setBrightness(((int)(254 * (max / 10000))) < 255 ? ((int)(254 * (max / 10000))) : 254)
                        .setColor(Color.rgb(rgbValues[0], rgbValues[1], rgbValues[2]))
                        .setTransitionTime(200)
                        .show();
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

    private double audioToColor(double freq) {
        return ((((COLOR_FREQ_MAX - COLOR_FREQ_MIN) * (freq - minFreqAvg)) / (maxFreqAvg - minFreqAvg))) + COLOR_FREQ_MIN;
    }

    static private double Gamma = 0.80;
    static private double IntensityMax = 255;

    /** Taken from Earl F. Glynn's web page:
     * <a href="http://www.efg2.com/Lab/ScienceAndEngineering/Spectra.htm">Spectra Lab Report</a>
     * */
    public static int[] waveLengthToRGB(double Wavelength){

        double factor;
        double Red,Green,Blue;

        if((Wavelength >= 380) && (Wavelength<440)){
            Red = -(Wavelength - 440) / (440 - 380);
            Green = 0.0;
            Blue = 1.0;
        }else if((Wavelength >= 440) && (Wavelength<490)){
            Red = 0.0;
            Green = (Wavelength - 440) / (490 - 440);
            Blue = 1.0;
        }else if((Wavelength >= 490) && (Wavelength<510)){
            Red = 0.0;
            Green = 1.0;
            Blue = -(Wavelength - 510) / (510 - 490);
        }else if((Wavelength >= 510) && (Wavelength<580)){
            Red = (Wavelength - 510) / (580 - 510);
            Green = 1.0;
            Blue = 0.0;
        }else if((Wavelength >= 580) && (Wavelength<645)){
            Red = 1.0;
            Green = -(Wavelength - 645) / (645 - 580);
            Blue = 0.0;
        }else if((Wavelength >= 645) && (Wavelength<781)){
            Red = 1.0;
            Green = 0.0;
            Blue = 0.0;
        }else{
            Red = 0.0;
            Green = 0.0;
            Blue = 0.0;
        }

        // Let the intensity fall off near the vision limits

        if((Wavelength >= 380) && (Wavelength<420)){
            factor = 0.3 + 0.7*(Wavelength - 380) / (420 - 380);
        }else if((Wavelength >= 420) && (Wavelength<701)){
            factor = 1.0;
        }else if((Wavelength >= 701) && (Wavelength<781)){
            factor = 0.3 + 0.7*(780 - Wavelength) / (780 - 700);
        }else{
            factor = 0.0;
        };


        int[] rgb = new int[3];

        // Don't want 0^x = 1 for x <> 0
        rgb[0] = Red==0.0 ? 0 : (int) Math.round(IntensityMax * Math.pow(Red * factor, Gamma));
        rgb[1] = Green==0.0 ? 0 : (int) Math.round(IntensityMax * Math.pow(Green * factor, Gamma));
        rgb[2] = Blue==0.0 ? 0 : (int) Math.round(IntensityMax * Math.pow(Blue * factor, Gamma));

        return rgb;
    }
}
