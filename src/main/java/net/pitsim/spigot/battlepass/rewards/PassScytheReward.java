package net.pitsim.spigot.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.items.MysticFactory;
import net.pitsim.spigot.battlepass.PassReward;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.MysticType;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassScytheReward extends PassReward {
	public int count;

	public PassScytheReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		for(int i = 0; i < count; i++) {
			ItemStack scythe = MysticFactory.getFreshItem(MysticType.TAINTED_SCYTHE, null);
			AUtil.giveItemSafely(pitPlayer.player, scythe);
		}
		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_HOE, count)
				.setName("&5Scythe Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &5" + count + "x Fresh Tainted Scythe"
				)).getItemStack();
		return itemStack;
	}
}
