package Players;

import Cards.Card;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class HumanPlayer extends Player
{
    public HumanPlayer()
    {
        setup();
    }

    private void setup()
    {
        setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                System.out.println("Clicked in HumanPlayer");
                double x = e.getX();
                double y = e.getY();
                Card clickedCard = getCardAtPos(x, y);
                playCard(clickedCard);
            }
        });
    }

    @Override
    public void cardPlayedByOpponent(Card playedCard)
    {
        return;
    } // does nothing.  To notify human player, just show GameBoard changes

    private Card getCardAtPos(double x, double y)
    {
        // search ObservableChildren nodes (cards) in Players.Player class and return the one at the clicked position.
        HBox hbox = (HBox) getChildren().get(0);
        Point2D hBoxPos = hbox.parentToLocal(x, y);
        for (Node n : hbox.getChildren()) {
            Card cv = (Card) n;
            Point2D cardPos = cv.parentToLocal(hBoxPos);
            if (cv.contains(cardPos)) {
                return cv;
            }
        }
        return new Card();
    }
}
