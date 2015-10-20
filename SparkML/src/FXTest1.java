/**
 * Created by zrm22 on 10/20/15.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXTest1 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("./layout/fxml_example.fxml"));

            Scene scene = new Scene(root, 300, 275);

            primaryStage.setTitle("FXML Welcome");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e) {
            System.out.print("ERROR");
        }
    }

}
