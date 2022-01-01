package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.pitevents.Juggernaut;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class Billionaire extends PitEnchant {

	public Billionaire() {
		super("Billionaire", true, ApplyType.SWORDS,
				"bill", "billionaire");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(NonManager.getNon(attackEvent.attacker) != null || HopperManager.isHopper(attackEvent.attacker)) {
			attackEvent.multiplier.add(getDamageMultiplier(enchantLvl));
			return;
		}

		int goldCost = getGoldCost(enchantLvl);
		if(NonManager.getNon(attackEvent.defender) == null) {
			goldCost = getPlayerGoldCost(enchantLvl);
		}
		if(UpgradeManager.hasUpgrade(attackEvent.attacker, "TAX_EVASION")) {
			goldCost = goldCost - (int) ((UpgradeManager.getTier(attackEvent.attacker, "TAX_EVASION") * 0.1) * goldCost);
		}

		double finalBalance = PitSim.VAULT.getBalance(attackEvent.attacker) - goldCost;
		if(finalBalance < 0) return;
		if(Juggernaut.juggernaut != attackEvent.attacker) PitSim.VAULT.withdrawPlayer(attackEvent.attacker, goldCost);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitPlayer.stats != null) pitPlayer.stats.billionaire += goldCost;

		attackEvent.multiplier.add(getDamageMultiplier(enchantLvl));
//		attackEvent.increasePercent += getDamageIncrease(enchantLvl) / 100.0;
		Sounds.BILLIONAIRE.play(attackEvent.attacker);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.##");
		return new ALoreBuilder("&7Hits with this sword deal &c" + getDamageMultiplier(enchantLvl) + "x",
				"&cdamage &7but cost &6" + getPlayerGoldCost(enchantLvl) + "g &7against", "&7players and &6" + getGoldCost(enchantLvl) + "g &7against", "&7bots").getLore();
	}

//	public double getDamageIncrease(int enchantLvl) {
//		if(enchantLvl % 3 == 0) return enchantLvl;
//		return (enchantLvl / 3.0) * 100;
//	}

	public double getDamageMultiplier(int enchantLvl) {
		return (double) Math.round((1 + (double) enchantLvl / 3) * 100) / 100;
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
