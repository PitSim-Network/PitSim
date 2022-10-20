package dev.kyro.pitsim.acosmetics.trails;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.*;
import dev.kyro.pitsim.acosmetics.collections.ParticleCollection;
import dev.kyro.pitsim.acosmetics.particles.BlockCrackParticle;
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

public class IceTrail extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public IceTrail() {
		super("&bIce Trail", "icetrail", CosmeticType.PARTICLE_TRAIL);
		accountForPitch = false;

		PitParticle particle = new BlockCrackParticle(this, new MaterialData(Material.PACKED_ICE));
		Vector vector = new Vector(0, 0.2, 0);
		collection.addParticle("main", particle, new ParticleOffset(vector, 0.5, 0, 0.5));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if(CosmeticManager.isStandingStill(pitPlayer.player)) return;
				Location displayLocation = pitPlayer.player.getLocation();
				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					collection.displayAll(entityPlayer, displayLocation);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.PACKED_ICE)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
