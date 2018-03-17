package Players;

import ActionHandlers.Action;
import ActionHandlers.ActionSubject;
import Cards.Card;
import Cards.CardView;
import Cards.Rank;
import Cards.Suit;
import javafx.scene.Group;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public abstract class Player extends Group
{
    public Player()
    {
        m_subject = new ActionSubject();
        m_queuedCards = new ArrayDeque<>();
        getChildren().addAll(new CardView(new Card(Suit.DIAMOND, Rank.EIGHT)));
    }

    // search through children and remove Cards.Card node passed as argument.
    public void removeCard(Card card)
    {
        //TODO remove from observable children list in Group first
        m_cards.remove(card);
    }

    public void dealHand(ArrayList<Card> hand)
    {
        assert(hand.size() == 4): "Players can only have 4 cards in their hand";
        m_cards = hand;
    }

    // what this method does will differ between the Players.HumanPlayer and AI. Thus, it is abstract.
    public abstract void cardPlayedByOpponentEvent(Card card);

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

    private Queue<Card> m_queuedCards; //!< keeps track of cards that the player "wants" to play
    protected ArrayList<Card> m_cards;
    public ActionSubject m_subject; //!< class to notify observers (like GameBoard) of actions
    public int capturedPoints = 0;
}
