import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

public class Game extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        GameBoard board = new GameBoard();

        Scene scene = new Scene(board);

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