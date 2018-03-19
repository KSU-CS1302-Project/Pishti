package Players;

import Cards.Card;
import Cards.CardView;

import java.util.ArrayList;

public class AIPlayer extends Player
{
    @Override
    public void cardPlayedByOpponent(Card playedCard)
    {
        // needs logic
        Card cardToPlay = ((CardView) getCardList().get(0)).m_card;
        System.out.println("AIPlayer.playCard called");
        playCard(cardToPlay);
    }

    ArrayList<Card> m_cardSeen;
}
