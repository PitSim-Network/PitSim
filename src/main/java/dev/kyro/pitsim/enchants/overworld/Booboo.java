package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Booboo extends PitEnchant {
	public static Booboo INSTANCE;
	public static int counter = 0;

	public Booboo() {
		super("Boo-boo", false, ApplyType.PANTS,
				"boo-boo", "boo", "bb", "booboo");
		isUncommonEnchant = true;
		INSTANCE = this;

		if(!isEnabled()) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					if(ComboVenom.isVenomed(player)) continue;

					if(level == 1 && counter % 5 == 0) {
						pitPlayer.heal(2);
					} else if(level == 2 && counter % 4 == 0) {
						pitPlayer.heal(2);
					} else if(level == 3 && counter % 3 == 0) {
						pitPlayer.heal(2);
					} else if(level == 4 && counter % 2 == 0) {
						pitPlayer.heal(2);
					} else if(level > 4) {
						pitPlayer.heal(2);
					}
				}

				counter++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Passively regain &c" + Misc.getHearts(2) + " &7every " +
						getSeconds(enchantLvl) + " &7seconds"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that " +
				"passively heals you while wearing it";
	}

	public int getSeconds(int enchantLvl) {

		return Math.max(5 - enchantLvl, 1);
	}
}
