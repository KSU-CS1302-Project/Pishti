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

        // for debugging, show border around Player in Scene
        HBox cardParent = new HBox();
        cardParent.setBorder(new Border(new BorderStroke(Paint.valueOf("blue"), BorderStrokeStyle.SOLID, null, null)));
        cardParent.setPrefSize(670, 244);
        getChildren().add(cardParent);
        cardParent.setMinHeight(242.0); //set minimum height to height of card images. Change later from hardcoded value
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
        for (Card card : hand) {
            getCardList().add(card);
        }
    }

    // what this method does will differ between the Players.HumanPlayer and AI. Thus, it is abstract.
    public abstract void cardPlayedByOpponent(Card playedCard);

    // implement in subclasses
    public void roundWon()
    {
        return;
    }

    // this must be here, in one method, because both the card must be queued and the notification sent, together
    protected final void playCard(Card card)
    {
        if (!m_queuedCards.contains(card))
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

    public final ObservableList<Node> getCardList()
    {
        HBox cardContainer = (HBox) getChildren().get(0);
        return cardContainer.getChildren();
    }

    private Queue<Card> m_queuedCards; //!< keeps track of cards that the player "wants" to play
    protected ArrayList<Card> m_cards;
    public ActionSubject m_subject; //!< class to notify observers (like GameBoard) of actions
    public int capturedPoints = 0;
    public int capturedCards = 0;
}
