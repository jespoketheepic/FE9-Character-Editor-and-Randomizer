package contents;

import contents.io.AutoReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static int region;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // AutoReader.readDataToFile();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/root.fxml"));
        primaryStage.setTitle("Jespoke's FE9 editor");
        primaryStage.setMinWidth(590);
        primaryStage.setMinHeight(410);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
