import javafx.scene.layout.BorderPane;

public class GameBoard extends BorderPane
{
    public GameBoard() {
        setCenter(new Card(Suit.HEART, Rank.TEN));
        setLeft(new Deck());
    }
}
