package net.pitsim.pitsim.cosmetics.killeffectsbot;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.cosmetics.CosmeticType;
import net.pitsim.pitsim.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IronKill extends PitCosmetic {

	public IronKill() {
		super("&f&lIron Kill", "ironkill", CosmeticType.BOT_KILL_EFFECT);
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.ANVIL)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"",
						"&7Become a &f&lBLACKSMITH &7and",
						"&7listen to your enemies be smashed",
						"&7by anvils!"
				))
				.getItemStack();
		return itemStack;
	}
}
