package net.pitsim.spigot.cosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import net.pitsim.spigot.cosmetics.collections.CapeCollection;
import net.pitsim.spigot.cosmetics.particles.CritParticle;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CritCape extends PitCosmetic {
	public CapeCollection cape;

	public CritCape() {
		super("&4&lCritical &c&lHit!", "critcape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new CritParticle(accountForPitch, accountForYaw));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(CritCape.this, pitPlayer, null);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Wear the marks of combat proudly",
						"&7on your back"
				))
				.getItemStack();
		return itemStack;
	}
}
