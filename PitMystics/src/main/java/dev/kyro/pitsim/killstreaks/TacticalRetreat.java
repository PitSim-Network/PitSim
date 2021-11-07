package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class TacticalRetreat extends Killstreak {

	public static TacticalRetreat INSTANCE;

	public TacticalRetreat() {
		super("Tactical Retreat", "TacticalRetreat", 7, 0);
		INSTANCE = this;
	}


	@Override
	public void proc(Player player) {
		Misc.applyPotionEffect(player, PotionEffectType.WEAKNESS, 20 * 5, 3, true, false);
		Misc.applyPotionEffect(player, PotionEffectType.REGENERATION, 20 * 5, 3, true, false);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.DOUBLE_PLANT, 1, 2);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &cRegeneration IV &7and", "&cWeakness IV &7for 5 seconds."));

		return builder.getItemStack();
	}
}
