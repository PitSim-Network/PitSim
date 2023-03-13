package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Barbaric extends PitEnchant {
	public static Barbaric INSTANCE;

	public Barbaric() {
		super("Barbaric", false, ApplyType.SCYTHES,
				"barbaric", "barb");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(!attackEvent.getAttackerPitPlayer().hasManaUnlocked() ||
				attackEvent.getAttackerPitPlayer().mana != attackEvent.getAttackerPitPlayer().getMaxMana()) return;
		attackEvent.increasePercent += getDamageIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamageIncrease(enchantLvl) + "% &7damage when your mana is full"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"increases the damage you do when your &bmana &7is full";
	}

	public static int getDamageIncrease(int enchantLvl) {
		return enchantLvl * 7 + 9;
	}
}
