package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import mlAlgorithms.RandomForestClass;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.io.File;

/**
 * Created by zrm22 on 10/21/15.
 */
public class SparkFrontEndController {
    @FXML private TextField trainingDataField;
    @FXML private TextField testingDataField;
    File trainingFile;
    File testingFile;

    @FXML
    protected void handleTrainingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Train File");
        Node currentNode = (Node)event.getSource();
        File selectedTrainingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());
        trainingFile = selectedTrainingFile;
        trainingDataField.setText(selectedTrainingFile.getName());
        System.out.println(selectedTrainingFile.getName());
       //System.out.println(fileName);
    }
    @FXML
    protected void handleTestingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Test File");
        Node currentNode = (Node)event.getSource();
        File selectedTestingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());
        testingFile = selectedTestingFile;
        testingDataField.setText(selectedTestingFile.getName());
        System.out.println(selectedTestingFile.getName());
        //System.out.println(fileName);
    }

    @FXML
    protected void handleRunAlgorithm(ActionEvent event) {
        System.out.println("Running Random Forest");
        RandomForestClass app = null;
        if(testingDataField.getText().equals("")) {
            app = new RandomForestClass(trainingFile);
        }
        else {
            app = new RandomForestClass(trainingFile,testingFile);
        }
        System.out.println("Begin train");
        final RandomForestModel model = app.trainModel();
        System.out.println("Finish train");
        app.getClassificationError(model);
        System.out.print("Finish run");
    }


}
