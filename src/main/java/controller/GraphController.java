package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import main.java.MDVRP;
import main.java.MDVRPSerializer;

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

    @FXML
    public void initialize() {
        MDVRP problemDTO = MDVRPSerializer.readFromFile("p01");
    }

    public void run() {

    }
}
