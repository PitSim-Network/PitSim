package net.pitsim.spigot.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.aitems.PitItem;
import net.pitsim.spigot.controllers.PortalManager;
import net.pitsim.spigot.controllers.SpawnManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TheCakeIsALie extends PitItem {
	public static List<Player> cooldownPlayers = new ArrayList<>();

	public TheCakeIsALie() {
		hasUUID = true;
		hasDropConfirm = true;
		marketCategory = MarketCategory.MISC;
	}

	@Override
	public String getNBTID() {
		return "cake";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("cake"));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	public Material getMaterial() {
		return Material.CAKE;
	}

	public String getName() {
		return "&8The &cC&ea&6k&ae &8is a &c&nLIE!";
	}

	public List<String> getLore(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		return new ALoreBuilder(
				"&7Teleports you to the &5Darkzone",
				"&7when right-clicked in spawn",
				"",
				"&7Usages: &c" + nbtItem.getInteger(NBTTag.CAKE_USAGES.getRef()),
				"",
				"&7Kept on death"
		).getLore();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!defaultUpdateItem(itemStack)) return;

		itemStack.setType(getMaterial());
		new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore(itemStack));
	}

	public ItemStack getItem() {
		ItemStack itemStack = new ItemStack(getMaterial(), 1);
		itemStack = buildItem(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack, true);
		nbtItem.setInteger(NBTTag.CAKE_USAGES.getRef(), 5);

		return new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore(itemStack))
				.getItemStack();
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand();
		if(!isThisItem(itemStack)) return;
		event.setCancelled(true);

		if(cooldownPlayers.contains(player)) {
			AOutput.error(player, "&c&lERROR!&7 You are currently on cooldown!");
			return;
		}

		if(!PitSim.status.isOverworld()) {
			AOutput.error(player, "&c&lERROR!&7 You can only use this in the &aOverworld&7!");
			return;
		}

		if(!SpawnManager.isInSpawn(player)) {
			AOutput.error(player, "&c&lERROR!&7 You can only use this in spawn!");
			return;
		}

		cooldownPlayers.add(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				cooldownPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 100L);

		NBTItem nbtItem = new NBTItem(itemStack, true);
		int uses = nbtItem.getInteger(NBTTag.CAKE_USAGES.getRef());
		if(uses <= 1) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else {
			nbtItem.setInteger(NBTTag.CAKE_USAGES.getRef(), uses - 1);
		}
		updateItem(itemStack);
		player.updateInventory();

		PortalManager.attemptServerSwitch(player);
		AOutput.send(player, "&c&lC&e&lA&6&lK&a&lE&b&l!&7 You feel a bit queasy...");
		Sounds.CAKE_CONSUME.play(player);
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		return null;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}
}
