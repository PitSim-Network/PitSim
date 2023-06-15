package net.pitsim.pitsim.cosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.cosmetics.CosmeticType;
import net.pitsim.pitsim.cosmetics.PitCosmetic;
import net.pitsim.pitsim.cosmetics.collections.CapeCollection;
import net.pitsim.pitsim.cosmetics.particles.FlameParticle;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FireCape extends PitCosmetic {
	public CapeCollection cape;

	public FireCape() {
		super("&6F&ci&6r&ce", "firecape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new FlameParticle(accountForPitch, accountForYaw));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(FireCape.this, pitPlayer, null);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BLAZE_POWDER)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Is it getting hot in here",
						"&7or is it just me?"
				))
				.getItemStack();
		return itemStack;
	}
}
