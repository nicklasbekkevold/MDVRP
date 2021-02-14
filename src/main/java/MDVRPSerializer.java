package main.java;

import main.java.domain.Customer;
import main.java.domain.Depot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Serializes data files to main.java.MDVRP java objects
 */
public class MDVRPSerializer {

    /**
     * @param problem Name of problem file to encode a main.java.MDVRP instance from
     * @return main.java.MDVRP problem instance
     */
    public static MDVRP readFromFile(final String problem) {

        String dataFilesPath = new File("src/main/resources/data_files").getAbsolutePath();
        String problemPath = dataFilesPath + File.separator + problem;

        System.out.println("Reading from file...");
        MDVRP mdvrp = new MDVRP();

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
                    mdvrp.setNumberOfVehiclesPerDepot(m);
                    mdvrp.setNumberOfCustomers(n);
                    mdvrp.setNumberOfDepots(t);
                }

                else if (lineNumber > 0 && lineNumber < t + 1) {
                    int D = constants.get(0);
                    int Q = constants.get(1);
                    mdvrp.setMaxRouteDuration(D);
                    mdvrp.setMaxVehicleLoad(Q);
                }

                else if (lineNumber > t && lineNumber < n + t + 1) {
                    int i = constants.get(0);
                    int x = constants.get(1);
                    int y = constants.get(2);
                    int d = constants.get(3);
                    int q = constants.get(4);
                    mdvrp.addCustomer(new Customer(i, x, y, d, q));
                }

                else {
                    int i = constants.get(0);
                    int x = constants.get(1);
                    int y = constants.get(2);
                    mdvrp.addDepot(new Depot(i, x, y));
                }

                lineNumber++;
                line = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading from file successful.");
        return mdvrp;
    }

    private static List<Integer> parseLine(final String line) {
        Scanner scanner = new Scanner(line);
        List<Integer> numbers = new ArrayList<>();
        while (scanner.hasNextInt()) {
            numbers.add(scanner.nextInt());
        }
        return numbers;
    }

    private MDVRPSerializer () {}

    public static void main(String[] args) {
        String directory = System.getProperty("user.home") + File.separator + "IdeaProjects";
        String folderName = "main.java.MDVRP" + File.separator + "src" + File.separator + "main/resources/data_files";
        String fileName = "p23";
        String absolutePath = directory + File.separator + folderName + File.separator + fileName;

        MDVRP mdvrp = MDVRPSerializer.readFromFile(absolutePath);
        System.out.println(mdvrp.getCustomers().get(10).getDemand());

    }
}

