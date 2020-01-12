package mage.cards.h;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.combat.CantAttackBlockAttachedEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AttachmentType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public final class HeliodsPunishment extends CardImpl {

    public HeliodsPunishment(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{W}");

        this.subtype.add(SubType.AURA);

        // Enchant creature
        TargetPermanent auraTarget = new TargetCreaturePermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.BoostCreature));
        Ability ability = new EnchantAbility(auraTarget.getTargetName());
        this.addAbility(ability);

        // Heliod's Punishment enters the battlefield with four task counters on it.
        this.addAbility(new EntersBattlefieldAbility(new AddCountersSourceEffect(CounterType.TASK.createInstance(4)), "with four task counters on it"));

        // Enchanted creature can't attack or block. It loses all abilities and has "{T}: Remove a task counter from Heliod's Punishment. Then if it has no task counters on it, destroy Heliod's Punishment."
        ability = new SimpleStaticAbility(Zone.BATTLEFIELD, new CantAttackBlockAttachedEffect(AttachmentType.AURA));
        ability.addEffect(new HeliodsPunishmentLoseAllAbilitiesEnchantedEffect());
        this.addAbility(ability);
    }

    private HeliodsPunishment(final HeliodsPunishment card) {
        super(card);
    }

    @Override
    public HeliodsPunishment copy() {
        return new HeliodsPunishment(this);
    }
}

class HeliodsPunishmentLoseAllAbilitiesEnchantedEffect extends ContinuousEffectImpl {

    public HeliodsPunishmentLoseAllAbilitiesEnchantedEffect() {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.LoseAbility);
        staticText = "It loses all abilities and has \"{T}: Remove a task counter from {this}. Then if it has no task counters on it, destroy {this}.\" ";
    }

    public HeliodsPunishmentLoseAllAbilitiesEnchantedEffect(final HeliodsPunishmentLoseAllAbilitiesEnchantedEffect effect) {
        super(effect);
    }

    @Override
    public HeliodsPunishmentLoseAllAbilitiesEnchantedEffect copy() {
        return new HeliodsPunishmentLoseAllAbilitiesEnchantedEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourceEnchantment = game.getPermanentOrLKIBattlefield(source.getSourceId());
        if (sourceEnchantment != null && sourceEnchantment.getAttachedTo() != null) {
            Permanent attachedTo = game.getPermanent(sourceEnchantment.getAttachedTo());
            if (attachedTo != null) {
                attachedTo.removeAllAbilities(source.getSourceId(), game);
                HeliodsPunishmentEffect effect = new HeliodsPunishmentEffect(sourceEnchantment.getName());
                Ability ability = new SimpleActivatedAbility(effect, new TapSourceCost());
                effect.setSourceEnchantment(sourceEnchantment);
                attachedTo.addAbility(ability, source.getSourceId(), game);
            }
        }
        return true;
    }

}

class HeliodsPunishmentEffect extends OneShotEffect {

    Permanent sourceEnchantment;

    public HeliodsPunishmentEffect(String sourceName) {
        super(Outcome.Benefit);
        this.staticText = "Remove a task counter from " + sourceName + ". Then if it has no task counters on it, destroy " + sourceName + ".";
        sourceEnchantment = null;
    }

    public HeliodsPunishmentEffect(final HeliodsPunishmentEffect effect) {
        super(effect);
        this.sourceEnchantment = effect.sourceEnchantment.copy();
    }

    @Override
    public HeliodsPunishmentEffect copy() {
        return new HeliodsPunishmentEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        if (sourceEnchantment != null) {
            if (sourceEnchantment.getCounters(game).getCount(CounterType.TASK) > 0) {
                sourceEnchantment.removeCounters(CounterType.TASK.createInstance(1), game);
                if (!game.isSimulation()) {
                    game.informPlayers("Removed a task counter from " + sourceEnchantment.getLogName());
                }
            }
            if (sourceEnchantment.getCounters(game).getCount(CounterType.TASK) == 0) {
                sourceEnchantment.destroy(source.getSourceId(), game, false);
            }
            return true;
        }
        return false;
    }

    public void setSourceEnchantment(Permanent sourceEnchantment) {
        this.sourceEnchantment = sourceEnchantment;
    }

}