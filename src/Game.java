import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Game extends Application
{
//
    @Override
    public void start(Stage primaryStage)
    {
        GameBoard board = new GameBoard();

        Scene scene = new Scene(board);
        scene.setCamera(new PerspectiveCamera());//

        primaryStage.setTitle("PISHTI");
        primaryStage.getIcons().add(new Image("ace_of_clubs.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}