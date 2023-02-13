package dev.kyro.pitsim.enchants.tainted.abilities;

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
		super("Max Health", true, ApplyType.CHESTPLATES,
				"manahealth");
		isTainted = true;
		INSTANCE = this;
		isRare = true;
	}

	public int getExtraHealth(Player player, Map<PitEnchant, Integer> enchantMap) {
		if(!enchantMap.containsKey(this) || !MapManager.inDarkzone(player)) return 0;
		int enchantLvl = enchantMap.get(this);

		return getExtraHealth(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder("&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl)), "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
	}

	public int getExtraHealth(int enchantLvl) {
		return 20;
	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}
}
