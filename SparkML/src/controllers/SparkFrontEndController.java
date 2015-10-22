package controllers;

import DataUtils.TSVReaderUtils;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    @FXML private Button run;

    @FXML private ComboBox labelSelector;
    @FXML private Text label_text;

    @FXML private ComboBox algoSelector;
    File trainingFile;
    File testingFile;
    HashMap<String, Integer> headerMap;


    @FXML private Text param1;
    @FXML private TextField param1_value;
    @FXML private Text param2;
    @FXML private TextField param2_value;




    @FXML
    protected void handleTrainingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Train File");
        Node currentNode = (Node)event.getSource();
        File selectedTrainingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());

        if(selectedTrainingFile!=null) {
            trainingFile = selectedTrainingFile;
            trainingDataField.setText(selectedTrainingFile.getName());

            //Get Headers
            headerMap = TSVReaderUtils.getHeadersFromFile(trainingFile);
            Set<String> headersSort = headerMap.keySet();
            labelSelector.setVisible(true);
            label_text.setVisible(true);
            ObservableList<String> items = labelSelector.getItems();
            items.remove(0, items.size());
            items.addAll(headersSort);
            labelSelector.setItems(items);
            labelSelector.setValue((items.get(0)));

            run.setDisable(false);
            System.out.println(selectedTrainingFile.getName());
        }
       //System.out.println(fileName);
    }
    @FXML
    protected void handleTestingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Test File");
        Node currentNode = (Node)event.getSource();
        File selectedTestingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());
        if(selectedTestingFile!=null) {
            testingFile = selectedTestingFile;
            testingDataField.setText(selectedTestingFile.getName());
            System.out.println(selectedTestingFile.getName());
        }
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

    @FXML
    protected void algorithmChoiceHandle(ActionEvent event) {
        System.out.println("Choice:"+algoSelector.getValue());
        param2.setDisable(false);
        param2_value.setDisable(false);

        switch((String)algoSelector.getValue()) {
            case "Random Forest for Classification":
                System.out.println(1);
                param1.setText("nTrees");
                param1_value.setText("10");
                param2.setText("nClasses");
                param2_value.setText("2");
                break;
            case "Naive Bayes for Classification":
                param1.setText("ABC");
                param1_value.setText("");
                param2.setText("nClasses");
                param2_value.setText("2");
                System.out.println(2);
                break;
            case "Random Forest for Regression":
                System.out.println(3);
                param1.setText("nTrees");
                param1_value.setText("10");
                param2.setDisable(true);
                param2_value.setDisable(true);
                param2.setText("nClasses");
                param2_value.setText("0");
                break;
        }
    }

}
