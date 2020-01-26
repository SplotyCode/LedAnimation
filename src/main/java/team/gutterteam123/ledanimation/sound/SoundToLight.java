package team.gutterteam123.ledanimation.sound;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceFactory;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.ChannelIn;
import com.jsyn.unitgen.LineIn;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Multiply;
import io.github.splotycode.mosaik.util.ThreadUtil;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class SoundToLight {

    private AudioDeviceManager deviceManager = AudioDeviceFactory.createAudioDeviceManager();

    private float[] spectrum;
    private FFTWriter fft;

    private int output, input;

    private LineOut lineOut;
    private LineIn lineIn;

    private void getDevices() {
        for (int i = 0; i < deviceManager.getDeviceCount(); i++) {
            System.out.println(deviceManager.getDeviceName(i));
            System.out.println(deviceManager.getMaxInputChannels(i));
            System.out.println(deviceManager.getMaxOutputChannels(i));
            /*if (deviceManager.getMaxInputChannels(i) > 0) {
                input = i;
            }
            if (deviceManager.getMaxOutputChannels(i) > 0) {
                output = i;
            }*/
            if (deviceManager.getDeviceName(i).contains("default")) {
                input = i;
                output = i;
            }
        }
    }

    public SoundToLight(int spectrumSize) {
        spectrum = new float[spectrumSize];
        fft = new FFTWriter(spectrumSize);

        Synthesizer synth = JSyn.createSynthesizer();
        getDevices();
        synth.add(lineIn = new LineIn());
        synth.add(fft);
        // Add an audio output.
        //synth.add(lineOut = new LineOut());
        // Connect the input to the output.
        lineIn.output.connect(0, fft.input, 0);
        //lineIn.output.connect(0, lineOut.input, 0);

        //lineIn.output.connect(1, lineOut.input, 1);

        synth.start(44100, input, 2, output, 1);
        //lineOut.start();
        fft.start();

        while (true) {
            update();
            System.out.println(Arrays.toString(spectrum));
            ThreadUtil.sleep(200);
        }

        //synth.stop();
    }

    public void update() {
        fft.calculateMagnitudes(spectrum);
    }


}
