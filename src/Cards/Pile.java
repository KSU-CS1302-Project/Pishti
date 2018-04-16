package Cards;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

public class Pile extends ImageView
{
    public Pile()
    {
        m_topCard = new Card();
    }

    public void addCard(Card card)
    {
        m_topCard = card;
        m_points = card.point;
        setImage(card.m_texture);
    }

    public Card getTopCard()
    {
        return m_topCard;
    }

    public ArrayList<Card> getRemainingCards()
    {
        return null;//return new ArrayList<Card>(m_cards);
    }

    //Would the comment below be easily adaptable to how we could get the points available in the pile?
    /*public int points() {
    	int point;
    	for(int i = m_cards.length() + 1 : i > 0 : i--) {
    		if(card.getRank() == JACK) {
    			point ++;
    		}
    		else if(card.getRank() == ACE) {
    			point ++;
    		}
    		else if(card.getRank() == TWO && card.getSuit() == CLUBS) {
    			point += 2;
    		}
    		else if(card.getRank() == TEN && card.getSuit() == DIAMONDS) {
    			point += 3;
    		}
    	}
    	return point;
    }*/
    
    public void flush()
    {
        m_topCard = null;
        m_numberOfCards = 0;
        m_points = 0;
    }

    private Card m_topCard;
    private int m_numberOfCards = 0;
    private int m_points = 0;
}
