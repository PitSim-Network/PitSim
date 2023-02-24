package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.PacketBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PurpleThumb extends PitEnchant {
	public static PurpleThumb INSTANCE;
	public static Map<Player, List<FlowerBunch>> flowerMap = new HashMap<>();

	public static final int FLOWER_SPAWN_RADIUS = 3;
	public static final int EFFECT_RADIUS = 5;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(enchantLvl == 0) {
						if(flowerMap.containsKey(player)) {
							List<FlowerBunch> flowerBunches = flowerMap.remove(player);
							for(FlowerBunch flowerBunch : flowerBunches) flowerBunch.remove();
						}
						continue;
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(enchantLvl == 0) continue;

					List<FlowerType> flowerTypes = getFlowersInProximity(player);
					for(FlowerType flowerType : flowerTypes) {
						if(flowerType == FlowerType.POPPY) {
							Misc.applyPotionEffect(player, PotionEffectType.INCREASE_DAMAGE, 20 * 3, 0, true, false);
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public PurpleThumb() {
		super("Purple Thumb", true, ApplyType.CHESTPLATES,
				"purplethumb", "thumb");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if(player.isSneaking()) return;

		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, 10);
		if(cooldown.isOnCooldown()) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}
		cooldown.restart();

		flowerMap.putIfAbsent(player, new ArrayList<>());
		List<FlowerBunch> flowerBunches = flowerMap.get(player);
		flowerBunches.add(new FlowerBunch(player, FlowerType.random()));
	}

	public static List<FlowerType> getFlowersInProximity(Player player) {
		List<FlowerType> flowerTypes = new ArrayList<>();
		if(!flowerMap.containsKey(player)) return flowerTypes;
		List<FlowerBunch> flowerBunches = flowerMap.get(player);

		for(FlowerBunch flowerBunch : flowerBunches) {
			if(player.getWorld() != flowerBunch.centerLocation.getWorld() ||
					player.getLocation().distance(flowerBunch.centerLocation) > EFFECT_RADIUS) continue;
			if(flowerTypes.contains(flowerBunch.flowertype)) continue;
			flowerTypes.add(flowerBunch.flowertype);
		}

		return flowerTypes;
	}


	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}

	private static class FlowerBunch {
		public Player player;
		public FlowerType flowertype;
		public Location centerLocation;
		public int length = 100;

		public List<PacketBlock> flowerBlocks = new ArrayList<>();

		public FlowerBunch(Player player, FlowerType flowertype) {
			this.player = player;
			this.flowertype = flowertype;
			this.centerLocation = player.getLocation();

			spawn();

			new BukkitRunnable() {
				@Override
				public void run() {
					if(!flowerMap.containsKey(player)) return;
					flowerMap.get(player).remove(FlowerBunch.this);
				}
			}.runTaskLater(PitSim.INSTANCE, length);
		}

		public void spawn() {
			List<Block> validBlocks = new ArrayList<>();
			for(double x = -FLOWER_SPAWN_RADIUS; x <= FLOWER_SPAWN_RADIUS; x++) {
				for(double z = -FLOWER_SPAWN_RADIUS; z <= FLOWER_SPAWN_RADIUS; z++) {
					for(int y = -5; y <= 5; y++) {
						Location testLocation = centerLocation.clone().add(x, y, z);
						Block block = testLocation.getBlock();
						if(block == null || block.getType() != Material.AIR) continue;
						Block blockBelow = block.getRelative(0, -1, 0);
//						TODO: Add solid block
						if(blockBelow == null || blockBelow.getType() == Material.AIR) continue;
						validBlocks.add(block);
						break;
					}
				}
			}
			Collections.shuffle(validBlocks);
			int flowers = Math.min(new Random().nextInt(3) + 5, validBlocks.size());
			for(int i = 0; i < flowers; i++) {
				Block block = validBlocks.get(i);
				PacketBlock flowerBlock = new PacketBlock(flowertype.material, flowertype.data, block.getLocation())
						.setViewers(Misc.getNearbyRealPlayers(block.getLocation(), 50))
						.spawnBlock()
						.removeAfter(length + new Random().nextInt(21));
				flowerBlocks.add(flowerBlock);

				new BukkitRunnable() {
					@Override
					public void run() {
						if(flowerBlock.isRemoved()) {
							cancel();
							return;
						}
						drawParticles(player, flowerBlock);
					}
				}.runTaskTimer(PitSim.INSTANCE, new Random().nextInt(3), 3);
			}
		}

		public void remove() {
			for(PacketBlock flowerBlock : flowerBlocks) flowerBlock.removeBlock();
		}

		public void drawParticles(Player player, PacketBlock flowerBlock) {
			RedstoneParticle redstoneParticle = new RedstoneParticle();
			for(int i = 0; i < 3; i++) {
				Location location = flowerBlock.getLocation().clone()
						.add(Misc.randomOffset(5), Misc.randomOffsetPositive(3) + 0.5, Misc.randomOffset(5));
				redstoneParticle.display(player, location, flowertype.particleColor);
			}
		}
	}
	private enum FlowerType {
		POPPY(Material.RED_ROSE, 0, ParticleColor.RED),
		BLUE_ORCHID(Material.RED_ROSE, 1, ParticleColor.AQUA),
		ALLIUM(Material.RED_ROSE, 2, ParticleColor.DARK_PURPLE),
		AZURE_BLUET(Material.RED_ROSE, 3, ParticleColor.WHITE),
		ORANGE_TULIP(Material.RED_ROSE, 5, ParticleColor.GOLD),
		DANDELION(Material.YELLOW_FLOWER, 0, ParticleColor.YELLOW);

//		RED_TULIP(Material.RED_ROSE, 4, ParticleColor.RED),
//		WHITE_TULIP(Material.RED_ROSE, 6, ParticleColor.WHITE),
//		PINK_TULIP(Material.RED_ROSE, 7, ParticleColor.AQUA),
//		OXEYE_DAISY(Material.RED_ROSE, 8, ParticleColor.AQUA),

		public final Material material;
		public final byte data;
		public final ParticleColor particleColor;

		FlowerType(Material material, int data, ParticleColor particleColor) {
			this.material = material;
			this.data = (byte) data;
			this.particleColor = particleColor;
		}

		public static FlowerType random() {
			return values()[new Random().nextInt(values().length)];
		}
	}
}
