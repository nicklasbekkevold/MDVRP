package main.java.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import main.java.MDVRP;
import main.java.MDVRPSerializer;

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
    private double mutationRate = 0.05;
    private double crossoverRate = 0.8;
    private boolean elitism = false;

    private boolean running = false;
    private MDVRP MDVRPInstance = null;

    @FXML
    public void initialize() {
        setFXMLParameters();
        setProblem();
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
        List<String> dataFileNames = MDVRPSerializer.getDataFileNames();
        fileSelectChoiceBox.setItems(FXCollections.observableArrayList(dataFileNames));
        fileSelectChoiceBox.getSelectionModel().selectFirst();

        fileSelectChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onProblemSelect(newValue));

        onProblemSelect(dataFileNames.get(0));
    }

    private void onProblemSelect(String problem) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        MDVRPInstance = MDVRPSerializer.readFromFile(problem);
    }

    public void run() {

    }
}
