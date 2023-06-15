package net.pitsim.pitsim.enchants.tainted.uncommon;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.DarkzoneManager;
import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.SubLevel;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Fearmonger extends PitEnchant {
	public static Fearmonger INSTANCE;
	public static List<Player> immunePlayers = new ArrayList<>();

	public Fearmonger() {
		super("Fearmonger", false, ApplyType.SCYTHES,
				"fearmonger", "fear");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		for(SubLevel subLevel : DarkzoneManager.subLevels) {
			for(PitMob pitMob : subLevel.mobs) {
				if(pitMob.getTarget() != killEvent.getKillerPlayer()) continue;
				pitMob.setTarget(null);
			}
		}

		immunePlayers.add(killEvent.getKillerPlayer());
		new BukkitRunnable() {
			@Override
			public void run() {
				immunePlayers.remove(killEvent.getKillerPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, getImmuneTicks(enchantLvl));
	}

	public static boolean isImmune(Player player) {
		return immunePlayers.contains(player);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.##");
		return new PitLoreBuilder(
				"&7No mobs will attack you for &a" + decimalFormat.format(getImmuneTicks(enchantLvl) / 20.0) +
						" second" + Misc.s(getImmuneTicks(enchantLvl) / 20.0) + " &7following each mob kill"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"causes mobs to temporarily de-target from you when you get a mob kill";
	}

	public static long getImmuneTicks(int enchantLvl) {
		return enchantLvl * 5L + 5;
	}
}
