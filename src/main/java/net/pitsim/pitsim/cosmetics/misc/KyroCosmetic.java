package net.pitsim.pitsim.cosmetics.misc;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.cosmetics.CosmeticType;
import net.pitsim.pitsim.cosmetics.PitCosmetic;
import net.pitsim.pitsim.cosmetics.misc.kyrocosmetic.SwarmParticle;
import net.pitsim.pitsim.cosmetics.misc.kyrocosmetic.LeechParticle;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class KyroCosmetic extends PitCosmetic {
	public LeechParticle leechParticle;
	public SwarmParticle swarmParticle;

	public KyroCosmetic() {
		super("&b&k|&9Kyro's \"Cosmetic\"&b&k|", "kyrocosmetic", CosmeticType.MISC);
		accountForPitch = false;
		accountForYaw = false;
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		leechParticle = new LeechParticle(pitPlayer.player);
		swarmParticle = new SwarmParticle(pitPlayer.player);
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(leechParticle != null) leechParticle.remove();
		if(swarmParticle != null) swarmParticle.remove();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.SKULL_ITEM, 1, 3)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7totally a cosmetic"
				))
				.getItemStack();
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		skullMeta.setOwner("KyroKrypt");
		itemStack.setItemMeta(skullMeta);
		return itemStack;
	}
}
