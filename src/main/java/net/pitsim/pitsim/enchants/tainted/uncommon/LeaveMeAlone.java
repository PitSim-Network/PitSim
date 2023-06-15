package net.pitsim.pitsim.enchants.tainted.uncommon;

import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class LeaveMeAlone extends PitEnchant {
	public static LeaveMeAlone INSTANCE;

	public LeaveMeAlone() {
		super("Leave Me Alone", false, ApplyType.CHESTPLATES,
				"leavemealone", "leaveme", "leave", "alone");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.multipliers.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage from other players"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"decreases the damage you take from other players";
	}

	public double getDamageReduction(int enchantLvl) {
		return enchantLvl * 7 + 6;
	}
}
