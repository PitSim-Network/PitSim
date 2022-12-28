package dev.kyro.pitsim.cosmetics.misc;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.misc.kyrocosmetic.DaggerParticle;
import dev.kyro.pitsim.cosmetics.misc.kyrocosmetic.LifestealParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class KyroCosmetic extends PitCosmetic {
	public LifestealParticle lifestealParticle;
	public DaggerParticle daggerParticle;

	public KyroCosmetic() {
		super("&b&k|&9Kyro's \"Cosmetic\"&b&k|", "kyrocosmetic", CosmeticType.MISC);
		accountForPitch = false;
		accountForYaw = false;
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		lifestealParticle = new LifestealParticle(pitPlayer.player);
		daggerParticle = new DaggerParticle(pitPlayer.player);
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(lifestealParticle != null) lifestealParticle.remove();
		if(daggerParticle != null) daggerParticle.remove();
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
