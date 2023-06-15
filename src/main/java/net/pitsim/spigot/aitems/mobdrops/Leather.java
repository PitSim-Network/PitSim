package net.pitsim.spigot.aitems.mobdrops;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.aitems.TemporaryItem;
import net.pitsim.spigot.brewing.PotionManager;
import net.pitsim.spigot.brewing.objects.BrewingIngredient;
import net.pitsim.spigot.brewing.objects.PotionEffect;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Leather extends BrewingIngredient implements TemporaryItem {

	public Leather() {
		super(4, "Neutrality", ChatColor.YELLOW, PotionType.REGEN);
		hasDropConfirm = true;
		marketCategory = MarketCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "wolf-hide";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("hide", "wolfhide"));
	}

	@Override
	public Material getMaterial() {
		return Material.LEATHER;
	}

	@Override
	public String getName() {
		return "&aWolf Hide";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Hide gathered from the Wolves",
				"&7of the Wolf Caves",
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

	}

	@EventHandler
	public void onHit(AttackEvent.Pre event) {
		PotionEffect defenderEffect = PotionManager.getEffect(event.getDefenderPlayer(), this);

		handleEvent(event, defenderEffect);

		PotionEffect attackerEffect = PotionManager.getEffect(event.getAttackerPlayer(), this);

		handleEvent(event, attackerEffect);
	}

	private void handleEvent(AttackEvent.Pre event, PotionEffect effect) {
		if(effect != null) {
			double attackerChance = (double) getPotency(effect.potency);
			boolean attackerIsProtected = Math.random() <= attackerChance;

			if(attackerIsProtected) {
				event.setCancelled(true);
				event.getWrapperEvent().setCancelled(true);
				Sounds.AEGIS.play(event.getDefenderPlayer().getLocation());
				event.getDefenderPlayer().getWorld().playEffect(event.getDefenderPlayer().getLocation(), Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.getData(), 100);
			}
		}
	}

	@Override
	public Object getPotency(BrewingIngredient potencyIngredient) {
		return 0.1 * potencyIngredient.tier;
	}

	@Override
	public List<String> getPotencyLore(BrewingIngredient potency) {
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.WHITE + "" + (int) ((double) getPotency(potency) * 100) + "% " + ChatColor.GRAY + "chance to " + color + "deflect " + ChatColor.GRAY + "incoming");
		lore.add(ChatColor.GRAY + "hits and " + color + "cancel " + ChatColor.GRAY + "out going hits.");
		return lore;
	}

	@Override
	public int getDuration(BrewingIngredient durationIngredient) {
		return 2 * 60 * 20 * durationIngredient.tier;
	}
}
