package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.adarkzone.altar.pedestals.*;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AltarManager {
	public static final Location TEXT_LOCATION = new Location(MapManager.getDarkzone(), 192.5, 95, -104.5);

	public static ArmorStand[] textStands = new ArmorStand[7];

	static {
		new KnowledgePedestal(new Location(MapManager.getDarkzone(), 189.5, 90.5, -102.5));
		new RenownPedestal(new Location(MapManager.getDarkzone(), 189.5, 90.5, -105.5));
		new HeresyPedestal(new Location(MapManager.getDarkzone(), 192.5, 90.5, -107.5));
		new WealthPedestal(new Location(MapManager.getDarkzone(), 195.5, 90.5, -105.5));
		new TurmoilPedestal(new Location(MapManager.getDarkzone(), 195.5, 90.5, -102.5));

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(onlinePlayer.getWorld() != MapManager.getDarkzone()) continue;
					if(onlinePlayer.getLocation().distance(TEXT_LOCATION) > 30) {
						AltarPedestal.disableAll(onlinePlayer);
						continue;
					}

					setDefaultText(onlinePlayer);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20);
	}


	public static void init() {
		for(int i = 0; i < 7; i++) {
			ArmorStand stand = (ArmorStand) MapManager.getDarkzone().spawnEntity(TEXT_LOCATION.clone().add(0, -0.3 * i, 0), EntityType.ARMOR_STAND);
			stand.setMarker(true);
			stand.setVisible(false);
			stand.setCustomNameVisible(true);
			stand.setArms(true);
			stand.setCustomName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "TAINTED ALTAR");
			stand.setGravity(false);

			textStands[i] = stand;
		}
	}

	public static void setText(Player player, String[] text) {
		if(text.length != 7) return;

		for(int i = 0; i < 7; i++) {
			if(text[i] == null) text[i] = "";

			DataWatcher dw = ((CraftEntity)textStands[i]).getHandle().getDataWatcher();
			dw.watch(2, (Object)ChatColor.translateAlternateColorCodes('&', text[i]));
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(textStands[i].getEntityId(), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
	}

	public static void setDefaultText(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);

		int difference = info.darkzoneLevel - pitPlayer.altarLevel;
		String color = difference > 0 ? "&c" : "&a";
		String status = difference > 0 ? "&7Taking &f" + Math.min(difference, 100) + "% &7of &bXP &7and &6Gold" : "&aYou are stronger than the Darkzone!";

		setText(player, new String[] {
				"&5Darkzone Level: " + info.darkzoneLevel,
				"&8&m----------------------",
				"&4&lAltar Level",
				"&4" + pitPlayer.altarLevel + " " + AUtil.createProgressBar("|", ChatColor.RED, ChatColor.GRAY, 30,
						pitPlayer.altarXP / DarkzoneLeveling.getXPForLevel(pitPlayer.altarLevel + 1)) + " &4" + (pitPlayer.altarLevel + 1),
				"&8&m----------------------",
				"&7Level Difference: " + color + Math.abs(difference),
				status
		});
	}

	public static void cleanUp() {
		for(ArmorStand textStand : textStands) {
			textStand.remove();
		}

		AltarPedestal.cleanUp();
	}
}
