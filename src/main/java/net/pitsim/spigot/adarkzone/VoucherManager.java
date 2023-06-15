package net.pitsim.spigot.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.aitems.MysticFactory;
import net.pitsim.spigot.controllers.ActionBarManager;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.SpawnManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.misc.PlayerItemLocation;
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
		if(!deadPitPlayer.isOnMega()) return;
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
					AOutput.send(deadPlayer, "&4&lHERESY!&7 Saved &f" + finalTotalVouchersUsed + " &7lives with &4Demonic Vouchers&7!");
					Sounds.VOUCHER_USE.play(deadPlayer);
				}
			}.runTaskLater(PitSim.INSTANCE, 10);
		}
	}
}
