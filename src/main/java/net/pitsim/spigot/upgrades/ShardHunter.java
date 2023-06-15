package net.pitsim.spigot.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.items.misc.AncientGemShard;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.NonManager;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.TieredRenownUpgrade;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.inventories.ShardHunterPanel;
import net.pitsim.spigot.megastreaks.Uberstreak;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ShardHunter extends TieredRenownUpgrade {
	public static ShardHunter INSTANCE;

	public ShardHunter() {
		super("Shardhunter", "SHARDHUNTER", 28, ShardHunterPanel.class);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || NonManager.getNon(killEvent.getDead()) == null) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		double chance = 0.00005 * tier;

		PitPlayer pitKiller = killEvent.getKillerPitPlayer();
		if(pitKiller.isOnMega() && pitKiller.getMegastreak() instanceof Uberstreak)
			chance *= Uberstreak.SHARD_MULTIPLIER;

		boolean givesShard = Math.random() < chance;

		if(!givesShard) return;
		AUtil.giveItemSafely(killEvent.getKillerPlayer(), ItemFactory.getItem(AncientGemShard.class).getItem(1), true);
		AOutput.send(killEvent.getKiller(), "&d&lGEM SHARD!&7 obtained from killing " + killEvent.getDeadPlayer().getDisplayName() + "!");

		File file = new File("plugins/NoteBlockAPI/Effects/ShardHunter.nbs");
		Song song = NBSDecoder.parse(file);
		RadioSongPlayer radioPlayer = new RadioSongPlayer(song);
		radioPlayer.setRepeatMode(RepeatMode.NO);
		radioPlayer.addPlayer(killEvent.getKillerPlayer());
		radioPlayer.setAutoDestroy(true);
		radioPlayer.setPlaying(true);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.EMERALD)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		DecimalFormat decimalFormat = new DecimalFormat("0.####");
		return "&f" + decimalFormat.format(tier * 0.005) + "% chance";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Gain &f+0.005% chance &7to obtain a &aGem Shard &7on bot kills, which are used to create &aTotally Legit Gems";
	}

	@Override
	public String getSummary() {
		return "&aShardhunter&7 is a &erenown&7 upgrade that gives you the small chance to gain &aAncient Gem " +
				"Shards&7 on bot kills, &aShards&7 can be used to craft a &aGem&7 that allows you to get nine " +
				"token &3Jewel&7 Items";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 13, 16, 19, 22, 25, 30, 35, 40, 50);
	}
}
