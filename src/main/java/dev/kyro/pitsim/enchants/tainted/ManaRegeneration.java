package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.ComboVenom;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ManaRegeneration extends PitEnchant {
	public static ManaRegeneration INSTANCE;

	public ManaRegeneration() {
		super("Regeneration", true, ApplyType.CHESTPLATES,
				"manaregen", "manaregeneration", "regen");
		isTainted = true;
		INSTANCE = this;
	}

	public static int counter = 0;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!MapManager.inDarkzone(player)) continue;
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					if(ComboVenom.isVenomed(player)) continue;

					if(counter % 2 == 0) {
						pitPlayer.heal(2);
					}
				}
				counter++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder("&7Heal significantly faster", "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}
}
