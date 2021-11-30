package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GoldenHeart extends PitEnchant {

	public GoldenHeart() {
		super("Golden Heart", false, ApplyType.PANTS,
				"goldenheart", "golden-heart", "gheart", "golden-hearts", "goldenhearts");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(KillEvent killEvent) {

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		pitKiller.heal(getHealing(enchantLvl), HealEvent.HealType.ABSORPTION, 12);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7gain &6+" + Misc.getHearts(getHealing(enchantLvl)) + " &7absorption on kill",
				"&7(max &6" + Misc.getHearts(12) + "&7)").getLore();
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl;
	}
}
