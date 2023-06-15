package net.pitsim.spigot.enchants.overworld;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.MusicManager;
import net.pitsim.spigot.controllers.StereoManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.EquipmentChangeEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Stereo extends PitEnchant {
	public static Stereo INSTANCE;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<Player, EntitySongPlayer> entry : StereoManager.playerMusic.entrySet()) {
					Player player = entry.getKey();
					player.getWorld().spigot().playEffect(player.getLocation(),
							Effect.NOTE, 0, 2, 0.5F, 0.5F, 0.5F, 1, 5, 25);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	public Stereo() {
		super("Stereo", false, ApplyType.PANTS,
				"stereo", "sterio");
		INSTANCE = this;
	}

	@EventHandler
	public void onArmorEquip(EquipmentChangeEvent event) {
		Player player = event.getPlayer();
		int currentStereoLevel = EnchantManager.getEnchantLevel(player, this);
		boolean hadStereo = StereoManager.playerMusic.containsKey(player);

//		Put on stereo
		if(currentStereoLevel != 0 && !hadStereo) {
			if(!player.hasPermission("pitsim.stereo")) {
				AOutput.error(player, "&c&lERROR!&7 You must have the &bMiraculous Rank &7or higher to use &9Stereo");
				Sounds.NO.play(player);
				return;
			}

			MusicManager.stopPlaying(player);

			File[] files = new File("plugins/NoteBlockAPI/Songs/").listFiles();
			assert files != null;
			File file = files[new Random().nextInt(files.length)];

			Song song = NBSDecoder.parse(file);
			EntitySongPlayer songPlayer = new EntitySongPlayer(song);
			songPlayer.setEntity(player);
			songPlayer.setDistance(16);
			songPlayer.setRepeatMode(RepeatMode.ONE);

			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(PitPlayer.getPitPlayer(player).musicDisabled) continue;
				songPlayer.addPlayer(onlinePlayer);
			}

			songPlayer.setAutoDestroy(true);
			songPlayer.setPlaying(true);
			StereoManager.playerMusic.put(player, songPlayer);
		} else if(currentStereoLevel == 0 && hadStereo) {
//			Take off stereo
			if(!StereoManager.playerMusic.containsKey(player)) return;
			EntitySongPlayer songPlayer = StereoManager.playerMusic.remove(player);
			songPlayer.destroy();
		}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7You play a tune while cruising around"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that plays " +
				"music to the wearer and all those nearby. Requires a rank to use (its cosmetic only)";
	}
}
