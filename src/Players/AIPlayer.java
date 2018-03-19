package Players;

import Cards.Card;

import java.util.ArrayList;

public class AIPlayer extends Player
{
    @Override
    public void cardPlayedByOpponent(Card playedCard)
    {
        // needs logic
        Card cardToPlay = ((Card) getCardList().get(0));
        System.out.println("AIPlayer.playCard called");
        playCard(cardToPlay);
    }

    ArrayList<Card> m_cardSeen;
}
