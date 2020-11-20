import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class HS {
    public static int hms;               //size o f memory harmony
    public static double hmsr;           //frequency of value selection from harmony memory
    public static double par;            //neighboring value selection frequency
    public static double fw;             //increment change vector
    public static int K;                 //max number of iteration
    public static int size;              //size of the bag
    public static int[] x;               //Vector of solution
    public static Harmonics newSolution;
    public static Harmonics solution;
    public static Harmonics[] solutions;
    protected static Random random = new Random();
    public static int numberOfThings;
    public static int[] cast;
    public static int[] value;
    public static int maxCost;
    public static int maxValue;


    public static void main(String[] args) throws FileNotFoundException {
        //read data for stuffing file things.txt
        File f = new File("src\\data.txt");
        Scanner scan = new Scanner(f);
        String s = scan.nextLine();
        String[] stings = s.split(" ");
        String path = "src\\things.txt";
        int n = Integer.parseInt(stings[0]);         //number of things
        numberOfThings = n;
        maxValue = Integer.parseInt(stings[1]);      //max value of one thing
        maxCost = Integer.parseInt(stings[2]);       //max cost of one thing
        things(path, n, maxValue, maxCost);          // write list of cast and value if file

        //read data for harmony search
        File f1 = new File("src\\parameters.txt");
        Scanner scan1 = new Scanner(f1);
        String s1 = scan1.nextLine();
        String[] stings1 = s1.split(" ");
        hms = Integer.parseInt(stings1[0]);         //size o f memory harmony
        hmsr = Double.parseDouble(stings1[1]);      //frequency of value selection from harmony memory
        par = Double.parseDouble(stings1[2]);       //neighboring value selection frequency
        fw = Double.parseDouble(stings1[3]);        //increment change vector
        K = Integer.parseInt(stings1[4]);           //max number of iteration
        size = Integer.parseInt(stings1[5]);        //size of the bag

        Scanner scan2 = new Scanner(new File("src\\things.txt"));
        value = parsInt(scan2.nextLine().split(" "));
        cast = parsInt(scan2.nextLine().split(" "));
        Harmonics[] harmonics = generateStartHarmonyMemory(cast, value);

        solutions = sortHarmonics(harmonics);
        solution = solutions[solutions.length - 1];

        //working HS
        for (int i = 0; i < K; i++) {
            harmonicsImprovisation();
            harmonyUpdate();
            solutions = sortHarmonics(solutions);
        }

        System.out.println();
        for (Harmonics h : solutions) {
            System.out.println("RESULT: " + h.toString());
        }
        System.out.println("\nBEST SOLVE: " + solutions[0]);
        System.out.println("\nBEST SOLVE: " + solutions[1]);
        System.out.println("\nBEST SOLVE: " + solutions[2]);
        System.out.println("\nBEST SOLVE: " + solutions[3]);
        System.out.println("\nBEST SOLVE: " + solutions[4]);

        XYChart chart = new XYChartBuilder().width(800).height(500).title(HS.class.getSimpleName()).xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

        ArrayList<Integer> step = new ArrayList<>();
        ArrayList<Integer> costs1 = new ArrayList<>();
        int count = 0;
        int index = solutions.length - 1;
        while (count < K) {
            if (count < hms){
                costs1.add(solutions[index].getSumCast());
                index--;
            }
            else
                costs1.add(solutions[0].getSumCast());
            count++;

        }
        for (int i = 1; i <= K; i++) {
            step.add(i);
        }

        chart.addSeries("a", step, costs1);
        new SwingWrapper<XYChart>(chart).displayChart();

    }

    static void harmonicsImprovisation() {
        int[] newSolutionLocal = new int[numberOfThings];
        double prob = randomProb();
        int sumValue = 0;
        if (prob > hmsr) {
            for (int i = 0; i < numberOfThings && sumValue < size - value[i]; i++) {
                sumValue += value[i];
                newSolutionLocal[i] = (int) (Math.random() * 2);
            }
        } else if (prob <= hmsr) {
            double prob1 = randomProb();
            int index = randomIndex();
            if (index == 0) {
                newSolutionLocal = solutions[index].getHarmonic();
            } else {
                newSolutionLocal = solutions[index - 1].getHarmonic();
            }
            if (prob1 <= par) {
                double pitch_adj;
                double uni_random;
                double total_value_range;

                for (int i = 0; i < numberOfThings; i++) {
                    uni_random = randomProb();
                    total_value_range = randomProb();
                    pitch_adj = uni_random * total_value_range * fw;
                    if ((newSolutionLocal[i] + pitch_adj) > 1 && newSolutionLocal[i] - pitch_adj >= 0) {
                        newSolutionLocal[i] = (int) (newSolutionLocal[i] + pitch_adj);
                    } else if ((newSolutionLocal[i] + pitch_adj) <= 1) {
                        newSolutionLocal[i] = (int) (newSolutionLocal[i] + pitch_adj);
                    }

                }
            }
        }
        newSolution = new Harmonics(newSolutionLocal, cast, value);
    }

    public static void harmonyUpdate() {
        if ((solution.getSumCast() < newSolution.getSumCast()) && newSolution.getSumValue() < size) {
            solution = newSolution;
            solutions[solutions.length - 1] = solution;
        }
    }

    private static int[] parsInt(String[] s) {
        int[] numbers = new int[s.length];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(s[i]);
        }
        return numbers;
    }

    //Sort array of harmonics(max element is first in the array)
    private static Harmonics[] sortHarmonics(Harmonics[] harmonics) {
        for (int i = 0; i < harmonics.length; i++) {
            Harmonics max = harmonics[i];
            int max_i = i;
            for (int j = i + 1; j < harmonics.length; j++) {
                if (harmonics[j].getSumCast() > max.getSumCast()) {
                    max = harmonics[j];
                    max_i = j;
                }
            }
            if (i != max_i) {
                Harmonics tmp = harmonics[i];
                harmonics[i] = harmonics[max_i];
                harmonics[max_i] = tmp;
            }
        }
        return harmonics;
    }

    //create start harmony memory
    private static Harmonics[] generateStartHarmonyMemory(int[] cast, int[] value) {
        int[][] hm = new int[hms][cast.length];
        Harmonics[] harmonics = new Harmonics[hm.length];
        int sumValue;
        for (int i = 0; i < harmonics.length; i++) {
            sumValue = 0;
            for (int j = 0; j < hm[i].length && sumValue <= size - value[j]; j++) {
                hm[i][j] = (int) (Math.random() * 2);
                sumValue += value[j];
            }
            harmonics[i] = new Harmonics(hm[i], cast, value);
        }

        return harmonics;
    }

    //stuffing the file with things
    private static void things(String path, int n, int maxValue, int maxCost) {

        try (FileWriter writer = new FileWriter(path, false)) {

            int number;
            //value of things
            for (int i = 0; i < n; i++) {
                number = (int) (Math.random() * maxValue + 1);
                writer.append(String.valueOf(number)).append(" ");
            }
            writer.append('\n');
            // cost of things
            for (int i = 0; i < n; i++) {
                number = (int) (Math.random() * maxCost + 1);
                writer.append(String.valueOf(number)).append(" ");
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static double randomProb() {
        double max = 1, min = 0;
        double range = max - min;
        double scaled = random.nextDouble() * range;
        return scaled + min;
    }

    public static int randomIndex() {
        return Math.toIntExact(Math.round(random.nextDouble() * hms));
    }

}

