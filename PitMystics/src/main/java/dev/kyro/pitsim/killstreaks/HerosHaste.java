package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class HerosHaste extends Killstreak {

	public static HerosHaste INSTANCE;

	public HerosHaste() {
		super("Hero's Hatse", "HerosHaste", 10, 0);
		INSTANCE = this;
	}


	@Override
	public void proc(Player player) {
		Misc.applyPotionEffect(player, PotionEffectType.SPEED, 20 * 12, 1, true, false);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayItem() {

		AItemStackBuilder builder = new AItemStackBuilder(Material.ENCHANTED_BOOK);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &eSpeed II &7for 12 seconds."));

		return builder.getItemStack();
	}
}
