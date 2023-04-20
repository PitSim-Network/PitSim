package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class DarkzoneTutorial extends Tutorial {

	public TutorialNPC tutorialNPC;

	public DarkzoneTutorial(PitPlayer pitPlayer) {
		super(pitPlayer.darkzoneTutorialData, pitPlayer);

		tutorialNPC = null;
	}

	@Override
	public Class<? extends Tutorial> getTutorialClass() {
		return DarkzoneTutorial.class;
	}

	@Override
	public void sendStartMessages() {
		if(data.completedObjectives.size() == 0) {
			sendMessage("&eHey! Welcome to the &5Darkzone&e!", 60);
			sendMessage("&eBefore you leave &fSpawn&e, I'll show you everything you need to know!", 110);
		} else if(TutorialManager.isOnLastObjective(pitPlayer.player)) {
			delayTask(() -> {
				sendNPCToLastCheckpoint();
				sendMessage(getProceedMessage(), 0);
			}, 60);

		} else {
			sendMessage("&eWelcome back to the &5Darkzone&e!", 60);
			sendMessage("&eI'll show you to the areas you missed last time.", 110);
		}
		sendMessage("&eWalk around to the &aGreen Highlighted Areas &eand I'll explain their uses!", 160);
	}

	@Override
	public int getStartTicks() {
		return 160;
	}

	@Override
	public void sendCompletionMessages() {
		sendMessage("&eThat's all I have to show you! You're ready to explore the &5Darkzone&e!", 40);
		sendMessage("&eIf you need any help, you can always ask in chat or join &f&ndiscord.pitsim.net&e.", 100);
		sendMessage("&eNow, proceed into the &cMonster Caves &eto start getting &fSouls&e.", 160);
		sendMessage("&eWith that being said, good luck on your journey!", 220);

//		tutorialNPC.npc.getNavigator().setTarget(new Location(MapManager.getDarkzone(), 292, 78, -130));
	}

	@Override
	public int getCompletionTicks() {
		return 220;
	}

	@Override
	public void onObjectiveComplete(TutorialObjective objective) {
		if(TutorialManager.isOnLastObjective(pitPlayer.player)) {

			NPCCheckpoint checkpoint = TutorialManager.getCheckpoint(TutorialObjective.MONSTER_CAVES);
			assert checkpoint != null;
			tutorialNPC.walkToCheckPoint(checkpoint);

			Misc.applyPotionEffect((LivingEntity) tutorialNPC.npc.getEntity(), PotionEffectType.SPEED, 999999, 1, false, false);
		}
	}

	@Override
	public boolean isActive() {
		return pitPlayer.prestige >= 5 && data.completedObjectives.size() < getObjectiveSize() && PitSim.status.isDarkzone();
	}

	@Override
	public void onStart() {
		tutorialNPC = new TutorialNPC(this);
	}

	@Override
	public void onTutorialEnd() {
		for(AltarPedestal.AltarReward value : AltarPedestal.AltarReward.values()) {
			value.restorePlayer(pitPlayer.player);
		}

		if(tutorialNPC != null) {
			tutorialNPC.remove();
			tutorialNPC = null;
		}
	}

	@Override
	public String getProceedMessage() {
		if(TutorialManager.isOnLastObjective(pitPlayer.player)) {
			return "&6&nCome meet me at the &c&nMonster Caves &6&noutside of spawn for your final objective!";
		} else return "&6&nGo ahead and choose another area to explore. I'll meet you over there!";
	}

	public void sendNPCToLastCheckpoint() {
		NPCCheckpoint checkpoint = TutorialManager.getCheckpoint(TutorialObjective.MONSTER_CAVES);
		assert checkpoint != null;
		tutorialNPC.walkToCheckPoint(checkpoint);

		Misc.applyPotionEffect((LivingEntity) tutorialNPC.npc.getEntity(), PotionEffectType.SPEED, 999999, 1, false, false);
	}
}
