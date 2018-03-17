package Players;

import Cards.Card;
import Players.Player;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

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
    public void cardPlayedByOpponentEvent(Card card)
    {
        return;
    }

    private Card getCardAtPos(double x, double y)
    {
        // search ObservableChildren nodes (cards) in Players.Player class and return the one at the clicked position.
        return new Card();
    }
}
