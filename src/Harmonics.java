import java.util.Arrays;

public class Harmonics {
    private int[] harmonic;
    private int[] cast;
    private int[] value;
    private int sumValue;
    private int sumCast;

    Harmonics(int[] harmonic, int[] cast, int[] value) {
        this.harmonic = harmonic;
        this.cast = cast;
        this.value = value;
        this.sumValue = setSumValue(harmonic, value);
        this.sumCast = setSumCast(harmonic, cast);
    }


    private static int setSumValue(int[] har, int[] val) {
        int sum = 0;
        for (int i = 0; i < har.length; i++) {
            if (har[i] != 0)
                sum += val[i];
        }
        return sum;
    }

    private static int setSumCast(int[] har, int[] cas) {
        int sum = 0;
        for (int i = 0; i < har.length; i++) {
            if (har[i] != 0)
                sum += cas[i];
        }
        return sum;
    }

    public int[] getCast() {
        return cast;
    }

    public int[] getValue() {
        return value;
    }

    public int[] getHarmonic() {
        return harmonic;
    }

    @Override
    public String toString() {
        return "Harmonic {" +
                " sumCast = " + this.sumCast +
                ", sumValue = " + this.sumValue +
                ", vector = " + Arrays.toString(this.harmonic) +
                '}';
    }

    public int getSumValue() {
        return sumValue;
    }

    public int getSumCast() {
        return sumCast;
    }
}
