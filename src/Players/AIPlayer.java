package Players;

import Cards.Card;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;

public class AIPlayer extends Player
{
    @Override
    public void cardPlayedByOpponent(Card playedCard)
    {
        Task<Card> task = new Task<Card>() {
            @Override protected Card call() throws Exception {
                Card cardToPlay;
                System.out.println("In Task");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        // for now just return the first card in the list
        return ((Card) getCardList().get(0));
    }

    ArrayList<Card> m_cardSeen;
}
