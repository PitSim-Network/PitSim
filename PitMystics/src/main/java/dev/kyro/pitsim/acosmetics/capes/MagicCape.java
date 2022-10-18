package dev.kyro.pitsim.acosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.acosmetics.collections.CapeCollection;
import dev.kyro.pitsim.acosmetics.particles.MagicParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MagicCape extends PitCosmetic {
	public CapeCollection cape;

	public MagicCape() {
		super("&5Magic Cape", "magiccape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new MagicParticle(this));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(MagicCape.this, pitPlayer, null);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.ENCHANTMENT_TABLE)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
