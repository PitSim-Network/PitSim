package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RR extends Killstreak {

	public static RR INSTANCE;

	public RR() {
		super("R And R", "R&R", 3, 10);
		INSTANCE = this;
	}


	@Override
	public void proc(Player player) {
		Misc.applyPotionEffect(player, PotionEffectType.REGENERATION, 20 * 3, 1, true, false);
		Misc.applyPotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 20 * 3, 0, true, false);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLDEN_CARROT);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &9Resistance I &7and", "&cRegen II &7for 3s."));

		return builder.getItemStack();
	}
}
