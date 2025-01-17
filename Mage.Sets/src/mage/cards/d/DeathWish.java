package mage.cards.d;

import mage.abilities.effects.common.ExileSpellEffect;
import mage.abilities.effects.common.LoseHalfLifeEffect;
import mage.abilities.effects.common.WishEffect;
import mage.abilities.hint.common.OpenSideboardHint;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author Plopman
 */
public final class DeathWish extends CardImpl {

    public DeathWish(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{1}{B}{B}");

        // You may choose a card you own from outside the game and put it into your hand. 
        this.getSpellAbility().addEffect(new WishEffect(StaticFilters.FILTER_CARD_A, false));
        this.getSpellAbility().addHint(OpenSideboardHint.instance);

        // You lose half your life, rounded up. 
        this.getSpellAbility().addEffect(new LoseHalfLifeEffect());

        // Exile Death Wish.
        this.getSpellAbility().addEffect(new ExileSpellEffect());
    }

    private DeathWish(final DeathWish card) {
        super(card);
    }

    @Override
    public DeathWish copy() {
        return new DeathWish(this);
    }
}
