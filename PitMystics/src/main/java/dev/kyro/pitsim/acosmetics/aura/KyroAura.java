package dev.kyro.pitsim.acosmetics.aura;

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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class KyroAura extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public static final double SIZE = 3.5;
	public static final double SPEED = 5;
	public static final double RANDOM = 0;

	public KyroAura() {
		super("&b&k|&9Kyro's Aura&b&k|", "kyroaura", CosmeticType.AURA);
		accountForPitch = false;
		accountForYaw = false;

		collection.addParticle("1", new EnchantmentTableParticle(this),
				new ParticleOffset(0, 0, 0, RANDOM, RANDOM, RANDOM));
		collection.addParticle("2", new EnchantmentTableParticle(this),
				new ParticleOffset(0, 0, 0, RANDOM, RANDOM, RANDOM));
		collection.addParticle("3", new EnchantmentTableParticle(this),
				new ParticleOffset(0, 0, 0, RANDOM, RANDOM, RANDOM));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			private final Vector yawVector = new Vector(0, SIZE, 0);
			private double yawX = 1;
			private double yawY = 0;
			private final Vector pitchVector = new Vector(SIZE, 0, 0);
			private double pitchX = 1;
			private double pitchY = 0;
			private final Vector rollVector = new Vector(0, 0, SIZE);
			private double rollX = 1;
			private double rollY = 0;
			@Override
			public void run() {
				yawX = (0 + (yawX - 0) * Math.cos(Math.toRadians(0.4)) - ((yawY - 0) * Math.sin(Math.toRadians(0.4))));
				yawY = (0 + (yawX - 0) * Math.sin(Math.toRadians(0.4)) + (yawY - 0) * Math.cos(Math.toRadians(0.4)));
				RotationTools.rotate(yawVector, 0, yawX * SPEED, yawY * SPEED);
				Location displayLocation1 = pitPlayer.player.getLocation().add(0, 1, 0).add(yawVector);

				pitchX = (0 + (pitchX - 0) * Math.cos(Math.toRadians(0.4)) - ((pitchY - 0) * Math.sin(Math.toRadians(0.4))));
				pitchY = (0 + (pitchX - 0) * Math.sin(Math.toRadians(0.4)) + (pitchY - 0) * Math.cos(Math.toRadians(0.4)));
				RotationTools.rotate(pitchVector, pitchX * SPEED, 0, pitchY * SPEED);
				Location displayLocation2 = pitPlayer.player.getLocation().add(0, 1, 0).add(pitchVector);

				rollX = (0 + (rollX - 0) * Math.cos(Math.toRadians(0.4)) - ((rollY - 0) * Math.sin(Math.toRadians(0.4))));
				rollY = (0 + (rollX - 0) * Math.sin(Math.toRadians(0.4)) + (rollY - 0) * Math.cos(Math.toRadians(0.4)));
				RotationTools.rotate(rollVector, rollX * SPEED, rollY * SPEED, 0);
				Location displayLocation3 = pitPlayer.player.getLocation().add(0, 1, 0).add(rollVector);

				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, pitPlayer.player.getLocation())) {
					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					collection.display("1", entityPlayer, displayLocation1);
					collection.display("2", entityPlayer, displayLocation2);
					collection.display("3", entityPlayer, displayLocation3);
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
		ItemStack itemStack = new AItemStackBuilder(Material.SKULL_ITEM, 1, 3)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7featuring some trippy",
						"&7particle math"
				))
				.getItemStack();
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		skullMeta.setOwner("KyroKrypt");
		itemStack.setItemMeta(skullMeta);
		return itemStack;
	}
}
