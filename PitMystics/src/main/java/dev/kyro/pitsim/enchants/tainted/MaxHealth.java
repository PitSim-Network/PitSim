package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class MaxHealth extends PitEnchant {
	public static MaxHealth INSTANCE;

	public MaxHealth() {
		super("Max Health", false, ApplyType.CHESTPLATES,
				"manahealth");
		isUncommonEnchant = true;
		tainted = true;
		INSTANCE = this;
	}

	public int getExtraHealth(Player player, Map<PitEnchant, Integer> enchantMap) {
		if(!enchantMap.containsKey(this) || !MapManager.inDarkzone(player)) return 0;
		int enchantLvl = enchantMap.get(this);

		return getExtraHealth(enchantLvl);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl))).getLore();
	}

	public int getExtraHealth(int enchantLvl) {

		return enchantLvl * 6 + 2;
	}
}
