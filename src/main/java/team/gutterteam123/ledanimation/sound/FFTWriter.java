package team.gutterteam123.ledanimation.sound;

import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.FixedRateMonoWriter;
import com.softsynth.math.FourierMath;

import java.util.Arrays;

public class FFTWriter extends FixedRateMonoWriter {

    private FloatSample buffer;
    private double[] real;
    private double[] imaginary;
    private double[] magnitude;

    protected FFTWriter(int bufferSize) {
        super();
        bufferSize *= 2;
        this.buffer = new FloatSample(bufferSize);
        this.real = new double[bufferSize];
        this.imaginary = new double[bufferSize];
        this.magnitude = new double[bufferSize / 2];

        dataQueue.queueLoop(this.buffer);
    }

    protected void calculateMagnitudes(float[] target) {
        int pos = (int) this.dataQueue.getFrameCount() % this.buffer.getNumFrames();
        for (int i = 0; i < this.buffer.getNumFrames(); i++) {
            this.real[i] = this.buffer.readDouble((pos + i) % this.buffer.getNumFrames());
        }
        Arrays.fill(this.imaginary, 0);
        FourierMath.fft(this.real.length, this.real, this.imaginary);
        FourierMath.calculateMagnitudes(this.real, this.imaginary, this.magnitude);
        for (int i = 0; i < target.length; i++) {
            target[i] = (float) (2 * this.magnitude[i]);
        }
    }

}
