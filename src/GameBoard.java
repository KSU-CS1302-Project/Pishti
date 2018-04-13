import ActionHandlers.Action;
import ActionHandlers.ActionObserver;
import Cards.Card;
import Cards.Deck;
import Cards.Pile;
import Players.AIPlayer;
import Players.HumanPlayer;
import Players.Player;
import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

public class GameBoard extends StackPane implements ActionObserver
{
    public GameBoard() {
        m_layout = new BorderPane();
        m_animationLayer = new Pane();
        m_animationLayer.setPickOnBounds(false);
        getChildren().addAll(m_layout, m_animationLayer);
        setup(2);
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
        m_layout.setBottom(m_humanPlayer);
        if (numberOfPlayers == 4) {
            Object[] tempArr = m_playerQueue.toArray();
            m_layout.setRight((Player)(tempArr[1]));
            m_layout.setTop((Player)(tempArr[2]));
            m_layout.setLeft((Player)(tempArr[3]));
            m_layout.setCenter(m_deck);
        } else {
            m_layout.setTop((Player)((m_playerQueue.toArray())[1]));
            m_layout.setLeft(m_deck);
            m_layout.setRight(m_pile);
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
        Line animate = new Line();
        animate.setStroke(Color.TRANSPARENT);
        Card animatedCard = new Card(card);
        m_animationLayer.getChildren().addAll(animate, animatedCard);
        Bounds cardBounds = getBoundsInAnimationLayer(card);
        Bounds pileBounds = getBoundsInAnimationLayer(m_pile);

        double xOriginToCenter = animatedCard.getBoundsInLocal().getWidth() / 2;
        double yOriginToCenter = animatedCard.getBoundsInLocal().getHeight() / 2;

        animate.setStartX(cardBounds.getMinX() + xOriginToCenter);
        animate.setStartY(cardBounds.getMinY() + yOriginToCenter);
        animate.setEndX(pileBounds.getMinX() - xOriginToCenter + m_pile.getBoundsInLocal().getWidth());
        animate.setEndY(pileBounds.getMinY() + yOriginToCenter);
        System.out.println("Pile width: " + m_pile.getBoundsInLocal().getWidth());
        System.out.println("DISTANCE: " + (pileBounds.getMinX() - cardBounds.getMinX()));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setPath(animate);
        pathTransition.setDuration(Duration.millis(1500));
        pathTransition.setNode(animatedCard);
        card.setVisible(false);
        pathTransition.play();

        pathTransition.setOnFinished(e -> {
            // finish animation processing
            System.out.println("ANIMATION FINISHED");
            animatedCard.setVisible(false);

            //I think here is where we would need to add in the points values for each card?
            
            // continue
            for (Player player : m_playerQueue) {
                if (player != playerOfCard) {
                    player.cardPlayedByOpponent(card);
                }
            }
            playerOfCard.removeCard(card);

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
        });
    }

    private Bounds getBoundsInAnimationLayer(Node node) {
        Bounds sceneBounds = node.localToScene(node.getBoundsInLocal());
        int depth = 1;
        Stack<Parent> parentStack = new Stack<>();
        Bounds parBounds = sceneBounds;
        parentStack.push(this.getParent());
        if (this.getParent() != null) {
            while (parentStack.peek() != getScene().getRoot()) {
                depth++;
                parentStack.push(parentStack.peek().getParent());
            }
            while (!parentStack.isEmpty()) {
                parBounds = parentStack.pop().parentToLocal(parBounds);
            }
        }
        Bounds gBoardBounds = this.parentToLocal(parBounds);
        Bounds animationLayerBounds = m_animationLayer.parentToLocal(gBoardBounds);
        return animationLayerBounds;
    }

    @Override
    public <T> void onActionNotification(T object, Action action)
    {
        if (action == Action.CARDPLAYED) { // object is Players.Player that played card
            assert(object instanceof Player); // for ActionHandlers.Action.CARDPLAYED object should always be of type Players.Player
            Player player = (Player) object;
            Player peek = m_playerQueue.peek();
            if (player == peek) {
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
    BorderPane m_layout;
    Pane m_animationLayer;
}
