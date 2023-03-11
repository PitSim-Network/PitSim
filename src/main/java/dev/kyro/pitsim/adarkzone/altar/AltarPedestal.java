package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.AltarBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

		if(isActivated(player)) deactivate(player, false);
		else activate(player);
	}

	public int getIndex() {
		return altarPedestals.indexOf(this);
	}

	public static AltarPedestal getPedestal(int index) {
		return altarPedestals.get(index);
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
}
