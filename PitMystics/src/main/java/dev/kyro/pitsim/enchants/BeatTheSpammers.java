package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BeatTheSpammers extends PitEnchant {

	public BeatTheSpammers() {
		super("Beat the Spammers", false, ApplyType.SWORDS,
				"bts", "spammers", "beat", "beat-the-spammers", "beatthespammers");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(!attackEvent.defender.getItemInHand().getType().equals(Material.BOW)) return;
		attackEvent.increasePercent += getDamage(enchantLvl) / 100D;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players", "&7holding a bow").getLore();
	}

	//	TODO: Beat The Spammers damage calculation

	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 10;
			case 2:
				return 25;
			case 3:
				return 40;

		}

		return 0;
	}
}
