package main.java.controller;

import javafx.animation.AnimationTimer;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.MDVRP;
import main.java.FileParser;
import main.java.App;
import main.java.Util;
import main.java.domain.Customer;
import main.java.domain.Depot;
import main.java.domain.Node;
import main.java.domain.Vehicle;
import main.java.ga.Chromosome;
import main.java.ga.GeneticAlgorithm;
import main.java.ga.Population;

import java.util.Iterator;
import java.util.List;


public class MDVRPController {

    @FXML
    public Canvas canvas;

    @FXML
    public Button runButton;

    @FXML
    public Button saveButton;

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

    private final long NANO_SECONDS_IN_SECOND = 1_000_000_000;
    private static final double NODE_WIDTH = 5.0;
    private static final double OFFSET = NODE_WIDTH / 2;

    private AnimationTimer animationTimer;
    private final double FRAME_DELAY = 0;

    private int populationSize = 100;
    private double mutationRate = 0.05;
    private double crossoverRate = 0.8;
    private boolean elitism = false;

    private MDVRP problemInstance;
    private GeneticAlgorithm geneticAlgorithm;
    private Population population;

    private boolean running = false;

    @FXML
    public void initialize(Stage stage) {
        setFXMLParameters(stage);
        setProblem();

        animationTimer = new AnimationTimer() {
            final long sleepDuration = (long) (FRAME_DELAY * NANO_SECONDS_IN_SECOND);
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if ((now - lastUpdate < sleepDuration)) { return; }
                lastUpdate = now;

                render(population);
                population = geneticAlgorithm.update();
            }
        };
    }

    public void run() {
        running = !running;
        changeRunButton();

        if (running) {
            saveButton.setDisable(true);
            geneticAlgorithm = new GeneticAlgorithm(problemInstance, populationSize, crossoverRate, mutationRate, elitism);
            population = geneticAlgorithm.getPopulation();
            animationTimer.start();
        } else {
            animationTimer.stop();
            saveButton.setDisable(false);
        }
    }

    public void save() {
        if (!running) {
            FileParser.writeToFile(problemInstance.getProblem(), population.getAlpha());
        }
    }

    private void changeRunButton() {
        if (running) {
            runButton.setText("Stop");
            runButton.setStyle("-fx-background-color: red;");
        } else {
            runButton.setText("Run");
            runButton.setStyle("-fx-background-color: green;");
        }
    }

    private void setFXMLParameters(Stage stage) {
        runButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            stage.getScene().setCursor(Cursor.HAND);
            runButton.setEffect(new DropShadow());
        });
        runButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            stage.getScene().setCursor(Cursor.DEFAULT);
            runButton.setEffect(null);
        });
        saveButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            stage.getScene().setCursor(Cursor.HAND);
            saveButton.setEffect(new DropShadow());
        });
        saveButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            stage.getScene().setCursor(Cursor.DEFAULT);
            saveButton.setEffect(null);
        });

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
        mutationRateField.setPromptText(Double.toString(mutationRate));
        mutationRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                mutationRate = 0.01;
            } else if (newValue.matches("[-+]?[0-9]*\\.?[0-9]*")) {
                mutationRate = Double.parseDouble(newValue);
                if (mutationRate > 1) {
                    mutationRate = 1;
                    mutationRateField.setText(Double.toString(mutationRate));
                }
            } else {
                mutationRateField.setText(Double.toString(mutationRate));
            }
        });
        crossoverRateField.setPromptText(Double.toString(crossoverRate));
        crossoverRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                crossoverRate = 0.08;
            } else if (newValue.matches("[-+]?[0-9]*\\.?[0-9]*")) {
                crossoverRate = Double.parseDouble(newValue);
                if (crossoverRate > 1) {
                    crossoverRate = 1;
                    crossoverRateField.setText(Double.toString(crossoverRate));
                }
            } else {
                crossoverRateField.setText(Double.toString(crossoverRate));
            }
        });
        elitismCheckBox.setSelected(elitism);
    }

    private void setProblem() {
        List<String> dataFileNames = FileParser.getDataFileNames();
        fileSelectChoiceBox.setItems(FXCollections.observableArrayList(dataFileNames));
        fileSelectChoiceBox.getSelectionModel().selectFirst();

        fileSelectChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onProblemSelect(newValue));

        onProblemSelect(dataFileNames.get(0));
    }

    private void onProblemSelect(final String problemId) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        problemInstance = FileParser.readFromFile(problemId);

        transformNodes();
        render(null);
    }

    private void transformNodes() {
        int horizontalChange = -problemInstance.getMinX();
        int verticalChange = -problemInstance.getMinY();
        double scalingFactor = (double) (App.HEIGHT - 20) / Math.max(problemInstance.getMaxX() + horizontalChange, problemInstance.getMaxY() + verticalChange);

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
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (population != null) {
            renderText(population);
            renderVehicles(context, population.getAlpha());
        } else {
            renderText();
        }
        renderCustomers(context);
        renderDepots(context);
    }

    private void renderText() {
        generationText.setText("Generation: ");
        diversityText.setText("Diversity: ");
        maxFitnessText.setText("Max fitness: ");
        averageFitnessText.setText("Average fitness: ");
    }

    private void renderText(Population population) {
        generationText.setText(String.format("Generation: %d", population.getGeneration()));
        diversityText.setText(String.format("Diversity: %f", population.getDiversity()));
        maxFitnessText.setText(String.format("Max fitness: %f", population.getMaxFitness()));
        averageFitnessText.setText(String.format("Average fitness: %f", population.getAverageFitness()));
    }

    private void renderCustomers(final GraphicsContext context) {
        for (Customer customer : problemInstance.getCustomers()) {
            context.setFill(Color.BLACK);
            context.fillOval(customer.getTransformedX(), customer.getTransformedY(), NODE_WIDTH, NODE_WIDTH);
            // context.strokeText(Integer.toString(customer.getId()), customer.getTransformedX(), customer.getTransformedY() - OFFSET);
        }
    }

    private void renderDepots(final GraphicsContext context) {
        context.setFill(Color.RED);
        for (Depot depot : problemInstance.getDepots()) {
            context.fillRect(depot.getTransformedX(), depot.getTransformedY(), NODE_WIDTH, NODE_WIDTH);
        }
    }

    private void renderVehicles(final GraphicsContext context, final Chromosome chromosome) {
        context.setLineWidth(1.0);

        List<Vehicle> vehicles = chromosome.getVehicles();
        Iterator<Color> colors = Util.distinctColors(vehicles.size());

        for (Vehicle vehicle : vehicles) {
            context.setStroke(colors.next());
            Depot depot = vehicle.getDepot();
            Node previousNode = depot;
            for (Customer customer : vehicle.getCustomers()) {
                context.strokeLine(previousNode.getTransformedX() + OFFSET, previousNode.getTransformedY() + OFFSET, customer.getTransformedX() + OFFSET, customer.getTransformedY() + OFFSET);
                previousNode = customer;
            }
            context.strokeLine(depot.getTransformedX() + OFFSET, depot.getTransformedY() + OFFSET, previousNode.getTransformedX() + OFFSET, previousNode.getTransformedY() + OFFSET);
        }
    }

}
