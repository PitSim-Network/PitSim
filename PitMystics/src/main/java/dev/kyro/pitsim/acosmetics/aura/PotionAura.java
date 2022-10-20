package dev.kyro.pitsim.acosmetics.aura;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.*;
import dev.kyro.pitsim.acosmetics.collections.ParticleCollection;
import dev.kyro.pitsim.acosmetics.particles.SpellMobParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionAura extends ColorableCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public PotionAura() {
		super("&dPotion Aura", "potionaura", CosmeticType.AURA);
		accountForYaw = false;
		accountForPitch = false;

		PitParticle particle = new SpellMobParticle(this);
		double distance = 6;
		collection.addParticle("main", particle, new ParticleOffset(0, distance / 4, 0, distance, distance / 2 + 2, distance));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				Location displayLocation = pitPlayer.player.getLocation();

				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					collection.display("main", entityPlayer, displayLocation, getColor(pitPlayer));
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
		ItemStack itemStack = new AItemStackBuilder(Material.POTION, 1, 16430)
				.setName(getDisplayName())
				.getItemStack();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
}
