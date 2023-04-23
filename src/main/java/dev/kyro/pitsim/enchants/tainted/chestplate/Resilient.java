package dev.kyro.pitsim.enchants.tainted.chestplate;

import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.ManaRegenEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
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
