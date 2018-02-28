import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

public class Game extends Application
{

    @Override
    public void start(Stage primaryStage)
    {
        Card card = new Card(Suit.HEART, Rank.TEN);
        Group root = new Group(card);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("PISHTI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}