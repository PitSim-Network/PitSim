package net.pitsim.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.battlepass.PassReward;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;

public class PassKeyReward extends PassReward {
	public KeyType keyType;
	public int count;

	public PassKeyReward(KeyType keyType, int count) {
		this.keyType = keyType;
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count / 64) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		ConsoleCommandSender console = PitSim.INSTANCE.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "cc give p " + keyType.refName + " " + count + " " + pitPlayer.player.getName());

		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.TRIPWIRE_HOOK, count)
				.setName("&d&lKey Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &7" + count + "x " + keyType.displayName
				)).getItemStack();
		return itemStack;
	}

	public enum KeyType {
		PITSIM("basic", "&6&lPit&e&lSim &7Key"),
		TAINTED("tainted", "&5&lTainted &7Key");

		public final String refName;
		public final String displayName;

		KeyType(String refName, String displayName) {
			this.refName = refName;
			this.displayName = displayName;
		}
	}
}
