package net.pitsim.pitsim.cosmetics.killeffectsbot;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.NonManager;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.cosmetics.CosmeticType;
import net.pitsim.pitsim.cosmetics.PitCosmetic;
import net.pitsim.pitsim.enums.NotePitch;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.events.PlayerSpawnCommandEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSpawn(PlayerSpawnCommandEvent event) {
		Player player = event.getPlayer();
		playerSongPositionMap.remove(player.getUniqueId());
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.WOOL, 1, getTetrisWoolColor())
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Go for a new high streak",
						"&7while playing the Tetris",
						"&7theme!"
				))
				.getItemStack();
		return itemStack;
	}

	public static byte getTetrisWoolColor() {
		int randomInt = new Random().nextInt(6);
		switch(randomInt) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;
			case 4:
				return 5;
			case 5:
				return 14;
		}
		return -1;
	}
}
