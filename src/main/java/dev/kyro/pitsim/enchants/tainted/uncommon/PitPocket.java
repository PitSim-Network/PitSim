package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class PitPocket extends PitEnchant {
	public static PitPocket INSTANCE;

	public PitPocket() {
		super("PitPocket", false, ApplyType.SCYTHES,
				"pitpocket", "pickpocket");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) return;
		if(!canApply(attackEvent)) return;
		if(attackEvent.getAttacker() != attackEvent.getWrapperEvent().getDamager()) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int soulsToSteal = Math.min(getMaxSteal(enchantLvl), attackEvent.getDefenderPitPlayer().taintedSouls);
		if(soulsToSteal == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), getCooldownSeconds(enchantLvl) * 20);
		if(cooldown.isOnCooldown()) return;
		cooldown.restart();

		attackEvent.getDefenderPitPlayer().taintedSouls -= soulsToSteal;
		attackEvent.getAttackerPitPlayer().taintedSouls += soulsToSteal;

		Sounds.PITPOCKET.play(attackEvent.getAttackerPlayer());
		Sounds.PITPOCKET.play(attackEvent.getDefenderPlayer());
		AOutput.send(attackEvent.getAttackerPlayer(), "&6&lHEIST!&7 You stole &f" + soulsToSteal + " soul" +
				(soulsToSteal == 1 ? "" : "s") + " &7from " + Misc.getDisplayName(attackEvent.getDefenderPlayer()));
		AOutput.send(attackEvent.getDefenderPlayer(), "&6&lTHIEF!&7 " + Misc.getDisplayName(attackEvent.getAttackerPlayer()) +
				" &7stole &f" + soulsToSteal + " soul" + (soulsToSteal == 1 ? "" : "s") + " from you");
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Steal &f" + getMaxSteal(enchantLvl) + " soul" + (getMaxSteal(enchantLvl) == 1 ? "" : "s") +
						" &7on melee hit (" + getCooldownSeconds(enchantLvl) + "s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"allows you to steal &fsouls &7from other players";
	}

	public static int getCooldownSeconds(int enchantLvl) {
		return 20;
	}

	public static int getMaxSteal(int enchantLvl) {
		return enchantLvl * 2;
	}
}
