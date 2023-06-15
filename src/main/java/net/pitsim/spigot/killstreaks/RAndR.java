package net.pitsim.spigot.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.controllers.objects.Killstreak;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RAndR extends Killstreak {

	public static RAndR INSTANCE;

	public RAndR() {
		super("R and R", "R&R", 7, 0);
		INSTANCE = this;
	}

	@Override
	public void proc(Player player) {
		Misc.applyPotionEffect(player, PotionEffectType.REGENERATION, 20 * 5, 2, true, false);
		Misc.applyPotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 0, true, false);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayStack(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLDEN_CARROT)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Gain &9Resistance I &7and",
						"&cRegen III &7for 3s."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eR and R&7 is a killstreak that gives you &9Resistance&7 and &cRegen&7 for a short period of time every &c7 kills";
	}
}
