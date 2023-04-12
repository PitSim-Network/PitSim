package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;

public class DarkzoneTutorial extends Tutorial {

	public TutorialNPC tutorialNPC;

	public DarkzoneTutorial(TutorialData data, PitPlayer pitPlayer) {
		super(data, pitPlayer);

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
		if(tutorialNPC != null) {
			tutorialNPC.remove();
			tutorialNPC = null;
		}
	}
}
