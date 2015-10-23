package controllers;

import DataUtils.MatrixUtils;
import DataUtils.TSVReaderUtils;
import javafx.application.Platform;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mlAlgorithms.RandomForestClass;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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

    @FXML private TextArea actiontarget;



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
    protected void handleRunAlgorithm(ActionEvent event) throws IOException{

//        Stage stage1 = new Stage();
//        Parent root = FXMLLoader.load(getClass().getResource("../layout/fxml_example.fxml"));
//
//        stage1.setScene(new Scene(root));
//        stage1.setTitle("Test");
//        stage1.initModality(Modality.APPLICATION_MODAL);
//        stage1.initOwner(run.getScene().getWindow());
//        stage1.show();
//

        run.setDisable(true);
        final Thread runRFThread = new Thread(new Runnable() {
            @Override
            public void run() {
                actiontarget.setVisible(true);
                updateUI("Starting Random Forest for Classification",actiontarget);

                System.out.println("Running Random Forest");
                RandomForestClass app = null;
                int labelIndex = headerMap.get((String)labelSelector.getValue());
                System.out.println("LabelINDEX "+labelIndex);

                updateUI("Using " + labelSelector.getValue() + " as the class label.",actiontarget);
                updateUI("Loading Files",actiontarget);

                HashMap<String,String> params = new HashMap<String,String>();
                params.put("numClasses",param2_value.getText());
                params.put("numTrees",param1_value.getText()); // Use more in practice.
                params.put("featureSubsetStrategy" , "auto"); // Let the algorithm choose.
                params.put("impurity", "gini");
                params.put("maxDepth", "5");
                params.put("maxBins" , "32");
                params.put("seed" , "12345");

                if(testingDataField.getText().equals("")) {
                    app = new RandomForestClass(labelIndex,trainingFile,params);
                }
                else {
                    app = new RandomForestClass(labelIndex,trainingFile,testingFile,params);
                }
                //TODO Format Params better
                updateUI("Creating RandomForest with the following parameters:\n"+params.toString()+"\n\n",actiontarget);

                System.out.println("Begin train");
                updateUI("Begin Training",actiontarget);

                try {
                    final RandomForestModel model = app.trainModel();
                    System.out.println("Finished Training");

                    updateUI("FinishedTraining", actiontarget);

                    updateUI("Test Error: " + app.getClassificationError(model), actiontarget);

                    updateUI("Precision:"+app.getMultClassMetrics(app.computePredictionPairs(model)).precision(),actiontarget);
                    updateUI("Recall:"+app.getMultClassMetrics(app.computePredictionPairs(model)).recall(),actiontarget);

                    updateUI("AUC:"+app.getBinClassMetrics(app.computePredictionPairs(model)).areaUnderROC(),actiontarget);


                    Matrix matrix = app.getConfusionMatrix(model);
                    double[][] matrixDoubleArray = new double[matrix.numRows()][matrix.numCols()];
                    System.out.println("\ta\t\tb\t\t<-classified as");
                    for (int i = 0; i < matrix.numCols(); i++) {
                        System.out.print("" + i + "\t");
                        for (int j = 0; j < matrix.numRows(); j++) {
                            //System.out.print(matrix.apply(i,j)+"\t\t\t");
                            System.out.printf("%5d", (int) matrix.apply(i, j));
                            matrixDoubleArray[j][i] = matrix.apply(j, i);
                        }
                        System.out.println();
                    }
                    //System.out.println(matrix.toString());

                    System.out.println("**********\n" + MatrixUtils.matrixToString(matrixDoubleArray));
                    updateUI(MatrixUtils.matrixToString(matrixDoubleArray), actiontarget);

                    System.out.print("Finish run");
                    enableRunButton(run);
                    app.closeContext();
                }
                catch(Exception e) {
                    updateUI("\n\nERROR: Exception occurred:\n"+e, actiontarget);
                    enableRunButton(run);
                    app.closeContext();
                }
            }
        });


        runRFThread.setDaemon(true);
        runRFThread.start();

        /*
        actiontarget.setVisible(true);
        actiontarget.setText("Starting Random Forest for Classification");

        System.out.println("Running Random Forest");
        RandomForestClass app = null;
        int labelIndex = headerMap.get((String)labelSelector.getValue());
        System.out.println("LabelINDEX "+labelIndex);
        appendText("\nUsing " + labelSelector.getValue() + " as the class label.");

        appendText("\nLoading Files");
        //addTextToActionField("Using " + labelSelector.getValue() + " as the class label.");
        //addTextToActionField("Loading Files");

        if(testingDataField.getText().equals("")) {
            app = new RandomForestClass(labelIndex,trainingFile);
        }
        else {
            app = new RandomForestClass(labelIndex,trainingFile,testingFile);
        }
        System.out.println("Begin train");
        //addTextToActionField("Beginning Training");
        appendText("\nBeginning Training");
        final RandomForestModel model = app.trainModel();
        System.out.println("Finished Training");
        //addTextToActionField("Finished Training");
        appendText("\nFinishedTraining");
        app.getClassificationError(model);
        System.out.print("Finish run");
        app.closeContext();
        */
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

    public void addTextToActionField(String text) {
        actiontarget.setText(actiontarget.getText()+"\n"+text);

    }
    public void appendText(String str) {
        //runSafe(() -> actiontarget.appendText(str));
    }

    private void updateUI(final String message, final TextArea textArea) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(message+"\n");
            }
        });
    }
    private void enableRunButton( final Button runButton) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                runButton.setDisable(false);
            }
        });
    }



}
