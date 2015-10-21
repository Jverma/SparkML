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

    File trainingFile;

    @FXML
    protected void handleTrainingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Node currentNode = (Node)event.getSource();
        File selectedTrainingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());
        trainingFile = selectedTrainingFile;
        trainingDataField.setText(selectedTrainingFile.getName());
        System.out.println(selectedTrainingFile.getName());
       //System.out.println(fileName);
    }

    @FXML
    protected void handleRunAlgorithm(ActionEvent event) {
        System.out.println("Running Random Forest");
        RandomForestClass app = new RandomForestClass(trainingFile);
        System.out.println("Begin train");
        final RandomForestModel model = app.trainModel();
        System.out.println("Finish train");
        app.getClassificationError(model);
        System.out.print("Finish run");
    }


}
