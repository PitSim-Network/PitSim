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

	}

	@Override
	public int getStartTicks() {
		return 0;
	}

	@Override
	public void sendCompletionMessages() {

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
