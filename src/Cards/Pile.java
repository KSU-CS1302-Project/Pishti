package Cards;

import java.util.ArrayList;
import java.util.Stack;

public class Pile
{
    public Pile()
    {
        m_cards = new Stack<>();
    }

    public void addCard(Card card)
    {
        m_cards.push(card);
    }

    public Card getTopCard()
    {
        return m_cards.peek();
    }

    public ArrayList<Card> getRemainingCards()
    {
        return new ArrayList<Card>(m_cards);
    }

    public void flush()
    {
        m_cards.clear();
    }

    private Stack<Card> m_cards;
}
