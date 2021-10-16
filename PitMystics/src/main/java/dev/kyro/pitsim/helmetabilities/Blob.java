package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HelmetListeners;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.enchants.PitBlob;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class Blob extends HelmetAbility {
	BukkitTask runnable;
	public Blob(Player player) {

		super(player,"Pit Blob", "pitblob", true, 11);
	}


	@Override
	public void onActivate() {

		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(10000)) {
			AOutput.error(player,"&cNot enough gold!");
			HelmetAbility.toggledHelmets.remove(goldenHelmet);
			ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
			return;
		}

		Slime slime;
		slime = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);
		slime.setSize(1);
		PitBlob.blobMap.put(player.getUniqueId(), slime);
		ASound.play(player, Sound.NOTE_PLING, 1.3F, 2);
		AOutput.send(player, "&6&lGOLDEN HELMET! &7Activated one minute of &9Pit Blob&7. (&6-10,000g&7)");

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(!goldenHelmet.withdrawGold(10000)) {
					AOutput.error(player,"&cNot enough gold!");
					goldenHelmet.deactivate();
					ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
				} else {
					ASound.play(player, Sound.NOTE_STICKS, 2F, 1.5F);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20L, 20);


	}

	@Override
	public void onDeactivate() {
		Slime slime = PitBlob.blobMap.get(player.getUniqueId());
		slime.remove();
		PitBlob.blobMap.remove(player.getUniqueId());
		runnable.cancel();
		AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Pit Blob&c.");

	}

	@Override
	public void onProc() {


	}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to launch", "&7yourself forwards (5s cd)", "", "&7Cost: &6" + formatter.format(10000) + "g");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.SLIME_BALL);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}




}
