package dev.kyro.pitsim.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class LuckyKill extends TieredRenownUpgrade {
	public static LuckyKill INSTANCE;

	public LuckyKill() {
		super("Lucky Kill", "LUCKY_KILL", 5);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this) ||
				NonManager.getNon(killEvent.getDead()) == null) return;

		double chance = 0.01 * UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		boolean isLuckyKill = Math.random() < chance;
		if(!isLuckyKill) return;

		killEvent.xpMultipliers.add(3.0);
		killEvent.maxXPMultipliers.add(3.0);
		killEvent.goldMultipliers.add(3.0);
		killEvent.goldCap *= 3.0;
		AOutput.send(killEvent.getKiller(), "&d&lLUCKY KILL!&7 Rewards tripled!");

		File file = new File("plugins/NoteBlockAPI/Effects/LuckyKill.nbs");
		Song song = NBSDecoder.parse(file);
		RadioSongPlayer rsp = new RadioSongPlayer(song);
		rsp.setRepeatMode(RepeatMode.NO);
		rsp.addPlayer(killEvent.getKillerPlayer());
		rsp.setPlaying(true);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.NAME_TAG)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&f" + tier + "% chance";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Gain &f+1% chance &7when getting a bot kill to make it a &dLucky Kill&7, tripling " +
				"all kill rewards and caps";
	}

	@Override
	public String getSummary() {
		return "&dLucky Kill &7is an &erenown &7upgrade that gives you a small chance to &dtriple &7bot kill rewards";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 20, 30, 40);
	}
}
