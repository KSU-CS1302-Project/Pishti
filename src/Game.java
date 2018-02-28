import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

public class Game extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        GameBoard board = new GameBoard();

        Scene scene = new Scene(board, 800, 600);

        primaryStage.setTitle("PISHTI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}