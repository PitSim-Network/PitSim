package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.TaxEvasion;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class Billionaire extends PitEnchant {
	public static Billionaire INSTANCE;

	public Billionaire() {
		super("Billionaire", true, ApplyType.MELEE,
				"bill", "billionaire");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int goldCost = getGoldCost(enchantLvl);
		if(NonManager.getNon(attackEvent.getDefender()) == null) goldCost = getPlayerGoldCost(enchantLvl);
		if(UpgradeManager.hasUpgrade(attackEvent.getAttackerPlayer(), TaxEvasion.INSTANCE))
			goldCost *= 1 - (UpgradeManager.getTier(attackEvent.getAttackerPlayer(), TaxEvasion.INSTANCE) * 0.05);

		if(PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer())) {
			double finalBalance = attackEvent.getAttackerPitPlayer().gold - goldCost;
			if(finalBalance < 0) return;
			attackEvent.getAttackerPitPlayer().gold -= goldCost;

			PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
			pitPlayer.stats.billionaire += goldCost;
		}

		attackEvent.increasePercent += getDamageIncrease(enchantLvl);
		Sounds.BILLIONAIRE.play(attackEvent.getAttacker());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.##");
		return new PitLoreBuilder(
				"&7Hits with this sword deal &c+" + decimalFormat.format(getDamageIncrease(enchantLvl)) + "% " +
				"&cdamage &7but cost &6" + getPlayerGoldCost(enchantLvl) + "g &7against players and &6" +
				getGoldCost(enchantLvl) + "g &7against bots"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that drastically " +
				"increases your damage but consumes &6gold &7on each strike";
	}

	public double getDamageIncrease(int enchantLvl) {
		if(enchantLvl % 3 == 0) return (enchantLvl / 3) * 100;
		return (enchantLvl / 3.0) * 100;
	}

	public int getGoldCost(int enchantLvl) {
		if(enchantLvl == 1) return 100;
		return enchantLvl * 450 - 600;
	}

	public int getPlayerGoldCost(int enchantLvl) {
		if(enchantLvl == 1) return 20;
		return enchantLvl * 30 - 20;
	}
}
