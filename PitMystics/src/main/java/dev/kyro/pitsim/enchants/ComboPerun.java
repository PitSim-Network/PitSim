package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.controllers.PitPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ComboPerun extends PitEnchant {

	public ComboPerun() {
		super("Combo: Perun's Wrath", true, ApplyType.SWORDS,
				"perun", "lightning");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, enchantLvl == 3 ? 4 : getStrikes(enchantLvl))) return;

		if(enchantLvl == 3) {
			int diamondPieces = 0;
			if(!(attackEvent.defender.getInventory().getHelmet() == null) && attackEvent.defender.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET) {
				diamondPieces = diamondPieces + 2;
			}
			if(!(attackEvent.defender.getInventory().getChestplate() == null) && attackEvent.defender.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE) {
				diamondPieces = diamondPieces + 2;
			}
			if(!(attackEvent.defender.getInventory().getLeggings() == null) && attackEvent.defender.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS) {
				diamondPieces = diamondPieces + 2;
			}
			if(!(attackEvent.defender.getInventory().getBoots() == null) && attackEvent.defender.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS) {
				diamondPieces = diamondPieces + 2;
			}

			attackEvent.trueDamage += diamondPieces;
		} else {
			attackEvent.trueDamage += getTrueDamage(enchantLvl);
		}
		attackEvent.defender.getWorld().strikeLightningEffect(attackEvent.defender.getLocation());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 3) {

			return new ALoreBuilder("&7Every &efourth &7hit strikes", "&elightning &7for &c1\u2764 &7+ &c1\u2764",
					"&7per &bdiamond piece &7on your", "&7victim.", "&7&oLightning deals true damage").getLore();
		}

		return new ALoreBuilder("&7Every&e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7hit strikes",
				"&elightning for &c" + Misc.getHearts(getTrueDamage(enchantLvl)) + "&7.", "&7&oLightning deals true damage").getLore();
	}

	public double getTrueDamage(int enchantLvl) {

		return enchantLvl + 2;
	}

	public int getStrikes(int enchantLvl) {

		return Math.max(6 - enchantLvl, 1);
	}
}
