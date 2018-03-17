package Players;

import Cards.Card;

import java.util.ArrayList;

public class AIPlayer extends Player
{
    @Override
    public void cardPlayedByOpponentEvent(Card card)
    {
    }

    ArrayList<Card> m_cardSeen;
}
