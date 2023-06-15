package net.pitsim.spigot.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.objects.PitPerk;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class JewelHunter extends PitPerk {
	public static JewelHunter INSTANCE;

	public JewelHunter() {
		super("Jewel Hunter", "jewelhunter");
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!hasPerk(attackEvent.getAttacker()) || !attackEvent.isDefenderPlayer() ||
				attackEvent.getAttacker() == attackEvent.getDefender()) return;

		if(MapManager.currentMap.world == attackEvent.getDefenderPlayer().getWorld() &&
				MapManager.currentMap.getMid().distance(attackEvent.getDefenderPlayer().getLocation()) < getRange()) {
			return;
		}

		if(EnchantManager.isJewel(attackEvent.getAttackerPlayer().getEquipment().getItemInHand()) ||
				EnchantManager.isJewel(attackEvent.getAttackerPlayer().getEquipment().getLeggings())) {
			AOutput.error(attackEvent.getAttackerPlayer(), "&3&lJEWEL HUNTER!&7 Does not work when you are using jewels");
			return;
		}

		double damageIncrease = 0;

		ItemStack heldItem = attackEvent.getDefender().getEquipment().getItemInHand();
		if(EnchantManager.isJewel(heldItem)) damageIncrease += getDamageIncrease();

		ItemStack pantsItem = attackEvent.getDefender().getEquipment().getLeggings();
		if(EnchantManager.isJewel(pantsItem)) damageIncrease += getDamageIncrease();

		attackEvent.increasePercent += damageIncrease;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.GOLD_SWORD)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7Outside middle&7, deal &c+" + getDamageIncrease() + "% &7damage for each &3Jewel " +
						"&7your opponent has (holding or wearing)"
		);
	}

	@Override
	public String getSummary() {
		return "&aJewel Hunter &7is a perk makes you deal more &cdamage &7to players holding and wearing jewel items";
	}

	public int getDamageIncrease() {
		return 5;
	}

	public static int getRange() {
		return 12;
	}
}
