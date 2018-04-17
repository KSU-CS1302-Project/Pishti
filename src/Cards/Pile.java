package Cards;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

public class Pile extends ImageView
{
    public Pile()
    {
        m_topCard = new Card();
        setImage(new Image("back.png", 167, 242, false, false));
    }

    public void addCard(Card card)
    {
        m_topCard = card;
        m_points += card.getPoint();//card.point;
        setImage(card.m_texture);
        m_numberOfCards += 1;
    }

    public Card getTopCard()
    {
        return m_topCard;
    }

    public ArrayList<Card> getRemainingCards()
    {
        return null;//return new ArrayList<Card>(m_cards);
    }
    
    public void flush()
    {
        m_topCard = null;
        m_numberOfCards = 0;
        m_points = 0;
        setImage(new Image("back.png", 167, 242, false, false));
    }
    
    public int getPileValue() {
    	return m_points;
    }
    
    public int getNumCards() {
    	return m_numberOfCards;
    }

    private Card m_topCard;
    private int m_numberOfCards = 0;
    private int m_points = 0;
}
