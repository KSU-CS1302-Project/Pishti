import ActionHandlers.Action;
import ActionHandlers.ActionObserver;
import Cards.Card;
import Cards.Deck;
import Players.AIPlayer;
import Players.HumanPlayer;
import Players.Player;
import javafx.scene.layout.BorderPane;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

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
        m_playOrderQueue = new ArrayDeque<>();
        m_players = new Player[numberOfPlayers];
        m_humanPlayer = new HumanPlayer();

        m_players[0] = m_humanPlayer;
        m_playOrderQueue.add(m_humanPlayer);

        for (int i = 1; i < numberOfPlayers; ++i) {
            m_players[i] = new AIPlayer();
            m_playOrderQueue.add(m_players[i]);
        }

        setBottom(m_humanPlayer);
        setTop(m_players[1]);
        if (numberOfPlayers == 4) {
            setLeft(m_players[2]);
            setCenter(m_deck);
            setRight(m_players[3]);
        } else {
            setLeft(m_deck);
        }

        for (Player player : m_players) {
            player.m_subject.addObserver(this);
        }

        dealCards();
    }

    // gives each player an ArrayList containing 4 cards.
    private void dealCards()
    {
        for (Player player : m_players) {
            ArrayList<Card> playerHand = new ArrayList<>();
            for (int i = 0; i < 4; ++i) {
                Card card = m_deck.draw();
                playerHand.add(card);
            }
            player.dealHand(playerHand);
        }
    }

    @Override
    public <T> void onActionNotification(T object, Action action)
    {
        if (action == Action.CARDPLAYED) { // object is Players.Player that played card
            assert(object instanceof Player); // for ActionHandlers.Action.CARDPLAYED object should always be of type Players.Player
            Player player = (Player) object;
            if (player == m_playOrderQueue.peek()) {
                cardPlayed(player, player.getNextQueuedCard());
                m_playOrderQueue.remove(); // remove player who just played card from front of queue...
                m_playOrderQueue.add(player); // and add back at end of queue
            }
        }
    }

    // card was played.  remove it from the hand of the player that played it, and notify other players.
    private void cardPlayed(Player playerOfCard, Card card)
    {
        playerOfCard.removeCard(card);
        for (int i = 0; i < m_players.length; ++i) {
            Player player = m_players[i];
            if (player != playerOfCard) {
                player.cardPlayedByOpponentEvent(card);
            }
        }

        //TODO add to pile, check for possible award of points, and continue play (if cards left in deck)

    }

    private Queue<Player> m_playOrderQueue; //!< Tracks and enforces the turn order for all players
    private HumanPlayer m_humanPlayer;
    private Player[] m_players;
    private Deck m_deck;
}
