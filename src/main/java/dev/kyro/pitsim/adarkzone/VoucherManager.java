package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.controllers.ActionBarManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.PlayerItemLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class VoucherManager implements Listener {

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!SpawnManager.isInSpawn(player)) continue;
					if(player.getWorld() == MapManager.getDarkzone()) continue;

					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					int vouchers = pitPlayer.darkzoneData.demonicVouchers;
					if(vouchers <= 0) continue;

					int mystics = Misc.getItemCount(player, true, (pitItem, itemStack) -> MysticFactory.isJewel(itemStack, true));

					String voucherText = vouchers != 1 ? "Vouchers" : "Voucher";
					String mysticText = mystics != 1 ? "Jewels" : "Jewel";
					String message = "&4" + vouchers + " " + voucherText + "&7, &d" + mystics + " " + mysticText;

					ActionBarManager.sendActionBar(player, message);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20);
	}


	@EventHandler(priority = EventPriority.HIGH)
	public void onKill(KillEvent event) {
		if(!PitSim.getStatus().isOverworld()) return;
		Player deadPlayer = event.getDeadPlayer();
		PitPlayer deadPitPlayer = event.getDeadPitPlayer();
		int totalVouchersUsed = 0;

		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : new ArrayList<>(event.getVulnerableItems().entrySet())) {
			KillEvent.ItemInfo info = entry.getValue();
			ItemStack itemStack = info.itemStack;

			boolean isJewel = MysticFactory.isJewel(itemStack, true);
			if(deadPitPlayer.darkzoneData.demonicVouchers > 0 && isJewel) {
				totalVouchersUsed++;
				deadPitPlayer.darkzoneData.demonicVouchers--;
				event.removeVulnerableItem(entry.getKey());
			}
		}

		if(totalVouchersUsed > 0) {
			int finalTotalVouchersUsed = totalVouchersUsed;
			new BukkitRunnable() {
				@Override
				public void run() {
					AOutput.send(deadPlayer, "&4&lHERESY! &7Saved &f" + finalTotalVouchersUsed + " &7lives with &4Demonic Vouchers&7!");
					Sounds.VOUCHER_USE.play(deadPlayer);
				}
			}.runTaskLater(PitSim.INSTANCE, 10);
		}
	}
}
