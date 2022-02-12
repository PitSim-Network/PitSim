package dev.kyro.pitsim.enchants;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.StereoManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.EquipmentSetEvent;
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
	public void onArmorEquip(EquipmentSetEvent event) {
		Player player = (Player) event.getHumanEntity();

		if(StereoManager.hasStereo(player)) {

			if(StereoManager.playerMusic.containsKey(player)) return;

			if(!player.hasPermission("pitsim.stereo")) {
				AOutput.error(player, "&c&lNOPE! &7You must have the &bMiraculous Rank &7or higher to use &9Stereo");
				Sounds.NO.play(player);
				return;
			}

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

			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(StereoManager.toggledPlayers.contains(onlinePlayer)) return;
				esp.addPlayer(onlinePlayer);
			}

			esp.setPlaying(true);
			StereoManager.playerMusic.put(player, esp);
		} else {
			EntitySongPlayer esp = StereoManager.playerMusic.get(player);
			if(StereoManager.playerMusic.containsKey(player)) esp.destroy();
			StereoManager.playerMusic.remove(player);
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(StereoManager.playerMusic.containsKey(player)) {
						player.getWorld().spigot().playEffect(player.getLocation(),
								Effect.NOTE, 0, 2, 0.5F, 0.5F, 0.5F,1, 5, 25);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) { }

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7You play a tune while cruising", "&7around").getLore();
	}
}
