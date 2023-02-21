package dev.kyro.pitsim.enchants.overworld;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Map;

public class Hearts extends PitEnchant {
	public static Hearts INSTANCE;

	public Hearts() {
		super("Hearts", false, ApplyType.PANTS,
				"hearts", "heart", "health");
		INSTANCE = this;
	}

	@EventHandler
	public void onArmorEquip(ArmorEquipEvent event) {
		Player player = event.getPlayer();

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.updateMaxHealth();
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl))
		).getLore();
	}

	public int getExtraHealth(int enchantLvl) {

		return enchantLvl * 2;
	}

	public int getExtraHealth(Map<PitEnchant, Integer> enchantMap) {
		if(!enchantMap.containsKey(this)) return 0;
		int enchantLvl = enchantMap.get(this);

		return getExtraHealth(enchantLvl);
	}
}
