package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HelmetListeners;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class LeapAbility extends HelmetAbility {
	public LeapAbility(Player player) {

		super(player,"Leap", "leap", false, 10);
	}


	@Override
	public void onActivate() {

	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public void onProc() {

		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(10000)) {
			AOutput.error(player,"&cNot enough gold!");
			ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
			return;
		}

		Cooldown cooldown = getCooldown(player, 100);
		if(cooldown.isOnCooldown()) {
			AOutput.error(player, "&cAbility on cooldown!");
			ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
			return;
		} else cooldown.reset();


		AOutput.send(player, "&6&lGOLDEN HELMET! &7Used &9Leap&7! (&6-10,000g&7)");
		ASound.play(player, Sound.BAT_TAKEOFF, 1, 1);
		Vector vector = player.getLocation().getDirection().setY(0).normalize().multiply(3).setY(1);
//		if(vector.getY() < 0) vector.setY(-vector.getY());
		player.setVelocity(vector);



	}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to launch", "&7yourself forwards (5s cd)", "", "&7Cost: &6" + formatter.format(10000) + "g");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.RABBIT_FOOT);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}
}
