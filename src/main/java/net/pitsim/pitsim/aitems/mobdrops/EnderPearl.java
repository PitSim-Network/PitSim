package net.pitsim.pitsim.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.aitems.TemporaryItem;
import net.pitsim.pitsim.brewing.PotionManager;
import net.pitsim.pitsim.brewing.objects.BrewingIngredient;
import net.pitsim.pitsim.brewing.objects.PotionEffect;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.enums.MarketCategory;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnderPearl extends BrewingIngredient implements TemporaryItem {

	public EnderPearl() {
		super(10, "Venom", ChatColor.GREEN, PotionType.POISON);
		hasDropConfirm = true;
		marketCategory = MarketCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "ender-pearl";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pearl", "enderpearl"));
	}

	@Override
	public Material getMaterial() {
		return Material.ENDER_PEARL;
	}

	@Override
	public String getName() {
		return "&aEnder Pearl";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Pearl gathered from the endermen",
				"&7of the End Caves",
				"",
				"&cLost on death"
		).getLore();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}

	@Override
	public TemporaryItem.TemporaryType getTemporaryType() {
		return TemporaryType.LOST_ON_DEATH;
	}

	@Override
	public void administerEffect(Player player, BrewingIngredient potency, int duration) {
		Misc.applyPotionEffect(player, PotionEffectType.POISON, duration, 0, false, false);
	}

	@EventHandler
	public void onHit(AttackEvent.Pre attackEvent) {
		PotionEffect attackerEffect = PotionManager.getEffect(attackEvent.getAttackerPlayer(), this);
		if(attackerEffect == null) return;

		int tokensToRemove = (int) getPotency(attackerEffect.potency);

		for (Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
			if(entry.getKey().applyType != ApplyType.SWORDS && entry.getKey().applyType != ApplyType.BOWS && entry.getKey().applyType != ApplyType.MELEE) continue;
			for (int i = 0; i < entry.getValue(); i++) {
				attackEvent.getAttackerEnchantMap().put(entry.getKey(), entry.getValue() - 1);

				tokensToRemove--;
				if(tokensToRemove == 0) return;
			}
		}
	}

	@EventHandler
	public void onDefend(AttackEvent.Apply defendEvent) {
		PotionEffect defenderEffect = PotionManager.getEffect(defendEvent.getDefenderPlayer(), this);
		if(defenderEffect == null) return;

		int tokensToRemove = (int) getPotency(defenderEffect.potency) / 2;

		for (Map.Entry<PitEnchant, Integer> entry : defendEvent.getDefenderEnchantMap().entrySet()) {
			if(entry.getKey().applyType != ApplyType.PANTS) continue;
			for (int i = 0; i < entry.getValue(); i++) {
				defendEvent.getDefenderEnchantMap().put(entry.getKey(), entry.getValue() - 1);

				tokensToRemove--;
				if(tokensToRemove == 0) return;
			}

		}
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return potencyIngredient.tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();

		lore.add("");
		lore.add(ChatColor.GRAY + "Disables " + color + getPotency(potency) + " Mystic Tokens " +  ChatColor.GRAY + "from being");
		lore.add(ChatColor.GRAY + "used in any way.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 20 * 15 * durationIngredient.tier;
	}
}
