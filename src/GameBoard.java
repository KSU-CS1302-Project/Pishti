import javafx.scene.layout.BorderPane;

public class GameBoard extends BorderPane
{
    public GameBoard() {
        super(new Card(Suit.HEART, Rank.TEN));
    }
}
