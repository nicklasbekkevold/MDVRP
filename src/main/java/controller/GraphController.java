package main.java.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.java.MDVRP;
import main.java.MDVRPSerializer;
import main.java.Main;
import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.ga.Chromosome;
import main.java.ga.Population;

import java.util.List;


public class GraphController {

    @FXML
    public Canvas canvas;

    @FXML
    public Button runButton;

    @FXML
    public ChoiceBox<String> fileSelectChoiceBox;

    @FXML
    public Text generationText;

    @FXML
    public Text diversityText;

    @FXML
    public Text maxFitnessText;

    @FXML
    public Text averageFitnessText;

    @FXML
    public TextField populationSizeField;

    @FXML
    public TextField mutationRateField;

    @FXML
    public TextField crossoverRateField;

    @FXML
    public CheckBox elitismCheckBox;

    private int populationSize = 100;
    private float mutationRate = 0.05F;
    private float crossoverRate = 0.8F;
    private boolean elitism = false;

    private boolean isRunning = false;
    private MDVRP problemInstance = null;

    @FXML
    public void initialize() {
        setFXMLParameters();
        setProblem();
    }

    public void run() {
        isRunning = !isRunning;
        changeRunButton();
        render(new Population());
    }

    private void changeRunButton() {
        if (isRunning) {
            runButton.setText("Stop");
            runButton.setStyle("-fx-background-color: red;");
        } else {
            runButton.setText("Run");
            runButton.setStyle("-fx-background-color: green;");
        }
    }

    private void setFXMLParameters() {
        populationSizeField.setPromptText(Integer.toString(populationSize));
        populationSizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                try {
                    populationSize = Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    populationSizeField.setText(oldValue);
                }
            }
        });
        mutationRateField.setPromptText(Float.toString(mutationRate));
        mutationRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                mutationRate = (float) 0.01;
            } else if (newValue.matches("[-+]?[0-9]*\\.?[0-9]*")) {
                mutationRate = Float.parseFloat(newValue);
                if (mutationRate > 1) {
                    mutationRate = 1;
                    mutationRateField.setText(Float.toString(mutationRate));
                }
            } else {
                mutationRateField.setText(Float.toString(mutationRate));
            }
        });
        crossoverRateField.setPromptText(Float.toString(crossoverRate));
        crossoverRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                crossoverRate = (float) 0.08;
            } else if (newValue.matches("[-+]?[0-9]*\\.?[0-9]*")) {
                crossoverRate = Float.parseFloat(newValue);
                if (crossoverRate > 1) {
                    crossoverRate = 1;
                    crossoverRateField.setText(Float.toString(crossoverRate));
                }
            } else {
                crossoverRateField.setText(Float.toString(crossoverRate));
            }
        });
        elitismCheckBox.setSelected(elitism);
    }

    private void setProblem() {
        List<String> dataFileNames = MDVRPSerializer.getDataFileNames();
        fileSelectChoiceBox.setItems(FXCollections.observableArrayList(dataFileNames));
        fileSelectChoiceBox.getSelectionModel().selectFirst();

        fileSelectChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onProblemSelect(newValue));

        onProblemSelect(dataFileNames.get(0));
    }

    private void onProblemSelect(final String problem) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        problemInstance = MDVRPSerializer.readFromFile(problem);

        transformNodes();
        render(null);
    }

    private void transformNodes() {
        int horizontalChange = 0 - problemInstance.getMinX();
        int verticalChange = 0 - problemInstance.getMinY();
        float scalingFactor = (float) Main.HEIGHT / Math.max(problemInstance.getMaxX() + horizontalChange, problemInstance.getMaxY() + verticalChange);

        for (Customer customer : problemInstance.getCustomers()) {
            customer.translate(horizontalChange, verticalChange);
            customer.scale(scalingFactor);
        }

        for (Depot depot : problemInstance.getDepots()) {
            depot.translate(horizontalChange, verticalChange);
            depot.scale(scalingFactor);
        }
    }

    private void render(final Population population) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        renderText(population);
        renderCustomers(context);
        renderDepots(context);
        // renderVehicles(context, population.getAlpha());
    }

    private void renderCustomers(final GraphicsContext context) {
        context.setFill(Color.BLACK);
        for (Customer customer : problemInstance.getCustomers()) {
            context.fillOval(customer.getTransformedX(), customer.getTransformedY(), 5, 5);
        }
    }

    private void renderDepots(final GraphicsContext context) {
        context.setFill(Color.RED);
        for (Depot depot : problemInstance.getDepots()) {
            context.fillRect(depot.getTransformedX(), depot.getTransformedY(), 5, 5);
        }
    }

    private void renderText(Population population) {
        String generationTextString = "Generation: ";
        String diversityTextString = "Diversity: ";
        String maxFitnessTextString = "Max fitness: ";
        String averageFitnessTextString = "Average fitness: ";

        if (population != null)  {
            generationTextString += population.getGeneration();
            diversityTextString += population.getDiversity();
            maxFitnessTextString += population.getMaxFitness();
            averageFitnessTextString += population.getAverageFitnessFitness();
        }

        generationText.setText(generationTextString);
        diversityText.setText(diversityTextString);
        maxFitnessText.setText(maxFitnessTextString);
        averageFitnessText.setText(averageFitnessTextString);
    }

}
