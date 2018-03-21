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

    public void flush()
    {
        m_cards.clear();
    }

    private Stack<Card> m_cards;
}
