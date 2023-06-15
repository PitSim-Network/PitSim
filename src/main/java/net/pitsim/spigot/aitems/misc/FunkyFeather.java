package net.pitsim.spigot.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.aitems.StaticPitItem;
import net.pitsim.spigot.controllers.GuildIntegrationManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.misc.PlayerItemLocation;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunkyFeather extends StaticPitItem {

	public FunkyFeather() {
		hasDropConfirm = true;
		marketCategory = MarketCategory.PURE_RELATED;
	}

	@Override
	public String getNBTID() {
		return "funky-feather";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("feather"));
	}

	@Override
	public Material getMaterial() {
		return Material.FEATHER;
	}

	@Override
	public String getName() {
		return "&3Funky Feather";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&eSpecial item",
				"&7Protects your inventory but",
				"&7gets consumed on death if",
				"&7in your hotbar (&aOverworld&7",
				"&7only)"
		).getLore();
	}

	public boolean useFeather(KillEvent killEvent) {
		LivingEntity killer = killEvent.getKiller();
		Player dead = killEvent.getDeadPlayer();

		for(int i = 0; i < 9; i++) {
			ItemStack itemStack = dead.getInventory().getItem(i);
			if(!isThisItem(itemStack)) continue;

			AOutput.send(dead, "&3&lFUNKY FEATHER!&7 Inventory protected!");
			if(itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
			else {
				dead.getInventory().setItem(i, null);
				killEvent.getDeadInventoryWrapper().removeItem(PlayerItemLocation.inventory(i));
			}
			dead.updateInventory();
			Sounds.FUNKY_FEATHER.play(dead);

			GuildIntegrationManager.handleFeather(killer, dead);

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(dead);
			pitPlayer.stats.feathersLost++;
			return true;
		}
		return false;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.IS_FEATHER.getRef());
	}
}
