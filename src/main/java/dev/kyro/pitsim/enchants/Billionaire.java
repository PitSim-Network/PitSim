package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.aaold.OldBossManager;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class Billionaire extends PitEnchant {

	public Billionaire() {
		super("Billionaire", true, ApplyType.MELEE,
				"bill", "billionaire");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(NonManager.getNon(attackEvent.getAttacker()) != null || HopperManager.isHopper(attackEvent.getAttacker())) {
//			Hoppers & (i think) bosses still use the old attack values so we don't have to change their stuff
			attackEvent.multipliers.add(getDamageMultiplier(enchantLvl));
			return;
		}

		int goldCost = getGoldCost(enchantLvl);
		if(NonManager.getNon(attackEvent.getDefender()) == null) {
			goldCost = getPlayerGoldCost(enchantLvl);
		}
		if(UpgradeManager.hasUpgrade(attackEvent.getAttackerPlayer(), "TAX_EVASION")) {
			goldCost = goldCost - (int) ((UpgradeManager.getTier(attackEvent.getAttackerPlayer(), "TAX_EVASION") * 0.05) * goldCost);
		}

//		if(!BossManager.bosses.containsKey(CitizensAPI.getNPCRegistry().getNPC(attackEvent.getAttacker())) && !HopperManager.isHopper(attackEvent.getAttacker())) {
			double finalBalance = attackEvent.getAttackerPitPlayer().gold - goldCost;
			if(finalBalance < 0) return;
			attackEvent.getAttackerPitPlayer().gold -= goldCost;
//		}

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(pitPlayer.stats != null) pitPlayer.stats.billionaire += goldCost;

//		attackEvent.multipliers.add(getDamageMultiplier(enchantLvl));
		attackEvent.increasePercent += getDamageIncrease(enchantLvl) / 100.0;
		Sounds.BILLIONAIRE.play(attackEvent.getAttacker());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
//		DecimalFormat decimalFormat = new DecimalFormat("0.##");
//		return new ALoreBuilder("&7Hits with this sword deal &c" + getDamageMultiplier(enchantLvl) + "x",
//				"&cdamage &7but cost &6" + getPlayerGoldCost(enchantLvl) + "g &7against", "&7players and &6" + getGoldCost(enchantLvl) + "g &7against", "&7bots").getLore();

		DecimalFormat decimalFormat = new DecimalFormat("0.##");
		return new ALoreBuilder("&7Hits with this sword deal &c+" + decimalFormat.format(getDamageIncrease(enchantLvl)) + "%",
				"&cdamage &7but cost &6" + getGoldCost(enchantLvl) / 5 + "g &7against", "&7players and &6" + getGoldCost(enchantLvl) + "g &7against", "&7bots").getLore();
	}

	public double getDamageIncrease(int enchantLvl) {
		if(enchantLvl % 3 == 0) return (int) (enchantLvl / 3) * 100;
		return (enchantLvl / 3.0) * 100;
	}

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
