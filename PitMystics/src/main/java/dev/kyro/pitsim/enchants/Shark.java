package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class Shark extends PitEnchant {

	public Shark() {
		super("Shark", false, ApplyType.SWORDS,
				"shark");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		List<Entity> entityList = attackEvent.attacker.getNearbyEntities(12, 12, 12);
		List<Player> playerList = new ArrayList<>();

		for(Entity entity : entityList) {
			if(entity instanceof Player && ((Player) entity).getHealth() < 12) playerList.add((Player) entity);
		}

		attackEvent.increasePercent += (getDamage(enchantLvl) / 100D)* playerList.size();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage per other",
				"&7player below &c6\u2764 &7within 12", "&7blocks").getLore();
	}

	//	TODO: Sharp damage calculation
	
	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 2;
			case 2:
				return 4;
			case 3:
				return 7;

		}

		return 0;
	}
}
