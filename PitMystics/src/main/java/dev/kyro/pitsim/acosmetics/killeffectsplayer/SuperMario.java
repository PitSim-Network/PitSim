package dev.kyro.pitsim.acosmetics.killeffectsplayer;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class SuperMario extends PitCosmetic {

	public SuperMario() {
		super("&fSuper Mario", "supermario", CosmeticType.PLAYER_KILL_EFFECT);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getDeadPlayer()) || !isEnabled(killEvent.getKillerPitPlayer()) ||
				nearMid(killEvent.getDeadPlayer())) return;

		File file = new File("plugins/NoteBlockAPI/Effects/mariodeath.nbs");
		Song song = NBSDecoder.parse(file);
		PositionSongPlayer psp = new PositionSongPlayer(song);
		psp.setTargetLocation(killEvent.getDeadPlayer().getLocation());
		psp.setDistance(SOUND_RANGE);
		psp.setRepeatMode(RepeatMode.NO);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) psp.addPlayer(onlinePlayer);
		psp.setPlaying(true);
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.RED_MUSHROOM)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
