package Players;

import ActionHandlers.Action;
import ActionHandlers.ActionSubject;
import Cards.Card;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public abstract class Player extends Group
{
    public Player()
    {
        m_subject = new ActionSubject();
        m_queuedCards = new ArrayDeque<>();
    }

    // search through children and remove Cards.Card node passed as argument.
    public void removeCard(Card card)
    {
        // remove from observableChildren list (Group)
        ObservableList<Node> list = getCardList();
        System.out.println("RemoveCard called");
        Card one = new Card();
        for (Node n : list) {
            Card child = (Card) n;
            if (child == card) {
                one = child;
            }
        }
        list.remove(one);

        // remove from Player list of Card representations
        m_cards.remove(card);
    }

    public void dealHand(ArrayList<Card> hand)
    {
        assert(hand.size() <= 4): "Players can only have up to 4 cards in their hand";
        m_cards = hand;
        HBox h = new HBox(m_cards.get(0), m_cards.get(1), m_cards.get(2), m_cards.get(3));

        // for debugging, show border around Player in Scene
        h.setBorder(new Border(new BorderStroke(Paint.valueOf("blue"), BorderStrokeStyle.SOLID, null, null)));
        getChildren().add(h);

    }

    // what this method does will differ between the Players.HumanPlayer and AI. Thus, it is abstract.
    public abstract void cardPlayedByOpponent(Card playedCard);

    // this must be here, in one method, because both the card must be queued and the notification sent, together
    protected final void playCard(Card card)
    {
        addQueuedCard(card);
        m_subject.sendActionNotification(this, Action.CARDPLAYED);
    }

    // removes and returns the next card in the queue
    public Card getNextQueuedCard()
    {
        return m_queuedCards.remove();
    }

    // queues a new card
    protected void addQueuedCard(Card card)
    {
        m_queuedCards.add(card);
    }

    protected final ObservableList<Node> getCardList()
    {
        HBox cardContainer = (HBox) getChildren().get(0);
        return cardContainer.getChildren();
    }

    private Queue<Card> m_queuedCards; //!< keeps track of cards that the player "wants" to play
    protected ArrayList<Card> m_cards;
    public ActionSubject m_subject; //!< class to notify observers (like GameBoard) of actions
    public int capturedPoints = 0;
}
