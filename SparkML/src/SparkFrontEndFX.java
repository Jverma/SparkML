import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by zrm22 on 10/20/15.
 */
public class SparkFrontEndFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("./layout/sparkFrontEndLayout.fxml"));

            Scene scene = new Scene(root, 1000, 700);

            primaryStage.setTitle("FXML Welcome");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e) {
            System.out.print("ERROR");
        }
    }

}
