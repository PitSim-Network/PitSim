package net.pitsim.pitsim.enchants.tainted.scythe;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.BossManager;
import net.pitsim.pitsim.adarkzone.DarkzoneManager;
import net.pitsim.pitsim.adarkzone.PitBoss;
import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.SpawnManager;
import net.pitsim.pitsim.controllers.objects.PitEnchantSpell;
import net.pitsim.pitsim.cosmetics.particles.BlockCrackParticle;
import net.pitsim.pitsim.events.SpellUseEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import net.pitsim.pitsim.misc.effects.FallingBlock;
import net.pitsim.pitsim.misc.math.Point2D;
import net.pitsim.pitsim.misc.math.Polygon2D;
import net.pitsim.pitsim.misc.math.RotationUtils;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class RollingThunder extends PitEnchantSpell {
	public static RollingThunder INSTANCE;
	public static Map<Player, List<BlockBreakData>> blockBreakDataMap = new HashMap<>();

	public static final double INITIAL_WIDTH = 0;
	public static final double EFFECT_ANGLE = 65;
	public static final int EFFECT_SEGMENTS = 5;
	public static final double EFFECT_INITIAL_SEGMENT_SIZE = 2;

	public RollingThunder() {
		super("Rolling Thunder",
				"rollingthunder", "roll", "rolling", "thunder");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(SpellUseEvent event) {
		if(!isThisSpell(event.getSpell())) return;
		Player player = event.getPlayer();

		World world = player.getWorld();
		Location mainLocation = player.getLocation();
		Vector mainDirection = mainLocation.getDirection().setY(0).normalize();
		Vector leftDirection = RotationUtils.rotate(mainDirection.clone(),
				-EFFECT_ANGLE / 2.0, 0, 0);
		Vector rightDirection = RotationUtils.rotate(mainDirection.clone(),
				EFFECT_ANGLE / 2.0, 0, 0);

		Vector offsetVector = RotationUtils.rotate(mainDirection.clone().multiply(INITIAL_WIDTH / 2.0), -90, 0, 0);
		Location leftLoc = mainLocation.clone().add(offsetVector);
		Location rightLoc = mainLocation.clone().add(offsetVector.clone().multiply(-1));

		List<Block> validBlocks = new ArrayList<>();
		double effectSize = getTotalSegmentSize() + 1;
		for(double x = -effectSize; x <= effectSize; x++) {
			for(double z = -effectSize; z <= effectSize; z++) {
				for(int y = -5; y <= 5; y++) {
					Location testLocation = mainLocation.clone().add(x, y, z);
					Block block = testLocation.getBlock();
					if(block == null || block.getType() == Material.AIR || SpawnManager.isInSpawn(testLocation)) continue;

					boolean hasSpace = true;
					for(int i = 0; i < 3; i++) {
						Block nthBlockAbove = testLocation.clone().add(0, i + 1, 0).getBlock();
						if(nthBlockAbove != null && nthBlockAbove.getType() == Material.AIR) continue;
						hasSpace = false;
						break;
					}
					if(hasSpace) validBlocks.add(block);
				}
			}
		}

		leftLoc.add(leftDirection.clone().multiply(getSegmentSize(0)));
		rightLoc.add(rightDirection.clone().multiply(getSegmentSize(0)));
		List<LivingEntity> stunnedEntities = new ArrayList<>();
		for(int i = 0; i < EFFECT_SEGMENTS; i++) {
			int segmentNum = i + 1;

			Location prevLeftLoc = leftLoc.clone();
			Location prevRightLoc = rightLoc.clone();
			leftLoc.add(leftDirection.clone().multiply(getSegmentSize(segmentNum)));
			rightLoc.add(rightDirection.clone().multiply(getSegmentSize(segmentNum)));
			Polygon2D polygon = new Polygon2D(new Point2D(prevLeftLoc.getX(), prevLeftLoc.getZ()), new Point2D(leftLoc.getX(), leftLoc.getZ()),
					new Point2D(rightLoc.getX(), rightLoc.getZ()), new Point2D(prevRightLoc.getX(), prevRightLoc.getZ()));

			for(Block block : new ArrayList<>(validBlocks)) {
				Location location = block.getLocation();
				if(!polygon.contains(new Point2D(location.getX(), location.getZ()))) continue;
				validBlocks.remove(block);

				Sound sound = Misc.getBlockBreakSound(block);
				new BukkitRunnable() {
					int count = 0;
					@Override
					public void run() {
						if(++count >= 5 + 1.5 * EFFECT_SEGMENTS) cancel();

						if(Math.random() < 0.1) damageBlock(player, block);

						if(Math.random() < 0.5) {
							BlockCrackParticle particle = new BlockCrackParticle(new MaterialData(block.getType()));
							for(Player nearbyPlayer : Misc.getNearbyRealPlayers(location, 50)) {
								EntityPlayer entityPlayer = ((CraftPlayer) nearbyPlayer).getHandle();
								particle.display(entityPlayer, location.clone().add(
										0.5 + Misc.randomOffset(5), new Random().nextInt(3) + 1, 0.5 + Misc.randomOffset(5)));
							}
						}
					}
				}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);

				new BukkitRunnable() {
					@Override
					public void run() {
						if(Math.random() < 0.25) world.playSound(location, sound, 1, 1);
						tossBlock(block, segmentNum);
					}
				}.runTaskLater(PitSim.INSTANCE, getRunnableDelay(segmentNum));
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					Location centerLoc = mainLocation.clone().add(mainDirection.clone().multiply(EFFECT_INITIAL_SEGMENT_SIZE * segmentNum));
					for(Entity entity : centerLoc.getWorld().getNearbyEntities(centerLoc, effectSize, effectSize, effectSize)) {
						if(!(entity instanceof LivingEntity) || entity == player) continue;
						LivingEntity livingEntity = (LivingEntity) entity;
						Location location = livingEntity.getLocation();
						if(SpawnManager.isInSpawn(location) || !polygon.contains(new Point2D(location.getX(), location.getZ())) ||
								Math.abs(location.getY() - mainLocation.getY()) > 5 || stunnedEntities.contains(livingEntity)) continue;
						PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
						PitBoss pitBoss = BossManager.getPitBoss(livingEntity);
						boolean isRealPlayer = PlayerManager.isRealPlayer(livingEntity);
						if(!isRealPlayer && pitBoss == null && pitMob == null) continue;
						livingEntity.setVelocity(new Vector(0, 0.4 + segmentNum * 0.03, 0));
						Misc.stunEntity(livingEntity, getStunTicks(event.getSpellLevel()));
					}
					Sounds.ANVIL_LAND.play(centerLoc);
				}
			}.runTaskLater(PitSim.INSTANCE, getRunnableDelay(segmentNum));
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				List<BlockBreakData> dataList = blockBreakDataMap.get(player);
				if(dataList == null || dataList.isEmpty()) {
					cancel();
					blockBreakDataMap.remove(player);
					return;
				}
				int toRemove = (int) (dataList.size() / 20.0) + 3;
				for(int i = 0; i < Math.min(toRemove, dataList.size()); i++) {
					BlockBreakData blockBreakData = dataList.remove(i);
					clearBlock(blockBreakData.entityID, blockBreakData.block);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 4, 2L);

		Sounds.ROLLING_THUNDER_START.play(mainLocation);
	}

	public static void damageBlock(Player player, Block block) {
		int stage = new Random().nextInt(4) + 1;
		blockBreakDataMap.putIfAbsent(player, new ArrayList<>());
		List<BlockBreakData> dataList = blockBreakDataMap.get(player);
		BlockBreakData blockBreakData = null;
		for(BlockBreakData testData : dataList) {
			if(!testData.block.getLocation().equals(block.getLocation())) continue;
			blockBreakData = testData;
			break;
		}
		if(blockBreakData == null) {
			BlockBreakData newData = new BlockBreakData(player, Misc.getNextEntityID(), block, stage);
			blockBreakData = newData;
			dataList.add(newData);
		} else {
			if(blockBreakData.breakStage == 9) return;
			blockBreakData.breakStage = Math.min(stage + blockBreakData.breakStage, 9);
		}

		PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(blockBreakData.entityID,
				new BlockPosition(block.getX(), block.getY(), block.getZ()), blockBreakData.breakStage);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
			entityPlayer.playerConnection.sendPacket(packet);
		}
	}

	public static void clearBlock(int entityID, Block block) {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
			PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(entityID,
					new BlockPosition(block.getX(), block.getY(), block.getZ()), 10);
			entityPlayer.playerConnection.sendPacket(packet);
		}
	}

	public static void tossBlock(Block block, int segmentNum) {
		Location location = block.getLocation();
		List<Player> viewers = Misc.getNearbyRealPlayers(location, 50);
		Vector vector = new Vector(0, 0.10 + segmentNum * 0.03 + Math.random() * 0.1, 0);

		new FallingBlock(block.getType(), block.getData(), location.add(0.5, 1, 0.5))
				.setViewers(viewers)
				.spawnBlock()
				.setVelocity(vector)
				.removeAfter(new Random().nextInt(15) + 10);
	}

	public static double getSegmentSize(int segmentNum) {
		return EFFECT_INITIAL_SEGMENT_SIZE + segmentNum;
	}

	public static double getTotalSegmentSize() {
		double total = 0;
		for(int i = 0; i < EFFECT_SEGMENTS; i++) total += getSegmentSize(i + 1);
		return total;
	}

	public static long getRunnableDelay(int segmentNum) {
		return (segmentNum - 1) * 6L + 20;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&6Off your feet!&7 Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, " +
						"sending a cascading earthquake through the world. Those hit by the quake are stunned " +
						"(" + getCooldownSeconds(enchantLvl) + "s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"&mwas stolen&7 may have been inspired by Breach's ultimate ability Rolling Thunder";
	}

	@Override
	public int getManaCost(int enchantLvl) {
		return Math.max(95 - enchantLvl * 15, 0);
	}

	@Override
	public int getCooldownTicks(int enchantLvl) {
		return getCooldownSeconds(enchantLvl) * 20;
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return Math.max(32 - enchantLvl * 4, 0);
	}

	public static int getStunTicks(int enchantLvl) {
		return 20 * 5;
	}

	public static class BlockBreakData {
		public Player player;
		public int entityID;
		public Block block;
		public int breakStage;

		public BlockBreakData(Player player, int entityID, Block block, int breakStage) {
			this.player = player;
			this.entityID = entityID;
			this.block = block;
			this.breakStage = breakStage;
		}
	}
}
