package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.altar.pedestals.HeresyPedestal;
import dev.kyro.pitsim.adarkzone.altar.pedestals.KnowledgePedestal;
import dev.kyro.pitsim.adarkzone.altar.pedestals.RenownPedestal;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.AltarBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class AltarPedestal implements Listener {
	public static List<AltarPedestal> altarPedestals = new ArrayList<>();
	public Location location;
	public ArmorStand stand;

	public List<UUID> activatedPlayers = new ArrayList<>();

	public AltarPedestal(Location location) {
		altarPedestals.add(this);
		Bukkit.getServer().getPluginManager().registerEvents(this, PitSim.INSTANCE);

		this.location = location;
		spawnStand();
	}

	public abstract String getDisplayName();
	public abstract int getActivationCost();
	public abstract ItemStack getItem(Player player);
	public abstract ParticleColor getParticleColor();

	public boolean isUnlocked(Player player) {

		if(getIndex() < 3 && ProgressionManager.isUnlocked(PitPlayer.getPitPlayer(player),
				ProgressionManager.getSkillBranch(AltarBranch.class), SkillBranch.MajorUnlockPosition.FIRST)) {
			return true;
		}

		if(getIndex() == 3 && ProgressionManager.isUnlocked(PitPlayer.getPitPlayer(player),
				ProgressionManager.getSkillBranch(AltarBranch.class), SkillBranch.MajorUnlockPosition.FIRST_PATH)) {
			return true;
		}

		return getIndex() == 4 && ProgressionManager.isUnlocked(PitPlayer.getPitPlayer(player),
				ProgressionManager.getSkillBranch(AltarBranch.class), SkillBranch.MajorUnlockPosition.SECOND_PATH);

	}

	public boolean isActivated(Player player) {
		return activatedPlayers.contains(player.getUniqueId());
	}

	public void spawnStand() {
		if(stand != null) {
			stand.remove();
		}

		location.getChunk().load();

		stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setCustomNameVisible(true);
		stand.setArms(true);
		stand.setCustomName(ChatColor.translateAlternateColorCodes('&', getDisplayName()));
		stand.setGravity(false);

		stand.getEquipment().setHelmet(new ItemStack(Material.SMOOTH_BRICK, 1, (short) 3));
	}

	public String getStatus(Player player) {
		if(!isUnlocked(player)) return "&c&lLOCKED";
		else if(isActivated(player)) return "&a&lACTIVE";
		else return "&e&lINACTIVE";
	}

	public void activate(Player player) {
		if(PitPlayer.getPitPlayer(player).darkzoneTutorial.isActive()) return;

		if(!isUnlocked(player)) {
			DataWatcher dw = ((CraftEntity)stand).getHandle().getDataWatcher();
			dw.watch(2, (Object)ChatColor.translateAlternateColorCodes('&', "&c&lLOCKED!"));
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(AltarManager.getStandID(stand), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);

			new BukkitRunnable() {
				@Override
				public void run() {
					DataWatcher dw = ((CraftEntity)stand).getHandle().getDataWatcher();
					dw.watch(2, (Object)ChatColor.translateAlternateColorCodes('&', getDisplayName()));
					PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(AltarManager.getStandID(stand), dw, false);
					((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
				}
			}.runTaskLater(PitSim.INSTANCE, 20);

			Sounds.ERROR.play(player);
			return;
		}

		activatedPlayers.add(player.getUniqueId());

		PacketPlayOutEntityEquipment identityEquipmentPacket = new PacketPlayOutEntityEquipment(AltarManager.getStandID(stand), 4, CraftItemStack.asNMSCopy(new ItemStack(Material.SEA_LANTERN)));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityEquipmentPacket);

		Sounds.PEDESTAL_ACTIVATE.play(player);
	}

	public void deactivate(Player player, boolean silent) {
		if(!isActivated(player)) return;

		PacketPlayOutEntityEquipment identityEquipmentPacket = new PacketPlayOutEntityEquipment(AltarManager.getStandID(stand), 4, CraftItemStack.asNMSCopy(new ItemStack(Material.SMOOTH_BRICK, 1, (short) 3)));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityEquipmentPacket);
		activatedPlayers.remove(player.getUniqueId());
		if(!silent) Sounds.PEDESTAL_DEACTIVATE.play(player);
	}

	@EventHandler
	public void onClick(PlayerInteractAtEntityEvent event) {
		if(!event.getRightClicked().getUniqueId().equals(stand.getUniqueId())) return;
		Player player = event.getPlayer();
		if(AltarManager.isInAnimation(player)) return;

		if(isActivated(player)) deactivate(player, false);
		else activate(player);
	}

	@EventHandler
	public void onChunkLoad(ChunkUnloadEvent event) {
		if(event.getChunk().equals(stand.getLocation().getChunk())) {
			event.setCancelled(true);
		}
	}

	public int getIndex() {
		return altarPedestals.indexOf(this);
	}

	@SuppressWarnings("unchecked")
	public static <T extends AltarPedestal> T getPedestal(Class<T> clazz) {
		for(AltarPedestal pedestal : altarPedestals) if(pedestal.getClass() == clazz) return (T) pedestal;
		throw new RuntimeException();
	}

	public static void disableAll(Player player) {
		for(AltarPedestal pedestal : altarPedestals) {
			pedestal.deactivate(player, true);
		}
	}

	public static void cleanUp() {
		for(AltarPedestal pedestal : altarPedestals) {
			pedestal.stand.remove();
		}
	}

	public static int getRewardChance(Player player, AltarReward reward) {
		return reward.pedestal.isActivated(player) ? DarkzoneBalancing.PEDESTAL_INCREASE_PERCENT : 0;
	}

	public static int getTotalCost(Player player) {
		int totalCost = DarkzoneBalancing.BASE_ALTAR_COST;
		for(AltarPedestal pedestal : altarPedestals) if(pedestal.isActivated(player)) totalCost += pedestal.getActivationCost();
		return totalCost;
	}

	public enum AltarReward {
		ALTAR_XP(getPedestal(KnowledgePedestal.class), DarkzoneBalancing.AVERAGE_XP_PER_100_SOULS),
		RENOWN(getPedestal(RenownPedestal.class), DarkzoneBalancing.AVERAGE_RENOWN_PER_100_SOULS),
		VOUCHERS(getPedestal(HeresyPedestal.class), DarkzoneBalancing.AVERAGE_VOUCHERS_PER_100_SOULS);

		public final AltarPedestal pedestal;
		public final double base;

		public final Map<UUID, Integer> storedTemporaryReward = new HashMap<>();
		public static final List<AltarRenownReward> renownRewards = new ArrayList<>();
		public static final List<AltarVoucherReward> voucherRewards = new ArrayList<>();

		AltarReward(AltarPedestal pedestal, double base) {
			this.pedestal = pedestal;
			this.base = base;
		}

		public double getRewardCount(RewardSize size) {
			double preRandomReward = this.base * size.multiplier;
			return preRandomReward * 0.75 + preRandomReward * Math.random() * 0.5;
		}

		public void rewardPlayer(Player player, int amount) {
			if(amount == 0) return;
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			restorePlayer(player);
			if(pitPlayer.darkzoneTutorial.isActive()) storedTemporaryReward.put(player.getUniqueId(), amount);

			switch(this) {
				case ALTAR_XP:
					AltarXPReward reward = new AltarXPReward(player, amount);
					reward.spawn(AltarManager.CONFIRM_LOCATION.clone().add(0, 2, 0));
					break;
				case RENOWN:
					AltarRenownReward renownReward = new AltarRenownReward(player, amount);
					renownReward.spawn(AltarManager.CONFIRM_LOCATION.clone().add(0, 2.5, 0));
					renownRewards.add(renownReward);
					break;
				case VOUCHERS:
					AltarVoucherReward heresyReward = new AltarVoucherReward(player, amount);
					heresyReward.spawn(AltarManager.CONFIRM_LOCATION.clone().add(0, 2.5, 0));
					voucherRewards.add(heresyReward);
					break;
			}
		}

		public void     restorePlayer(Player player) {
			if(!storedTemporaryReward.containsKey(player.getUniqueId())) return;
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

			switch(this) {
				case ALTAR_XP:
					pitPlayer.darkzoneData.altarXP -= storedTemporaryReward.get(player.getUniqueId());
					break;
				case RENOWN:
					List<AltarRenownReward> renownToRemove = new ArrayList<>();
					pitPlayer.renown -= storedTemporaryReward.get(player.getUniqueId());
					for(AltarRenownReward renownReward : renownRewards) {
						if(renownReward.player != player) continue;
						renownReward.despawnReward();
						renownToRemove.add(renownReward);
					}
					renownRewards.removeAll(renownToRemove);
					break;
				case VOUCHERS:
					pitPlayer.darkzoneData.demonicVouchers -= storedTemporaryReward.get(player.getUniqueId());
					List<AltarVoucherReward> vouchersToRemove = new ArrayList<>();
					for(AltarVoucherReward voucherReward : voucherRewards) {
						if(voucherReward.player != player) continue;
						voucherReward.despawnReward();
						vouchersToRemove.add(voucherReward);
					}

					voucherRewards.removeAll(vouchersToRemove);
					break;
			}
			storedTemporaryReward.remove(player.getUniqueId());
		}
	}

	public enum RewardSize {
		NONE(DarkzoneBalancing.PEDESTAL_NONE_THRESHOLD, DarkzoneBalancing.NONE_REWARD_MULTIPLIER),
		LOW(DarkzoneBalancing.PEDESTAL_LOW_THRESHOLD, DarkzoneBalancing.LOW_REWARD_MULTIPLIER),
		HIGH(100, DarkzoneBalancing.HIGH_REWARD_MULTIPLIER);

		private final int threshold;
		private final double multiplier;

		RewardSize(int threshold, double multiplier) {
			this.threshold = threshold;
			this.multiplier = multiplier;
		}

		public int getThreshold() {
			return threshold;
		}

		public double getMultiplier() {
			return multiplier;
		}

		public static RewardSize getFromChance(double chance) {
			chance = Math.min(chance, 100);
			for(RewardSize rewardSize : values()) {
				if(chance <= rewardSize.threshold) return rewardSize;
			}
			throw new RuntimeException();
		}
	}
}
