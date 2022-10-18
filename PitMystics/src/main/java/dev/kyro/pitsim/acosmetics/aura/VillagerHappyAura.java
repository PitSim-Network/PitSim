package dev.kyro.pitsim.acosmetics.aura;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.*;
import dev.kyro.pitsim.acosmetics.collections.ParticleCollection;
import dev.kyro.pitsim.acosmetics.particles.VillagerHappyParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class VillagerHappyAura extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public VillagerHappyAura() {
		super("&aLively Aura", "livelyaura", CosmeticType.AURA);
		accountForPitch = false;

		PitParticle particle = new VillagerHappyParticle(this);
		double distance = 10;
		collection.addParticle("main", particle, new ParticleOffset(0, distance / 4 - 2, 0, distance, distance / 2 + 1, distance));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				Location displayLocation = pitPlayer.player.getLocation();

				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					for(int i = 0; i < 5; i++) collection.display("main", entityPlayer, displayLocation);
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
		ItemStack itemStack = new AItemStackBuilder(Material.EMERALD_ORE)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
