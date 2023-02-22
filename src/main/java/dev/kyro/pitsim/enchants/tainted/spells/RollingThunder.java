package dev.kyro.pitsim.enchants.tainted.spells;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DarkzoneManager;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import dev.kyro.pitsim.misc.math.Polygon2D;
import dev.kyro.pitsim.misc.math.PolygonPoint;
import dev.kyro.pitsim.misc.math.RotationUtils;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RollingThunder extends PitEnchant {
	public static RollingThunder INSTANCE;
	public static final double INITIAL_WIDTH = 0;
	public static final double EFFECT_ANGLE = 65;
	public static final int EFFECT_SEGMENTS = 5;
	public static final double EFFECT_INITIAL_SEGMENT_SIZE = 2;

	public RollingThunder() {
		super("Rolling Thunder", true, ApplyType.SCYTHES,
				"rollingthunder", "roll", "rolling", "thunder");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();

		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(player, getCooldownSeconds(enchantLvl) * 20);
		if(cooldown.isOnCooldown()) {
			Sounds.NO.play(player);
			return;
		}
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}
		cooldown.restart();

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
		for(double x = -effectSize; x < effectSize - 1; x++) {
			for(double z = -effectSize; z < effectSize - 1; z++) {
				for(int y = -5; y <= 5; y++) {
					Location testLocation = mainLocation.clone().add(x, y, z);
					Block block = testLocation.getBlock();
					if(block == null || block.getType() == Material.AIR) continue;

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
		new BukkitRunnable() {
			int segmentNum = 1;
			@Override
			public void run() {
				if(segmentNum++ == EFFECT_SEGMENTS) cancel();

				Location prevLeftLoc = leftLoc.clone();
				Location prevRightLoc = rightLoc.clone();
				leftLoc.add(leftDirection.clone().multiply(getSegmentSize(segmentNum)));
				rightLoc.add(rightDirection.clone().multiply(getSegmentSize(segmentNum)));
				Polygon2D polygon = new Polygon2D(new PolygonPoint(prevLeftLoc.getX(), prevLeftLoc.getZ()), new PolygonPoint(leftLoc.getX(), leftLoc.getZ()),
						new PolygonPoint(rightLoc.getX(), rightLoc.getZ()), new PolygonPoint(prevRightLoc.getX(), prevRightLoc.getZ()));

				for(Block block : new ArrayList<>(validBlocks)) {
					Location location = block.getLocation();
					if(!polygon.contains(new PolygonPoint(location.getX(), location.getZ()))) continue;
					validBlocks.remove(block);
					tossBlock(block, segmentNum);
					Sound sound = Misc.getBlockBreakSound(block);
					if(Math.random() < 0.5) {
						world.playSound(location, sound, 1, 1);
						BlockCrackParticle particle = new BlockCrackParticle(new MaterialData(block.getType()));
						for(Player nearbyPlayer : Misc.getNearbyRealPlayers(location, 50)) {
							EntityPlayer entityPlayer = ((CraftPlayer) nearbyPlayer).getHandle();
							particle.display(entityPlayer, location.clone().add(0.5, new Random().nextInt(3) + 1, 0.5));
						}
					}
				}
				Location centerLoc = mainLocation.clone().add(mainDirection.clone().multiply(EFFECT_INITIAL_SEGMENT_SIZE * segmentNum));
				for(Entity entity : centerLoc.getWorld().getNearbyEntities(centerLoc, effectSize, effectSize, effectSize)) {
					if(!(entity instanceof LivingEntity) || entity == player) continue;
					LivingEntity livingEntity = (LivingEntity) entity;
					Location location = livingEntity.getLocation();
					if(!polygon.contains(new PolygonPoint(location.getX(), location.getZ())) ||
							Math.abs(location.getY() - mainLocation.getY()) > 5 || stunnedEntities.contains(livingEntity)) continue;
					PitMob pitMob = DarkzoneManager.getPitMob(livingEntity);
					PitBoss pitBoss = BossManager.getPitBoss(livingEntity);
					boolean isRealPlayer = PlayerManager.isRealPlayer(livingEntity);
					if(!isRealPlayer && pitBoss == null && pitMob == null) continue;
					livingEntity.setVelocity(new Vector(0, 0.4 + segmentNum * 0.03, 0));
					Misc.stunEntity(livingEntity, 100);
				}
				Sounds.ANVIL_LAND.play(centerLoc);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 8);
	}

	public static void tossBlock(Block block, int segmentNum) {
		Location location = block.getLocation();
		List<Player> viewers = Misc.getNearbyRealPlayers(location, 50);
		Vector vector = new Vector(0, 0.10 + segmentNum * 0.03 + Math.random() * 0.1, 0);

		new FallingBlock(block.getType(), block.getData(), location.add(0.5, 1, 0.5))
				.setViewers(viewers)
				.spawnBlock()
				.setVelocity(vector)
				.removeAfter(new Random().nextInt(20) + 10);
	}

	public static double getSegmentSize(int segmentNum) {
		return EFFECT_INITIAL_SEGMENT_SIZE + segmentNum;
	}

	public static double getTotalSegmentSize() {
		double total = 0;
		for(int i = 0; i < EFFECT_SEGMENTS; i++) total += getSegmentSize(i + 1);
		return total;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&6Off your feet! &7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, " +
						"sending a cascading earthquake through the world. Anyone hit by the quake is stunned " +
						"(" + getCooldownSeconds(enchantLvl) + " second" + (getCooldownSeconds(enchantLvl) == 1 ? "" : "s") + ")"
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 1;
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return 20;
	}
}
