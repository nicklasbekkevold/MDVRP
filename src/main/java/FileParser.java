package main.java;

import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Vehicle;
import main.java.ga.Chromosome;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Serializes and Deserializes data to and from main.java.MDVRP java objects
 */
public class FileParser {

    static String dataFilesPath = new File("src/main/resources/data_files").getAbsolutePath();
    static String solutionFilesPath = new File("src/main/resources/solution_files").getAbsolutePath();

    // Used for plotting
    static int minX;
    static int minY;
    static int maxX;
    static int maxY;

    // Enforces static class
    private FileParser() {}

    /**
     * @return list of the data file names: p01, p02, ...
     */
    public static List<String> getDataFileNames() {
        List<String> dataFileNames= new ArrayList<>();

        final File dataFilesFolder = new File(dataFilesPath);
        for (final File dataFile : dataFilesFolder.listFiles()) {
            if (!dataFile.isDirectory()) {
                dataFileNames.add(dataFile.getName());
            }
        }

        Collections.sort(dataFileNames);
        return dataFileNames;
    }

    /**
     * @param problemId Name of problem file to encode a main.java.MDVRP instance from
     * @return main.java.MDVRP problem instance
     */
    public static MDVRP readFromFile(final String problemId) {

        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;

        String problemPath = dataFilesPath + File.separator + problemId;
        System.out.println(String.format("Reading from file %s ...", problemId));

        int numberOfVehiclesPerDepot = -1; // m
        int numberOfCustomers = -1; // n
        int numberOfDepots = -1; // t

        int maxRouteDuration = -1; // D
        int maxVehicleLoad = -1; // Q

        List<Customer> customers = new ArrayList<>();
        List<Depot> depots = new ArrayList<>();
        Depot.resetSerialNumber();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(problemPath))) {

            int lineNumber = 0;
            String line = bufferedReader.readLine();

            int n = -1;
            int t = -1;

            while (line != null) {
                List<Integer> constants = parseLine(line);

                if (lineNumber == 0) {
                    int m = constants.get(0);
                    n = constants.get(1);
                    t = constants.get(2);
                    numberOfVehiclesPerDepot = m;
                    numberOfCustomers = n;
                    numberOfDepots = t;
                }

                else if (lineNumber > 0 && lineNumber <= t) {
                    int D = constants.get(0);
                    int Q = constants.get(1);
                    maxRouteDuration = D;
                    maxVehicleLoad = Q;
                }

                else if (lineNumber > t && lineNumber <= n + t) {
                    int i = constants.get(0);
                    int x = constants.get(1);
                    int y = constants.get(2);
                    int d = constants.get(3); // Ignored: 0 for all files
                    int q = constants.get(4);
                    customers.add(new Customer(i, x, y, q));
                    updateMinMax(x, y);
                }

                else {
                    int i = constants.get(0);
                    int x = constants.get(1);
                    int y = constants.get(2);
                    depots.add(new Depot(i, x, y));
                    updateMinMax(x, y);
                }

                lineNumber++;
                line = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Reading from file successful.");

        return new MDVRP(
            problemId,
            numberOfVehiclesPerDepot,
            numberOfCustomers,
            numberOfDepots,
            maxRouteDuration,
            maxVehicleLoad,
            depots,
            customers,
            minX,
            minY,
            maxX,
            maxY
        );
    }

    private static void updateMinMax(int x, int y) {
        FileParser.minX = Math.min(FileParser.minX, x);
        FileParser.minY = Math.min(FileParser.minY, y);
        FileParser.maxX = Math.max(FileParser.maxX, x);
        FileParser.maxY = Math.max(FileParser.maxY, y);
    }

    private static List<Integer> parseLine(final String line) {
        Scanner scanner = new Scanner(line);
        List<Integer> numbers = new ArrayList<>();
        while (scanner.hasNextInt()) {
            numbers.add(scanner.nextInt());
        }
        return numbers;
    }

    public static void writeToFile(final String problemId, Chromosome chromosome) {
        final String solutionPath = solutionFilesPath + File.separator + problemId;

        System.out.println(String.format("Writing to solution file %s ...", problemId));
        final File solutionFile = new File(solutionPath);

        try (PrintWriter writer = new PrintWriter(solutionFile)) {
            writer.println(chromosome);
            for (Vehicle vehicle : chromosome.getVehicles()) {
                writer.println(vehicle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Writing to file successful.");
    }

    public static void saveTrainingData(List<Double> bestDurations, List<Double> averageDurations) {
        File trainingDataFile = new File("src/main/resources/training_data.txt");

        System.out.println("Saving training data...");
        try (PrintWriter writer = new PrintWriter(trainingDataFile)) {
            for (int i = 0; i < bestDurations.size(); i++) {
                writer.println(String.format("%d %.2f %.2f", i + 1, bestDurations.get(i), averageDurations.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saving training data successful.");
    }





}

