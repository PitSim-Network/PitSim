package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.events.armor.AChangeEquipmentEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class GottaGoFast extends PitEnchant {
	public static GottaGoFast INSTANCE;

	public GottaGoFast() {
		super("Gotta go fast", false, ApplyType.PANTS,
				"gotta-go-fast", "gottagofast", "gtgf", "gotta", "fast");
		INSTANCE = this;
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onArmorEquip(AChangeEquipmentEvent event) {

		Player player = event.getPlayer();

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		int enchantLvl = enchantMap.getOrDefault(this, 0);

		Map<PitEnchant, Integer> oldEnchantMap = EnchantManager.getEnchantsOnPlayer(event.getPreviousArmor());
		int oldEnchantLvl = oldEnchantMap.getOrDefault(this, 0);

		if(enchantLvl != oldEnchantLvl) {

			player.setWalkSpeed(getWalkSpeed(enchantLvl));
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(level != 0) {
						player.getWorld().spigot().playEffect(player.getLocation(),
								Effect.CLOUD, 0, 1, 0.5F, 0.5F, 0.5F,0.01F, 5, 25);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Move &e" + Misc.roundString(getWalkSpeedLore(enchantLvl)) + "&e% faster &7at all times").getLore();
	}

//	TODO: GTGF equation
	public float getWalkSpeed(int enchantLvl) {

		switch(enchantLvl) {
			case 0:
				return 0.2F;
			case 1:
				return 0.2F + (0.2F * 0.04F);
			case 2:
				return 0.2F + (0.2F * 0.1F);
			case 3:
				return 0.2F + (0.2F * 0.2F);
		}
		return 0.2F;
	}

	public float getWalkSpeedLore(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 4;
			case 2:
				return 10;
			case 3:
				return 20;
		}
		return 0.2F;
	}
}
