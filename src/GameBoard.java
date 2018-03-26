import ActionHandlers.Action;
import ActionHandlers.ActionObserver;
import Cards.Card;
import Cards.Deck;
import Cards.Pile;
import Players.AIPlayer;
import Players.HumanPlayer;
import Players.Player;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class GameBoard extends BorderPane implements ActionObserver
{
    public GameBoard() {
        setup(2);
    }

    public GameBoard(int numberOfPlayers) {
        setup(numberOfPlayers);
    }

    private void setup(int numberOfPlayers)
    {
        assert(numberOfPlayers == 2 || numberOfPlayers == 4): "Incorrect number of players: must be 2 or 4";

        m_deck = new Deck();
        m_pile = new Pile();
        m_playerQueue = new ArrayBlockingQueue<Player>(numberOfPlayers);
        m_humanPlayer = new HumanPlayer();

        m_playerQueue.add(m_humanPlayer);
        for (int i = 1; i < numberOfPlayers; ++i) {
            m_playerQueue.add(new AIPlayer());
        }

        // setup visual layout of GameBoard based on number of players
        setBottom(m_humanPlayer);
        if (numberOfPlayers == 4) {
            Object[] tempArr = m_playerQueue.toArray();
            setRight((Player)(tempArr[1]));
            setTop((Player)(tempArr[2]));
            setLeft((Player)(tempArr[3]));
            setCenter(m_deck);
        } else {
            setTop((Player)((m_playerQueue.toArray())[1]));
            setLeft(m_deck);
            setRight(m_pile);
        }

        // setup ActionNotifications between players and GameBoard
        for (Player player : m_playerQueue) {
            player.m_subject.addObserver(this);
        }

        dealCards();
    }

    // gives each player an ArrayList containing 4 cards.
    private void dealCards()
    {
        int remainingCards = m_deck.remainingCards();
        for (Player player : m_playerQueue) {
            ArrayList<Card> playerHand = new ArrayList<>();
            for (int i = 0; i < Math.min(4, remainingCards / 2); ++i) {
                Card card = m_deck.draw();
                playerHand.add(card);
            }
            player.dealHand(playerHand);
        }
    }

    // card was played.  remove it from the hand of the player that played it, and notify other players.
    private void cardPlayed(Player playerOfCard, Card card)
    {
        playerOfCard.removeCard(card);
        for (Player player : m_playerQueue) {
            if (player != playerOfCard) {
                player.cardPlayedByOpponent(card);
            }
        }

        //TODO add to pile, check for possible award of points, and continue play (if cards left in deck)
        m_pile.addCard(card);
        /*
        PointManager class:
            Based on current value of Pile (only top card matters?),
            give point to player who played 'capturing' card.
         */

        boolean allPlayerHandsEmpty = true;
        for (Player player : m_playerQueue) {
            if (!player.getCardList().isEmpty()) {
                allPlayerHandsEmpty = false;
            }
        }
        if (allPlayerHandsEmpty) {
            dealCards();
        }

    }

    @Override
    public <T> void onActionNotification(T object, Action action)
    {
        if (action == Action.CARDPLAYED) { // object is Players.Player that played card
            assert(object instanceof Player); // for ActionHandlers.Action.CARDPLAYED object should always be of type Players.Player
            Player player = (Player) object;
            Player peek = m_playerQueue.peek();
            if (player == m_playerQueue.peek()) {
                m_playerQueue.remove(); // remove player who just played card from front of queue...
                m_playerQueue.add(player); // and add back at end of queue
                cardPlayed(player, player.getNextQueuedCard());
            }
        }
    }

    private HumanPlayer m_humanPlayer;
    private Queue<Player> m_playerQueue; // holds players - In queue to track and enforce turn order.
    private Deck m_deck;
    private Pile m_pile;
}
