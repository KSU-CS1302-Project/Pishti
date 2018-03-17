package Cards;

import Cards.Card;
import Cards.Rank;
import Cards.Suit;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CardView extends ImageView
{
    public CardView()
    {

    }

    public CardView(Card card)
    {
        m_card = card;
        m_texture = getImageForEnum(card.getSuit(), card.getRank());
        setImage(m_texture);
    }

    private Image getImageForEnum(Suit suit, Rank rank)
    {
        String imageFileName = getRankFileName(rank) + "_of_" +
                getSuitFileName(suit) + ".png";
        return new Image(imageFileName, 167, 242, false, false);
    }

    private String getSuitFileName(Suit suit)
    {
        switch (suit) {
            case SPADE: return "spades";
            case DIAMOND: return "diamonds";
            case CLUB: return "clubs";
            case HEART: return "hearts";
        }
        return "NA";
    }

    private String getRankFileName(Rank rank)
    {
        switch (rank) {
            case ACE: return "ace";
            case TWO: return "2";
            case THREE: return "3";
            case FOUR: return "4";
            case FIVE: return "5";
            case SIX: return "6";
            case SEVEN: return "7";
            case EIGHT: return "8";
            case NINE: return "9";
            case TEN: return "10";
            case JACK: return "jack";
            case QUEEN: return "queen";
            case KING: return "king";
        }
        return "NA";
    }

    public Card m_card;
    private Image m_texture;
}
