package Cards;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Stack;

public class Pile extends ImageView
{
    public Pile()
    {
        m_cards = new Stack<>();
    }

    public void addCard(Card card)
    {
        m_cards.push(card);
        setImage(card.m_texture);
    }

    public Card getTopCard()
    {
        return m_cards.peek();
    }

    public ArrayList<Card> getRemainingCards()
    {
        return new ArrayList<Card>(m_cards);
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
        m_cards.clear();
    }

    private Stack<Card> m_cards;
}
