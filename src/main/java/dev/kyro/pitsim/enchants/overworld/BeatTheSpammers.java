package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BeatTheSpammers extends PitEnchant {

	public BeatTheSpammers() {
		super("Beat the Spammers", false, ApplyType.SWORDS,
				"bts", "beat", "beat-the-spammers", "beatthespammers");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(!attackEvent.getDefender().getEquipment().getItemInHand().getType().equals(Material.BOW)) return;
		attackEvent.increasePercent += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players holding a bow"
		).getLore();
	}

	public int getDamage(int enchantLvl) {

		return enchantLvl * 25;
	}
}
