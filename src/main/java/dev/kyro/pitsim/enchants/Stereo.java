package dev.kyro.pitsim.enchants;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.MusicManager;
import dev.kyro.pitsim.controllers.StereoManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Stereo extends PitEnchant {
	public static Stereo INSTANCE;

	public Stereo() {
		super("Stereo", false, ApplyType.PANTS,
				"stereo", "sterio");
		INSTANCE = this;
	}

	@EventHandler
	public void onArmorEquip(ArmorEquipEvent event) {
		Player player = (Player) event.getPlayer();

		new BukkitRunnable() {
			@Override
			public void run() {
				boolean stereoOnChest = false;
				boolean stereoOnPants = false;
				if(!Misc.isAirOrNull(event.getPlayer().getInventory().getChestplate())) {
					if(!EnchantManager.getEnchantsOnItem(event.getPlayer().getInventory().getChestplate()).containsKey(this))
						stereoOnChest = true;
				}
				if(!Misc.isAirOrNull(event.getPlayer().getInventory().getLeggings())) {
					if(!EnchantManager.getEnchantsOnItem(event.getPlayer().getInventory().getLeggings()).containsKey(this))
						stereoOnPants = true;
				}

				if(StereoManager.hasStereo(player)) {

//					if(stereoOnChest && MapManager.inDarkzone(player)) {
//						EntitySongPlayer esp = StereoManager.playerMusic.get(player);
//						if(StereoManager.playerMusic.containsKey(player)) esp.destroy();
//						StereoManager.playerMusic.remove(player);
//						return;
//					}

					if(StereoManager.playerMusic.containsKey(player)) return;

					if(!player.hasPermission("pitsim.stereo") && !stereoOnChest) {
						AOutput.error(player, "&c&lERROR!&7 You must have the &bMiraculous Rank &7or higher to use &9Stereo");
						Sounds.NO.play(player);
						return;
					}

					if(!stereoOnPants && stereoOnChest && !MapManager.inDarkzone(player)) return;
					if(stereoOnPants && !stereoOnChest && MapManager.inDarkzone(player)) return;

					File exampleSong = new File("plugins/NoteBlockAPI/Songs/AllStar.nbs");
					File dir = new File(exampleSong.getAbsoluteFile().getParent());
					File[] files = dir.listFiles();
					Random rand = new Random();
					assert files != null;
					File file = files[rand.nextInt(files.length)];

					Song song = NBSDecoder.parse(file);
					EntitySongPlayer esp = new EntitySongPlayer(song);
					esp.setEntity(player);
					esp.setDistance(16);
					esp.setRepeatMode(RepeatMode.ONE);

					MusicManager.stopPlaying(player);

					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(PitPlayer.getPitPlayer(player).musicDisabled) continue;
						esp.addPlayer(onlinePlayer);
					}

					esp.setAutoDestroy(true);
					esp.setPlaying(true);
					StereoManager.playerMusic.put(player, esp);
				} else {
					EntitySongPlayer esp = StereoManager.playerMusic.get(player);
					if(StereoManager.playerMusic.containsKey(player)) esp.destroy();
					StereoManager.playerMusic.remove(player);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(StereoManager.playerMusic.containsKey(player)) {
						player.getWorld().spigot().playEffect(player.getLocation(),
								Effect.NOTE, 0, 2, 0.5F, 0.5F, 0.5F, 1, 5, 25);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7You play a tune while cruising", "&7around").getLore();
	}
}
