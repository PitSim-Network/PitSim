package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HelmetListeners;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.enchants.PitBlob;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class BlobAbility extends HelmetAbility {
	public BukkitTask runnable;
	public BlobAbility(Player player) {
		super(player,"Pit Blob", "pitblob", true, 11);
	}

	@Override
	public void onActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		Slime slime;
		slime = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);
		slime.setSize(1);
		PitBlob.blobMap.put(player.getUniqueId(), slime);
		Sounds.HELMET_ACTIVATE.play(player);
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Pit Blob&7 (&6-4,000g&7 per second)");

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(!goldenHelmet.withdrawGold(4000)) {
					AOutput.error(player,"&cNot enough gold!");
					goldenHelmet.deactivate();
					Sounds.NO.play(player);
				} else {
					Sounds.HELMET_TICK.play(player);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20L, 20);
	}

	@Override
	public boolean shouldActivate() {
		GoldenHelmet goldenHelmet = HelmetListeners.getHelmetInstance(player);

		assert goldenHelmet != null;
		if(!goldenHelmet.withdrawGold(4000)) {
			AOutput.error(player,"&cNot enough gold!");
			Sounds.NO.play(player);
			return false;
		}
		return true;
	}

	@Override
	public void onDeactivate() {
		Slime slime = PitBlob.blobMap.get(player.getUniqueId());
		try {
			slime.remove();

		} catch(Exception e) {
			e.printStackTrace();
		}
		PitBlob.blobMap.remove(player.getUniqueId());
		if(runnable != null) runnable.cancel();
		AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Pit Blob&c.");
	}

	@Override
	public void onProc() { }

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to toggle the", "&7Pit Blob. The blob grows", "&7with kills.", "", "&7Cost: &6" +
				formatter.format(4000) + "g &7per second");
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
