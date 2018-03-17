package Cards;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Card
{
    public Card()
    {
    }

    public Card(Suit suit, Rank rank)
    {
        m_suit = suit;
        m_rank = rank;
    }

    public Suit getSuit() {return m_suit;}
    public Rank getRank() {return m_rank;}

    private Suit m_suit;
    private Rank m_rank;
}
