package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HelmetListeners;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class GoldRushAbility extends HelmetAbility {
	public GoldRushAbility(Player player) {

		super(player,"Gold Rush", "goldrush", true, 12);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isActive(attackEvent.attacker)) return;

		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(attackEvent.attacker);
		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(650)) {
			AOutput.error(attackEvent.attacker,"&cNot enough gold!");
			goldenHelmet.deactivate();
			Sounds.NO.play(player);
			return;
		}
		Sounds.GOLD_RUSH.play(attackEvent.attacker);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isActive(killEvent.killer)) return;

		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(killEvent.killer);
		assert goldenHelmet != null;

		killEvent.goldMultipliers.add(3D);
	}

	@Override
	public void onActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);
		assert goldenHelmet != null;

		Sounds.HELMET_ACTIVATE.play(player);
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Gold Rush&7. (&6-650g&7 per hit)");
	}

	@Override
	public boolean shouldActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		if(PitSim.VAULT.getBalance(player) < 650) {
			AOutput.error(player,"&cNot enough gold!");
			Sounds.NO.play(player);
			return false;
		}
		return true;
	}

	@Override
	public void onDeactivate() {
		AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Gold Rush&c.");
	}

	@Override
	public void onProc() { }

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to toggle", "&7Gold. Triple gold on kill", "",
				"&7Cost: &6" + formatter.format(650) + "g &7per hit");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_INGOT);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}
}
