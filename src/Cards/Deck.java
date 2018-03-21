package Cards;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Collections;

public class Deck extends ImageView {
	
	//contains deck array .
    public Deck()
    {
        buildDeck();
        Image texture = new Image("back.png", 167, 242, false, false); //texture stand-in
        setImage(texture);
    }

    public Deck(ArrayList<Card> cards)
    {
        m_cards = cards;
    }

    private void buildDeck()
    {
        m_cards = new ArrayList<Card>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(suit, rank);
                m_cards.add(card);
            }
        }
        shuffle();
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

    public int remainingCards()
    {
        return m_cards.size();
    }

    public Card draw()
    {
        Card drawnCard =  m_cards.get(m_cards.size() - 1);
        m_cards.remove(drawnCard);
        return drawnCard;
    }

    private ArrayList<Card> m_cards;
}
