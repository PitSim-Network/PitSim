package net.pitsim.pitsim.enchants.tainted.chestplate;

import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.ManaRegenEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Resilient extends PitEnchant {
	public static Resilient INSTANCE;

	public Resilient() {
		super("Resilient", true, ApplyType.CHESTPLATES,
				"resilient", "resilent", "resileint");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	public static int getRegenIncrease(PitPlayer pitPlayer) {
		if(!pitPlayer.hasManaUnlocked()) return 0;

		int enchantLvl = EnchantManager.getEnchantLevel(pitPlayer.player, INSTANCE);
		if(enchantLvl == 0) return 0;

		return getRegenIncrease(enchantLvl);
	}

	@EventHandler
	public void onManaRegen(ManaRegenEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;
		event.multipliers.add(Misc.getReductionMultiplier(getManaReduction(enchantLvl)));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Passively regenerate health &c+" + getRegenIncrease(enchantLvl) + "% &7faster. When worn, regain mana &b" +
						getManaReduction(enchantLvl) + "% &7slower"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"causes you to regenerate &bmana &7faster when worn";
	}

	public static int getRegenIncrease(int enchantLvl) {
		return enchantLvl * 15 + 5;
	}

	public static int getManaReduction(int enchantLvl) {
		return 40;
	}
}
