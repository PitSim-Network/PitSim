package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class Hearts extends PitEnchant {
	public static Hearts INSTANCE;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {

					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					pitPlayer.updateMaxHealth();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public Hearts() {
		super("Hearts", false, ApplyType.PANTS,
				"hearts", "heart", "health");
		INSTANCE = this;
	}

	@EventHandler
	public void onArmorEquip(EquipmentSetEvent event) {
		Player player = (Player) event.getHumanEntity();

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.updateMaxHealth();
	}

	public int getExtraHealth(Map<PitEnchant, Integer> enchantMap) {
		if(!enchantMap.containsKey(this)) return 0;
		int enchantLvl = enchantMap.get(this);

		return getExtraHealth(enchantLvl);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl))).getLore();
	}

	public int getExtraHealth(int enchantLvl) {

		return enchantLvl + 1;
	}
}
