package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.controllers.objects.PitPlayer;

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
			sendMessage("&eBefore you leave &fspawn&e, I'll show you everything you need to know!", 110);
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
		tutorialNPC.walkToCheckPoint(TutorialNPC.NPC_END_LOCATION);
		sendMessage("&eThat's all I have to show you! You're ready to explore the &5Darkzone&e!", 40);
		sendMessage("&eIf you need any help, you can always ask in chat or join &f&ndiscord.pitsim.net&e.", 100);
		sendMessage("&eAfter leaving spawn, make sure to head over to the &cMonster Caves &eto start getting &fSouls&e.", 160);
		sendMessage("&eWith that being said, good luck on your journey!", 220);
	}

	@Override
	public int getCompletionTicks() {
		return 220;
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
		return "&6&nGo ahead and choose another area to explore. I'll meet you over there!";
	}
}
