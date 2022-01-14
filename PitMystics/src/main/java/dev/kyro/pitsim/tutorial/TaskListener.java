package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillstreakEquipEvent;
import dev.kyro.pitsim.events.MegastreakEquipEvent;
import dev.kyro.pitsim.events.PerkEquipEvent;
import dev.kyro.pitsim.tutorial.inventories.PerkGUI;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.sequences.KillstreakSequence;
import dev.kyro.pitsim.tutorial.sequences.MegastreakSequence;
import dev.kyro.pitsim.tutorial.sequences.PerkSequence;
import dev.kyro.pitsim.tutorial.sequences.VampireSequence;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskListener implements Listener {

	@EventHandler
	public void onVampEquip(PerkEquipEvent event) {
		Player player = event.getPlayer();
		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;
		if(tutorial.sequence.getClass() != VampireSequence.class) return;

		if(event.getPerk() != PitPerk.getPitPerk("vampire")) return;

		tutorial.onTaskComplete(Task.EQUIP_VAMPIRE);
	}

	@EventHandler
	public void onPerkEquip(PerkEquipEvent event) {

		new BukkitRunnable() {
			@Override
			public void run() {
				Player player = event.getPlayer();
				Tutorial tutorial = TutorialManager.getTutorial(player);
				if(tutorial == null) return;
				if(tutorial.sequence.getClass() != PerkSequence.class) return;

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				for(PitPerk pitPerk : pitPlayer.pitPerks) {
					if(pitPerk == PitPerk.getPitPerk("none")) return;
				}
				tutorial.onTaskComplete(Task.EQUIP_PERKS);
			}
		}.runTaskLater(PitSim.INSTANCE, 2L);
	}

	@EventHandler
	public void onKillstreakEquip(KillstreakEquipEvent event) {
		Player player = event.getPlayer();
		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;
		if(tutorial.sequence.getClass() != KillstreakSequence.class) return;

		if(event.getKillstreak() == Killstreak.getKillstreak("NoKillstreak")) return;
		tutorial.onTaskComplete(Task.EQUIP_KILLSTREAK);
	}

	@EventHandler
	public void onMegastreakEquip(MegastreakEquipEvent event) {
		Player player = event.getPlayer();
		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;
		if(tutorial.sequence.getClass() != MegastreakSequence.class) return;

		tutorial.onTaskComplete(Task.EQUIP_MEGASTREAK);
	}

	public static void onEnchantBillLs(Player player) {
		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;

		tutorial.onTaskComplete(Task.ENCHANT_BILL_LS);
	}

	public static void onEnchantRGM(Player player) {
		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;

		tutorial.onTaskComplete(Task.ENCHANT_RGM);
	}

	public static void onMegaDrainEnchant(Player player) {
		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;

		tutorial.onTaskComplete(Task.ENCHANT_MEGA_DRAIN);
	}


	@EventHandler
	public void onClickEvent(NPCRightClickEvent event){

		Player player = event.getClicker();

		for(Tutorial tutorial : TutorialManager.tutorials.values()) {
			if(event.getNPC() == tutorial.upgradesNPC) {
				if(player != tutorial.player) return;
				dev.kyro.pitsim.tutorial.inventories.PerkGUI perkGUI = new PerkGUI(player);
				perkGUI.open();
			}
		}
	}
}
