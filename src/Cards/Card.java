package Cards;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Card extends ImageView
{
    public Card()
    {
    }

    public Card(Suit suit, Rank rank, boolean visible)
    {
        this(suit, rank);
        setFrontVisible(visible);
    }

    public Card(Suit suit, Rank rank)
    {
        m_suit = suit;
        m_rank = rank;
        m_texture = getImageForEnum(suit, rank);
        setImage(m_texture);
        if(suit.equals(Suit.CLUB) && rank.equals(Rank.TWO)) {
        	m_point = 2;
        }
        else if(suit.equals(Suit.DIAMOND) && rank.equals(Rank.TEN)) {
        	m_point = 3;
        }
        else if(rank.equals(Rank.JACK)) {
        	m_point = 1;
        }
        else if(rank.equals(Rank.ACE)) {
        	m_point = 1;
        }
        else {
        	m_point = 0;
        }
    }

    public Card(Card copiedCard)
    {
        m_suit = copiedCard.getSuit();
        m_rank = copiedCard.getRank();
        m_texture = getImageForEnum(m_suit, m_rank);
        m_frontVisible = copiedCard.m_frontVisible;
        if (m_frontVisible)
            setImage(m_texture);
        else
            setImage(new Image("back.png", 167, 242, false, false));
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

    public void setFrontVisible(boolean visible)
    {
        if (!visible) {
            setImage(new Image("back.png", 167, 242, false, false));
        } else {
            setImage(getImageForEnum(m_suit, m_rank));
        }
        m_frontVisible = visible;
    }

    public boolean getFrontVisible()
    {
        return m_frontVisible;
    }

    public boolean equals(Card card)
    {
        return (card.m_rank == this.m_rank);
    }

    public Suit getSuit() {return m_suit;}
    public Rank getRank() {return m_rank;}
    public int getPoint() {return m_point;}
   

    private Suit m_suit;
    private Rank m_rank;
    public Image m_texture;
    private boolean m_frontVisible = true;
    public int m_point;
}
