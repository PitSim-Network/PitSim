package dev.kyro.pitsim.acosmetics.aura;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.*;
import dev.kyro.pitsim.acosmetics.collections.ParticleCollection;
import dev.kyro.pitsim.acosmetics.particles.FlameParticle;
import dev.kyro.pitsim.acosmetics.particles.LavaParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FireAura extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public FireAura() {
		super("&cFire Aura", "fireaura", CosmeticType.AURA);
		accountForPitch = false;

		PitParticle fireParticle = new FlameParticle(this);
		PitParticle lavalParticle = new LavaParticle(this);
		double distance = 6;
		collection.addParticle("fire", fireParticle, new ParticleOffset(0, distance / 4, 0, distance, distance / 2 + 2, distance));
		collection.addParticle("lava", lavalParticle, new ParticleOffset(0, distance / 4, 0, distance, distance / 2 + 2, distance));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				Location displayLocation = pitPlayer.player.getLocation();

				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					PitPlayer onlinePitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(onlinePlayer != pitPlayer.player && !onlinePitPlayer.playerSettings.auraParticles) continue;

					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					if(Math.random() < 0.67) {
						collection.display("fire", entityPlayer, displayLocation);
					} else {
						collection.display("lava", entityPlayer, displayLocation);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId())) runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BLAZE_POWDER)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
