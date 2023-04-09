package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.objects.PitPlayer;

public class DarkzoneTutorial extends Tutorial {
	public DarkzoneTutorial(TutorialData data, PitPlayer pitPlayer) {
		super(data, pitPlayer);
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
		return false;
	}
}
