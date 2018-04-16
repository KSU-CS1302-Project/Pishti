package Players;

import Cards.Card;
import javafx.concurrent.Task;

import java.util.ArrayList;

public class AIPlayer extends Player
{
    /*
    When this method is called by GameBoard, notifying the AIPlayer that a card has been played by an opponent,
    it starts the asynchronous task of selecting a card to play.
     */
    @Override
    public void cardPlayedByOpponent(Card playedCard)
    {
        Task<Card> task = new Task<Card>() {
            @Override protected Card call() throws Exception {
                Card cardToPlay;
                System.out.println("In Task");
                cardToPlay = getCardToPlay();
                return cardToPlay;
            }
        };
        task.setOnSucceeded(t -> {
            System.out.println("Task.setOnSucceeded called.");
            playCard((Card) t.getSource().getValue());
        });
        new Thread(task).start();
        System.out.println("THIS SHOULD RUN BEFORE THE TASK!!!");
    }

    private Card getCardToPlay()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // for now just return the first card in the list
        return ((Card) getCardList().get(0));
    }

    ArrayList<Card> m_cardSeen;
}
