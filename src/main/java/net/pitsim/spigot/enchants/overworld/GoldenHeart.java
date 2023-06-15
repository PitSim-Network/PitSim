package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.HealEvent;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GoldenHeart extends PitEnchant {

	public GoldenHeart() {
		super("Golden Heart", false, ApplyType.PANTS,
				"goldenheart", "golden-heart", "gheart", "golden-hearts", "goldenhearts");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitKiller = killEvent.getKillerPitPlayer();
		pitKiller.heal(getHealing(enchantLvl), HealEvent.HealType.ABSORPTION, 12, this);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Gain &6+" + Misc.getHearts(getHealing(enchantLvl)) + " &7absorption on kill (max &6" +
				Misc.getHearts(12) + "&7)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that gives you " +
				"&6absorption &7on kill";
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl;
	}
}
