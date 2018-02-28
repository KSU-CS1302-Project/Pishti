import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	
	//contains deck array
    public Deck()
    {
    }

    public Deck(ArrayList<Card> cards)
    {
        m_cards = cards;
    }

    public void shuffle()
    {
        Collections.shuffle(m_cards);
    }

    public void addCard(Card card)
    {
        m_cards.add(card);
    }

    public void removeCard(Card card) {
        m_cards.remove(card);
    }

    public Card draw()
    {
        Card drawnCard =  m_cards.get(m_cards.size() - 1);
        m_cards.remove(drawnCard);
        return drawnCard;
    }

    private ArrayList<Card> m_cards;

}
