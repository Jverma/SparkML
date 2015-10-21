package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by zrm22 on 10/21/15.
 */
public class SparkFrontEndController {

    @FXML
    protected void handleTrainingBrowserButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Node currentNode = (Node)event.getSource();
        File selectedTrainingFile =  fileChooser.showOpenDialog(currentNode.getScene().getWindow());
        System.out.println(selectedTrainingFile.getName());
       //System.out.println(fileName);
    }
}
