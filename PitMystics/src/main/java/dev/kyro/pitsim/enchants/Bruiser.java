package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Bruiser extends PitEnchant {

	public Bruiser() {
		super("Bruiser", false, ApplyType.SWORDS,
				"bruiser");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0 || !attackEvent.defender.isBlocking()) return;
		attackEvent.decrease += getDamageReduction(enchantLvl);

		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		String command = "ipban " + attackEvent.defender.getName() + " 1m pov bruiser";
		Bukkit.dispatchCommand(console, command);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Blocking with your swords reduces", "&7received damage by &c" + Misc.getHearts(getDamageReduction(enchantLvl))).getLore();
	}

	public double getDamageReduction(int enchantLvl) {

		return Math.floor(Math.pow(enchantLvl, 1.3) * 0.5);
	}
}
