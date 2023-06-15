package net.pitsim.spigot.enchants.overworld;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.controllers.Cooldown;
import net.pitsim.spigot.controllers.HitCounter;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.megastreaks.Uberstreak;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class PushComesToShove extends PitEnchant {

	public PushComesToShove() {
		super("Push comes to shove", false, ApplyType.BOWS,
				"pushcomestoshove", "push-comes-to-shove", "pcts");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		if(attackEvent.getArrow() == null) return;
		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();

		HitCounter.incrementCounter(pitAttacker.player, this);
		if(!HitCounter.hasReachedThreshold(pitAttacker.player, this, 3)) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 200);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		if(attackEvent.isDefenderPlayer()) {
			PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.getDefenderPlayer());
			if(pitDefender.getMegastreak() instanceof Uberstreak && pitDefender.isOnMega()) return;
		}

		Vector velocity = attackEvent.getArrow().getVelocity().normalize().multiply(getPunchMultiplier(enchantLvl) / 2.35);
		velocity.setY(0);

		attackEvent.getDefender().setVelocity(velocity);

		pitAttacker.stats.pcts++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Every 3rd shot on a player has &bPunch " +
				AUtil.toRoman(getPunchLevel(enchantLvl)) + " &7(5s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that applies " +
				"a huge knockback to every few bow shots you land";
	}

	public int getPunchMultiplier(int enchantLvl) {
		return (int) Math.floor(Math.pow(enchantLvl, 0.67) * 22) - 10;
	}

	public int getPunchLevel(int enchantLvl) {
		return enchantLvl * 2 + 1;
	}
}
