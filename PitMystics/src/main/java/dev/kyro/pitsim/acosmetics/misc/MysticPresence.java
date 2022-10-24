package dev.kyro.pitsim.acosmetics.misc;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.*;
import dev.kyro.pitsim.acosmetics.collections.ParticleCollection;
import dev.kyro.pitsim.acosmetics.particles.EnchantmentTableParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MysticPresence extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public MysticPresence() {
		super("&5Mystic &dPresence", "mysticpresence", CosmeticType.MISC);
		accountForPitch = false;

		PitParticle particle = new EnchantmentTableParticle(this);

		for(int i = 0; i < 40; i++) {
			Vector vector = new Vector(1, 0, 0);
			RotationTools.rotate(vector, 9 * i, 0, 0);
			vector.add(new Vector(random(0.2), 0, random(0.2)));
			collection.addParticle(i, particle, new ParticleOffset(vector));
		}
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			private int count = 0;
			@Override
			public void run() {
				if(!CosmeticManager.isStandingStill(pitPlayer.player)) return;

				Location displayLocation = pitPlayer.player.getLocation();
				double increase = 2.2;
				displayLocation.add(0, increase, 0);

				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					collection.display(count++ % collection.particleCollectionMap.size(), entityPlayer, displayLocation);
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
		ItemStack itemStack = new AItemStackBuilder(Material.ENCHANTMENT_TABLE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Encase yourself in a protective",
						"&7spell"
				))
				.getItemStack();
		return itemStack;
	}
}
