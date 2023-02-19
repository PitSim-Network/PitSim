package dev.kyro.pitsim.cosmetics.trails;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.cosmetics.*;
import dev.kyro.pitsim.cosmetics.collections.ParticleCollection;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class IronTrail extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();
	public ItemStack dropStack;

	public IronTrail() {
		super("&7Iron Trail", "irontrail", CosmeticType.PARTICLE_TRAIL);
		accountForPitch = false;

		PitParticle particle = new BlockCrackParticle(accountForPitch, accountForYaw, new MaterialData(Material.IRON_BLOCK));
		Vector vector = new Vector(0, 0.2, 0);
		collection.addParticle("main", particle, new ParticleOffset(vector, 0.5, 0, 0.5));

		dropStack = new ItemStack(Material.IRON_INGOT);
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			private int count = 0;

			@Override
			public void run() {
				if(CosmeticManager.isStandingStill(pitPlayer.player)) return;
				Location displayLocation = pitPlayer.player.getLocation();
				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					PitPlayer onlinePitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(onlinePlayer != pitPlayer.player && !onlinePitPlayer.playerSettings.trailParticles) continue;

					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					for(int i = 0; i < 2; i++)
						collection.displayAll(entityPlayer, displayLocation);
					if(count++ % 2 == 0 && Math.random() < 0.1) dropItem(dropStack, displayLocation, 0.5, 0.5, 0.5);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.IRON_INGOT)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Dig deeper! You might find",
						"&7some more!"
				))
				.getItemStack();
		return itemStack;
	}
}
