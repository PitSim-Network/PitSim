package dev.kyro.pitsim.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.TieredRenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
		if(!killEvent.isKillerPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;
		if(!(NonManager.getNon(killEvent.getDead()) == null)) return;
		if(!(killEvent.getDead() instanceof Player)) return;
		if(BossManager.isPitBoss(killEvent.getDead())) return;
		if(!killEvent.isDeadPlayer()) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		double chance = 0.01 * tier;

		boolean isLuckyKill = Math.random() < chance;

		if(isLuckyKill) killEvent.isLuckyKill = true;

		if(isLuckyKill) {
			AOutput.send(killEvent.getKiller(), "&d&lLUCKY KILL!&7 Rewards tripled!");

			File file = new File("plugins/NoteBlockAPI/Effects/LuckyKill.nbs");
			Song song = NBSDecoder.parse(file);
			RadioSongPlayer rsp = new RadioSongPlayer(song);
			rsp.setRepeatMode(RepeatMode.NO);
			rsp.addPlayer(killEvent.getKillerPlayer());
			rsp.setPlaying(true);
		}
	}

	@Override
	public ItemStack getBaseItemStack() {
		return new AItemStackBuilder(Material.NAME_TAG)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&f" + tier + "% chance";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Gain &f+1% chance &7when getting a player kill to make it a &dLucky Kill&7, tripling " +
				"all kill rewards";
	}

	@Override
	public String getSummary() {
		return "&dLucky Kill &7is an &erenown &7upgrade that gives you a small chance to &dtriple &7player kill rewards";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 20, 30, 40);
	}
}
