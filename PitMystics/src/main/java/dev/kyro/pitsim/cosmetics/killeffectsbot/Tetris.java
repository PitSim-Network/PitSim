package dev.kyro.pitsim.cosmetics.killeffectsbot;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NotePitch;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Tetris extends PitCosmetic {
	public static Map<UUID, Integer> playerSongPositionMap = new HashMap<>();
	public static List<Float> tetrisThemeNotes = new ArrayList<>();

	static {
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());

		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_11.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_15.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_15.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_13.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_11.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_5.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_8.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_10.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_6.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
		tetrisThemeNotes.add(NotePitch.PITCH_3.getPitch());
	}

	public Tetris() {
		super("&c&lT&6&lE&e&lT&a&lR&b&lI&d&lS&9&l!", "tetris", CosmeticType.BOT_KILL_EFFECT);
		preventKillSound = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(NonManager.getNon(killEvent.getDead()) == null || !isEnabled(killEvent.getKillerPitPlayer())) return;
		int count = playerSongPositionMap.getOrDefault(killEvent.getKillerPlayer().getUniqueId(), 0);
		killEvent.getKillerPlayer().playSound(killEvent.getKillerPlayer().getLocation(), Sound.NOTE_PLING, 1, tetrisThemeNotes.get(count));
		playerSongPositionMap.put(killEvent.getKillerPlayer().getUniqueId(), (count + 1) % tetrisThemeNotes.size());
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		playerSongPositionMap.putIfAbsent(pitPlayer.player.getUniqueId(), 0);
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		playerSongPositionMap.remove(pitPlayer.player.getUniqueId());
	}

	@EventHandler
	public void onDeath(KillEvent killEvent) {
		if(!killEvent.isDeadPlayer()) return;
		playerSongPositionMap.remove(killEvent.getDeadPlayer().getUniqueId());
	}

	@EventHandler
	public void onDeath(OofEvent event) {
		Player player = event.getPlayer();
		playerSongPositionMap.remove(player.getUniqueId());
	}

//	TODO: Add on spawn when spawnevent is added

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.WOOL, 1, Misc.getTetrisWoolColor())
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Go for a new high streak",
						"&7while playing the Tetris",
						"&7theme!"
				))
				.getItemStack();
		return itemStack;
	}
}
