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

public class RainbowTrail extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public RainbowTrail() {
		super("&c&lR&6&la&e&li&a&ln&2&lb&b&lo&9&lw &1&lT&5&lr&d&la&c&li&6&ll", "rainbowtrail", CosmeticType.PARTICLE_TRAIL);
		accountForPitch = false;

		Vector vector = new Vector(0, 0.2, 0);
		int ref = 0;
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 14)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 1)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 4)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 5)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 13)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 9)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 11)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 10)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
		collection.addParticle(ref++, new BlockCrackParticle(this, new MaterialData(Material.WOOL, (byte) 2)),
				new ParticleOffset(vector, 0.5, 0, 0.5));
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
						collection.display((count++ / 10) % collection.particleCollectionMap.size(), entityPlayer, displayLocation);
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
		ItemStack itemStack = new AItemStackBuilder(Material.WOOL, 1, 6)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Legend says this trail will",
						"&7lead you to a pot of gold!"
				))
				.getItemStack();
		return itemStack;
	}
}
