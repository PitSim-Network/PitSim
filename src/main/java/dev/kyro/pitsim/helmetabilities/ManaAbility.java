package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ManaAbility extends HelmetAbility {
	public ManaAbility(Player player) {

		super(player, "Mana Charge", "mana", false, 16);
	}


	@Override
	public void onActivate() {

	}

	@Override
	public boolean shouldActivate() {
		return false;
	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public void onProc() {
		ItemStack goldenHelmet = GoldenHelmet.getHelmet(player);

		assert goldenHelmet != null;
		if(!GoldenHelmet.withdrawGold(player, goldenHelmet, 10000)) {
			AOutput.error(player, "&cNot enough gold!");
			Sounds.NO.play(player);
			return;
		}

		Cooldown cooldown = getCooldown(player, 100);
		if(cooldown.isOnCooldown()) {
			AOutput.error(player, "&cAbility on cooldown!");
			Sounds.NO.play(player);
			return;
		} else cooldown.restart();

		AOutput.send(player, "&6&lGOLDEN HELMET! &7Used &9Mana Charge&7! (&6-10,000g&7)");
		Sounds.MANA.play(player);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.mana = pitPlayer.getMaxMana();
	}


	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to recharge", "&7your mana to full. (5s cooldown)", "&7Only works in the &5Darkzone&7.", "", "&7Cost: &6" + formatter.format(10000) + "g");
	}

	@Override
	public ItemStack getDisplayItem() {

		AItemStackBuilder builder = new AItemStackBuilder(Material.POTION);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		ItemStack potion = builder.getItemStack();
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setMainEffect(PotionEffectType.REGENERATION);
		potion.setItemMeta(meta);

		return potion;
	}
}
