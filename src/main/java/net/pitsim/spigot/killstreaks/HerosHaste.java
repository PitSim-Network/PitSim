package net.pitsim.spigot.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.controllers.objects.Killstreak;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class HerosHaste extends Killstreak {

	public static HerosHaste INSTANCE;

	public HerosHaste() {
		super("Hero's Haste", "HerosHaste", 7, 12);
		INSTANCE = this;
	}

	@Override
	public void proc(Player player) {
		Misc.applyPotionEffect(player, PotionEffectType.SPEED, 20 * 8, 3, true, false);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayStack(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BOOK)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Gain &eSpeed IV &7for 8 seconds."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eHero’s Haste&7 is a killstreak that gives you &eSpeed IV&7 for a short period of time every &c7 kills";
	}
}
