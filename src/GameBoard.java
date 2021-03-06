import ActionHandlers.Action;
import ActionHandlers.ActionObserver;
import Cards.Card;
import Cards.Deck;
import Cards.Pile;
import Cards.Rank;
import Players.AIPlayer;
import Players.HumanPlayer;
import Players.Player;
import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.shape.Rectangle;
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
        m_pointsDisplay[0] = new Text("You: " + "0");
        m_pointsDisplay[1] = new Text("Computer: " + "0");
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
        	//StackPane tempStack = new StackPane();
            //tempStack.getChildren().add((Player)(m_playerQueue.toArray())[1]);
            //tempRect.setWidth(m_layout.getWidth());tempRect.setHeight(m_layout.getHeight());tempRect.setFill(Color.BLACK);
            //tempStack.getChildren().add(tempRect);
        	//m_layout.setTop(tempStack);
            m_layout.setTop((Player)(m_playerQueue.toArray())[1]);
            m_layout.setLeft(m_deck);
            m_layout.setRight(m_pile);
        }

        // setup ActionNotifications between players and GameBoard
        for (Player player : m_playerQueue) {
            player.m_subject.addObserver(this);
        }

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                prime();
                dealCards();
            });
        });
        thread.start();
    }
    
    private void prime() {
        SequentialTransition sequence4Cards = new SequentialTransition();
        sequence4Cards.getChildren().addAll();
    	for (int i = 0; i < 4; i++) {
    		Card card = m_deck.draw();
    		card.setFrontVisible(false);
    		sequence4Cards.getChildren().add(moveCard(card, m_deck, m_pile, () -> {m_pile.addCard(card);}, true));
    	}
    	sequence4Cards.play();
    }

    // gives each player an ArrayList containing 4 cards.
    private void dealCards()
    {
        int remainingCards = m_deck.remainingCards();
        //SequentialTransition sequence = new SequentialTransition();
        for (Player player : m_playerQueue) {
            ArrayList<Card> playerHand = new ArrayList<>();
            for (int i = 0; i < Math.min(4, remainingCards / 2); ++i) {
                Card card = m_deck.draw();
                playerHand.add(card);
                //ParallelTransition transition = moveCard(card, m_deck, player, () -> {}, true);
                //sequence.getChildren().add(transition);
            }
            //sequence.play();
            player.dealHand(playerHand);
        }
    }

    private ParallelTransition moveCard(Card card, Node source, Node destination, Runnable callback, boolean doFlip)
    {
        // initialize transitions
        ParallelTransition rotateAndMove = new ParallelTransition();
        PathTransition pathTransition = new PathTransition();
        SequentialTransition sequence = new SequentialTransition();
          ParallelTransition rotationStep1 = new ParallelTransition();
            RotateTransition rotation1X = new RotateTransition();
            RotateTransition rotation1Y = new RotateTransition();
          ParallelTransition rotationStep2 = new ParallelTransition();
            RotateTransition rotation2X = new RotateTransition();
            RotateTransition rotation2Y = new RotateTransition();

        // add transitions into containers
        if (doFlip) {
            rotateAndMove.getChildren().addAll(pathTransition, sequence);
            sequence.getChildren().addAll(rotationStep1, rotationStep2);
            rotationStep1.getChildren().addAll(rotation1X, rotation1Y);
            rotationStep2.getChildren().addAll(rotation2X, rotation2Y);
        } else {
            rotateAndMove.getChildren().addAll(pathTransition);
        }

        Line path = new Line();
        Card animatedCard = new Card(card);
        Bounds sourceBounds = (source == null ? getBoundsInAnimationLayer(card) : getBoundsInAnimationLayer(source));
        Bounds pileBounds = getBoundsInAnimationLayer(destination);
        double xOriginToCenter = animatedCard.getBoundsInLocal().getWidth() / 2;
        double yOriginToCenter = animatedCard.getBoundsInLocal().getHeight() / 2;

        // setup path transition
        m_animationLayer.getChildren().addAll(path, animatedCard);
        path.setStroke(Color.TRANSPARENT);
        path.setStartX(sourceBounds.getMinX() + xOriginToCenter);
        path.setStartY(sourceBounds.getMinY() + yOriginToCenter);
        path.setEndX(pileBounds.getMinX() - xOriginToCenter + m_pile.getBoundsInLocal().getWidth());
        path.setEndY(pileBounds.getMinY() + yOriginToCenter);
        pathTransition.setDuration(Duration.millis(1000));
        pathTransition.setNode(animatedCard);
        pathTransition.setPath(path);

        //
        rotation1X.setNode(animatedCard);
        rotation1X.setAxis(Rotate.X_AXIS);
        rotation1X.setFromAngle(360);
        rotation1X.setToAngle(270);
        rotation1X.setInterpolator(Interpolator.LINEAR);
        rotation1X.setCycleCount(1);

        rotation1Y.setNode(animatedCard);
        rotation1Y.setAxis(Rotate.X_AXIS);
        rotation1Y.setFromAngle(360);
        rotation1Y.setToAngle(270);
        rotation1Y.setInterpolator(Interpolator.LINEAR);
        rotation1Y.setCycleCount(1);

        rotation2X.setNode(animatedCard);
        rotation2X.setAxis(Rotate.X_AXIS);
        rotation2X.setFromAngle(90);
        rotation2X.setToAngle(0);
        rotation2X.setInterpolator(Interpolator.LINEAR);
        rotation2X.setCycleCount(1);

        rotation2Y.setNode(animatedCard);
        rotation2Y.setAxis(Rotate.X_AXIS);
        rotation2Y.setFromAngle(90);
        rotation2Y.setToAngle(0);
        rotation2Y.setInterpolator(Interpolator.LINEAR);
        rotation2Y.setCycleCount(1);

        rotationStep1.setOnFinished(e -> {
            animatedCard.setFrontVisible(true);
        });


        rotateAndMove.setOnFinished(e -> {
            callback.run();
            animatedCard.setVisible(false);
        });

        card.setVisible(false);
        return rotateAndMove;
    }

    // card was played.  remove it from the hand of the player that played it, and notify other players.
    private void cardPlayed(Player playerOfCard, Card card)
    {
        moveCard(card, null, m_pile, () -> {
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
                    prime();
                }
            }
        }, playerOfCard instanceof AIPlayer).play();
    }

    private void endGame()
    {
        getChildren().remove(m_animationLayer);
        getChildren().remove(m_layout);
        m_layout = new BorderPane();
        getChildren().add(m_layout);
        int AIPoints = 0;
        int humanPoints = 0;
        for (Player p : m_playerQueue) {
            if (p instanceof HumanPlayer)
                humanPoints = p.capturedPoints;
            else
                AIPoints = p.capturedPoints;
        }
        if(humanPoints > AIPoints) {
    		m_layout.setCenter(new Text("Human Player Won!!\rCongratulations!"));
    	}
        else if (AIPoints > humanPoints){
        	m_layout.setCenter(new Text("Computer Player Won!!\rYou Lost!"));
        }
        else {
            m_layout.setCenter(new Text("THE GAME DEVS SCREWED UP.  SO SUE THEM."));
        }
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
        String label = (index == 0) ? "You" : "Computer";
        m_pointsDisplay[index].setText(label + ": " + pointsValue);
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

    //private Rectangle tempRect;
    private HumanPlayer m_humanPlayer;
    private Queue<Player> m_playerQueue; // holds players - In queue to track and enforce turn order.
    private Deck m_deck;
    private Pile m_pile;
    BorderPane m_layout;
    Pane m_animationLayer;
    HBox m_pointsDisplayPane;
    Text[] m_pointsDisplay;
}
