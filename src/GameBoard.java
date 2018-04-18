import ActionHandlers.Action;
import ActionHandlers.ActionObserver;
import Cards.Card;
import Cards.Deck;
import Cards.Pile;
import Cards.Rank;
import Players.AIPlayer;
import Players.HumanPlayer;
import Players.Player;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.beans.value.ObservableIntegerValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
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
        m_pointsDisplay = new Text[2];
        m_pointsDisplayPane = new HBox();
        m_pointsDisplayPane.setSpacing(50);
        m_pointsDisplayPane.setAlignment(Pos.CENTER);
        m_pointsDisplay[0] = new Text("Player 1: " + "0");
        m_pointsDisplay[1] = new Text("Player 2: " + "0");
        m_pointsDisplay[0].setStyle("-fx-fill: RED; -fx-font-size: 16;");
        m_pointsDisplay[1].setStyle("-fx-fill: blue; -fx-font-size: 16;");
        m_layout.setCenter(m_pointsDisplayPane);
        m_pointsDisplayPane.getChildren().addAll(m_pointsDisplay);
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
        int fullDuration = 1000;
        /*
        START PATHTRANSITION SETUP
         */
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
        pathTransition.setDuration(Duration.millis(fullDuration));
        pathTransition.setNode(animatedCard);
        card.setVisible(false);
        //pathTransition.play();
        pathTransition.setOnFinished(e -> {
            // finish animation processing
            System.out.println("ANIMATION FINISHED");
            animatedCard.setVisible(false);

            //I think here is where we would need to add in the points values for each card?

            if((m_pile.getTopCard() != null) && (card.getRank() == Rank.JACK)) {
                System.out.println("Jack played");
                m_pile.addCard(card);
                System.out.println("Jack added to stack");
                addPointsToPlayer(playerOfCard, m_pile.getPileValue());
                System.out.println("Points added");
                if(m_pile.getNumCards() > 26) {
                    addPointsToPlayer(playerOfCard, 3);
                    System.out.println("Majority of cards awarded");
                }
                m_pile.flush();
                System.out.println("Deck cleared");
            }

            else if((m_pile.getTopCard() != null) && (m_pile.getTopCard().getRank() == Rank.JACK)
                    && (m_pile.getTopCard().getRank() == card.getRank()) && m_pile.getNumCards() == 1) {
                System.out.println("Jack PISHTI found!");
                m_pile.addCard(card);
                System.out.println("Jack Pishti Added to stack");
                addPointsToPlayer(playerOfCard, 20);
                System.out.println("20 points added");
                m_pile.flush();
            }

            else if((m_pile.getTopCard() != null) && (m_pile.getTopCard().getRank() == card.getRank())
                    && (m_pile.getNumCards() == 1)) {
                System.out.println("Non-Jack Pishti found!");
                m_pile.addCard(card);
                System.out.println("Non-jack pishti added to stack");
                addPointsToPlayer(playerOfCard, 10);
                System.out.println("10 points added");
                m_pile.flush();
            }

            else if ((m_pile.getTopCard() != null) && (m_pile.getTopCard().getRank() == card.getRank())) {
                System.out.println("KSJFDLKJSFDLKJS:LKFDJ");
                System.out.println("JJKJDSLKFJSLKDFJLKSDJF");
                m_pile.addCard(card);
                System.out.println("add card success");
                addPointsToPlayer(playerOfCard, m_pile.getPileValue());
                System.out.println("Adding Points to playerOfCard success");
                if(m_pile.getNumCards() > 26) {
                    addPointsToPlayer(playerOfCard, 3);
                }
                m_pile.flush();
                System.out.println("YOU GOT A POINTTTTT");
            }
            else {
                m_pile.addCard(card);
            }

            // continue
            for (Player player : m_playerQueue) {
                if (player != playerOfCard) {
                    player.cardPlayedByOpponent(card);
                }
            }
            playerOfCard.removeCard(card);
            System.out.println("\n\n\t\tPlayer POINTS:\t" + playerOfCard.capturedPoints);
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
            if (allPlayerHandsEmpty) { // deal new hand
                int playersWithWinningPoints = 0;
                for (Player p : m_playerQueue) {
                    if (p.capturedPoints >= 151) {
                        ++playersWithWinningPoints;
                    }
                }
                if (playersWithWinningPoints != 0)
                    endGame();
                if (m_deck.remainingCards() >=2) {
                    dealCards();
                } else {
                    m_pile.flush();
                    for (Player p : m_playerQueue) {
                        p.capturedCards = 0; // reset number of cards from a given deck to 0. (for majority calculation)
                    }
                    m_deck = new Deck();
                    dealCards();
                }
            }
        });
        /*
        END PATH TRANSITION SETUP
         */
        /*
        BEGIN ROTATION SETUP
         */
//        RotateTransition rotation1 = new RotateTransition();
//        RotateTransition rotation2 = new RotateTransition();
//        rotation1.setAxis(Rotate.Y_AXIS);
//        rotation2.setAxis(Rotate.Y_AXIS);
//        rotation1.setFromAngle(360);
//        rotation1.setToAngle(180);
//        rotation2.setFromAngle(180);
//        rotation2.setToAngle(0);
//        rotation1.setInterpolator(Interpolator.LINEAR);
//        rotation1.setCycleCount(10);
//        rotation2.setInterpolator(Interpolator.LINEAR);
//        rotation2.setCycleCount(10);
//        rotation1.setDuration(Duration.millis(fullDuration / 2 - fullDuration / 20));
//        rotation2.setDuration(Duration.millis(fullDuration / 2 - fullDuration / 20));
//        rotation1.setOnFinished(e -> {
//            animatedCard.setFrontVisible(true);
//            rotation2.play();
//        });

        /*
        END ROTATION SETUP
         */

        //ParallelTransition transitions = new ParallelTransition(card);//, pathTransition, rotation1);
        //transitions.play();
        pathTransition.play();
    }

    private void endGame()
    {
        getChildren().remove(m_animationLayer);
        getChildren().remove(m_layout);
        m_layout = new BorderPane();
        getChildren().add(m_layout);
        m_layout.setCenter(new Text("PLAYER WON!"));
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

    private void addPointsToPlayer(Player player, int points)
    {
        player.capturedPoints += points;
        String pointsValue = Integer.toString(player.capturedPoints);
        int index = (player == m_humanPlayer) ? 0 : 1;
        m_pointsDisplay[index].setText("Player " + Integer.toString(index) + ": " + pointsValue);
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
    HBox m_pointsDisplayPane;
    Text[] m_pointsDisplay;
}
