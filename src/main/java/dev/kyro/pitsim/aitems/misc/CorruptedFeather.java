package dev.kyro.pitsim.aitems.misc;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.GuildIntegrationManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorruptedFeather extends PitItem {

	public CorruptedFeather() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "corrupted-feather";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("corruptfeather"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.INK_SACK;
	}

	@Override
	public String getName(Player player) {
		return "&5Corrupted Feather";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&eSpecial item",
				"&7protects your ingredients but",
				"&7gets consumed on death if",
				"&7in your hotbar."
		).getLore();
	}

	public boolean useCorruptedFeather(LivingEntity killer, Player dead) {
		for(int i = 0; i < 9; i++) {
			ItemStack itemStack = dead.getInventory().getItem(i);
			if(!isThisItem(itemStack)) continue;

			AOutput.send(dead, "&5&lCORRUPTED FEATHER! &7Ingredients protected.");
			if(itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
			else dead.getInventory().setItem(i, null);
			dead.updateInventory();
			Sounds.FUNKY_FEATHER.play(dead);

			GuildIntegrationManager.handleFeather(killer, dead);

			// Remove this if you don't want them counting for lb
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(dead);
			if(pitPlayer.stats != null) pitPlayer.stats.feathersLost++;
			return true;
		}
		return false;
	}
}
