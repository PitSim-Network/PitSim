package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;

import java.util.List;

public class ComboPerun extends PitEnchant {

	public ComboPerun() {
		super("Combo: Perun's Wrath", true, ApplyType.MELEE,
				"perun", "lightning");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, enchantLvl == 3 ? 4 : getStrikes(enchantLvl)))
			return;

		if(pitPlayer.stats != null) pitPlayer.stats.perun++;

		if(enchantLvl == 3) {
			int damage = 2;
			if(!(attackEvent.getDefender().getEquipment().getHelmet() == null) && attackEvent.getDefender().getEquipment().getHelmet().getType() == Material.DIAMOND_HELMET) {
				damage += 1;
			}
			if(!(attackEvent.getDefender().getEquipment().getChestplate() == null) && attackEvent.getDefender().getEquipment().getChestplate().getType() == Material.DIAMOND_CHESTPLATE) {
				damage += 1;
			}
			if(!(attackEvent.getDefender().getEquipment().getLeggings() == null) && attackEvent.getDefender().getEquipment().getLeggings().getType() == Material.DIAMOND_LEGGINGS) {
				damage += 1;
			}
			if(!(attackEvent.getDefender().getEquipment().getBoots() == null) && attackEvent.getDefender().getEquipment().getBoots().getType() == Material.DIAMOND_BOOTS) {
				damage += 1;
			}

			attackEvent.trueDamage += damage;
		} else {
			double damage = 2;
			if(NonManager.getNon(attackEvent.getDefender()) != null) damage += getTrueDamage(enchantLvl);

			attackEvent.trueDamage += damage;
		}

		Misc.strikeLightningForPlayers(attackEvent.getDefender().getLocation(), 10);
	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		if(event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) event.setCancelled(true);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		if(enchantLvl == 3) {
			return new ALoreBuilder("&7Every &efourth &7hit strikes", "&elightning &7for &c" + Misc.getHearts(2) + " &7+ &c" + Misc.getHearts(1),
					"&7per &bdiamond piece &7on your", "&7victim.", "&7(Lightning deals true damage)").getLore();
		}

		return new ALoreBuilder("&7Every&e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7hit strikes",
				"&elightning &7for &c" + Misc.getHearts(2) + " &7+ &c" + Misc.getHearts(getTrueDamage(enchantLvl)) + " &7if the victim",
				"&7is a non (Lightning deals true damage)").getLore();
	}

	public double getTrueDamage(int enchantLvl) {

		return enchantLvl + 2;
	}

	public int getStrikes(int enchantLvl) {

		return Math.max(Misc.linearEnchant(enchantLvl, -0.5, 5), 1);
	}
}
