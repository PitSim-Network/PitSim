package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Devour extends PitEnchant {
	public static Devour INSTANCE;

	public Devour() {
		super("Devour", true, ApplyType.CHESTPLATES,
				"devour");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int soulCost = getSoulCost(enchantLvl);

		if(PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer())) {
			double finalBalance = attackEvent.getAttackerPitPlayer().taintedSouls - soulCost;
			if(finalBalance < 0) return;
			attackEvent.getAttackerPitPlayer().taintedSouls -= soulCost;

			PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
			if(pitPlayer.stats != null) pitPlayer.stats.billionaire += soulCost;
		}

		attackEvent.increasePercent += getDamageIncrease(enchantLvl) / 100.0;
		Sounds.BILLIONAIRE.play(attackEvent.getAttacker());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Hits with this sword deal &c+" + getDamageIncrease(enchantLvl) + "% " +
				"&cdamage &7but cost &6" + getSoulCost(enchantLvl)
		).getLore();
	}

	public double getDamageIncrease(int enchantLvl) {
		if(enchantLvl % 3 == 0) return (enchantLvl / 3) * 100;
		return (enchantLvl / 3.0) * 100;
	}

	public int getSoulCost(int enchantLvl) {
		return enchantLvl;
	}
}
