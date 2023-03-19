package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JewelHunter extends PitPerk {

	public static JewelHunter INSTANCE;

	public JewelHunter() {
		super("Jewel Hunter", "jewelhunter", new ItemStack(Material.GOLD_SWORD), 21, false, "", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer() || !attackEvent.isDefenderPlayer()) return;
		if(!playerHasUpgrade(attackEvent.getAttacker())) return;
		if(MapManager.inDarkzone(attackEvent.getAttacker())) return;
		if(attackEvent.getAttacker() == attackEvent.getDefender()) return;

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
	public List<String> getDescription() {
		return new ALoreBuilder("&eOutside middle&7, Deal &c+" + getDamageIncrease() + "% &7damage for",
				"&7each &3jewel &7your opponent has", "&7(holding or wearing)").getLore();
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
