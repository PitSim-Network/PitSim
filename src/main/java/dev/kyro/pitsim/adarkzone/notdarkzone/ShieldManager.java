package dev.kyro.pitsim.adarkzone.notdarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.uncommon.Mending;
import dev.kyro.pitsim.enchants.tainted.znotcodedrare.PurpleThumb;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ShieldManager implements Listener {
	public static final double ACTIVE_REGEN_AMOUNT = 0.05;

	static {
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(pitPlayer.shield.isActive()) {
						double activeRegenAmount = ACTIVE_REGEN_AMOUNT;
						activeRegenAmount *= Mending.getIncreaseMultiplier(pitPlayer.player);
						activeRegenAmount *= PurpleThumb.getShieldRegenMultiplier(pitPlayer.player);
						pitPlayer.shield.addShield(activeRegenAmount);
					} else {
						pitPlayer.shield.regenerateTick();
					}
					pitPlayer.updateXPBar();
				}
				count++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}
}
