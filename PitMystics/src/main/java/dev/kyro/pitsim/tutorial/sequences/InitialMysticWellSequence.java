package dev.kyro.pitsim.tutorial.sequences;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.inventories.EnchantingGUI;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class InitialMysticWellSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public int waitTime = 0;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public InitialMysticWellSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.VIEW_MYSTIC_WELL);
		this.player = player;
		this.tutorial = tutorial;
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		placeTable();
		sendMessage(TutorialMessage.MYSTIC1);
		wait(5);
		sendMessage(TutorialMessage.MYSTIC2);
		wait(5);
		sendMessage(TutorialMessage.MYSTIC3);
		wait(5);
		sendMessage(TutorialMessage.MYSTIC4);
		wait(5);
		openGUI();
		sendMessage(TutorialMessage.MYSTIC5);
		wait(5);
		sendMessage(TutorialMessage.MYSTIC6);
		wait(5);
		sendMessage(TutorialMessage.MYSTIC7);
		wait(5);
		sendMessage(TutorialMessage.MYSTIC8);
		wait(5);
		completeTask(Task.VIEW_MYSTIC_WELL);
	}

	public void wait(int seconds) {
		waitTime = waitTime + seconds;
	}

	public void sendMessage(TutorialMessage message) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				MessageManager.sendTutorialMessage(player, message);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void completeTask(Task task) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.onTaskComplete(task);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void openGUI() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				EnchantingGUI enchantGUI = new EnchantingGUI(player);
				player.openInventory(enchantGUI.getHomePanel().getInventory());
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void placeTable() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				EnchantingGUI enchantGUI = new EnchantingGUI(player);
				tutorial.upgradesNPC.destroy();
				tutorial.upgradesNPC = null;

				Location holoLocation = tutorial.areaLocation.clone().add(0, 3.5, 1);
				Hologram holo = HologramsAPI.createHologram(PitSim.INSTANCE, tutorial.areaLocation);
				holo.appendTextLine("&d&lMystic Well");
				tutorial.mysticWellHolo = holo;

				Location blockLocation = tutorial.areaLocation.add(0, -1, 0);
				Bukkit.getWorld("tutorial").getBlockAt(blockLocation).setType(Material.ENCHANTMENT_TABLE);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}


}
