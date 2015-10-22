package controllers;

import DataUtils.TSVReaderUtils;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import mlAlgorithms.RandomForestClass;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zrm22 on 10/21/15.
 */
public class SparkFrontEndController {
    @FXML private TextField trainingDataField;
    @FXML private TextField testingDataField;

    @FXML private ComboBox labelSelector;
    File trainingFile;
    File testingFile;
    HashMap<String, Integer> headerMap;

    @FXML
    protected void handleTrainingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Train File");
        Node currentNode = (Node)event.getSource();
        File selectedTrainingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());
        trainingFile = selectedTrainingFile;
        trainingDataField.setText(selectedTrainingFile.getName());


        //Get Headers
        headerMap = TSVReaderUtils.getHeadersFromFile(trainingFile);
        Set<String> headersSort = headerMap.keySet();

        ObservableList<String> items =  labelSelector.getItems();
        items.remove(0,items.size());
        items.addAll(headersSort);
        labelSelector.setItems(items);
        labelSelector.setValue((items.get(0)));
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
        int labelIndex = headerMap.get((String)labelSelector.getValue());
        System.out.println("LabelINDEX "+labelIndex);
        if(testingDataField.getText().equals("")) {
            app = new RandomForestClass(labelIndex,trainingFile);
        }
        else {
            app = new RandomForestClass(labelIndex,trainingFile,testingFile);
        }
        System.out.println("Begin train");
        final RandomForestModel model = app.trainModel();
        System.out.println("Finish train");
        app.getClassificationError(model);
        System.out.print("Finish run");
        app.closeContext();
    }


}
