package net.pitsim.pitsim.cosmetics.aura;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.cosmetics.*;
import net.pitsim.pitsim.cosmetics.collections.ParticleCollection;
import net.pitsim.pitsim.cosmetics.particles.SpellMobParticle;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionAura extends ColorableCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public PotionAura() {
		super("&5Potion Aura", "potionaura", CosmeticType.AURA);
		accountForYaw = false;
		accountForPitch = false;

		PitParticle particle = new SpellMobParticle(accountForPitch, accountForYaw);
		double distance = 6;
		collection.addParticle("main", particle, new ParticleOffset(0, distance / 4, 0, distance, distance / 2 + 2, distance));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if(Math.random() < 0.2) return;
				Location displayLocation = pitPlayer.player.getLocation();

				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					PitPlayer onlinePitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(onlinePlayer != pitPlayer.player && !onlinePitPlayer.playerSettings.auraParticles) continue;

					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					collection.display("main", entityPlayer, displayLocation, getColor(pitPlayer));
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
		ItemStack itemStack = new AItemStackBuilder(Material.BREWING_STAND_ITEM)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Collect all the colors!"
				))
				.getItemStack();
		return itemStack;
	}
}
