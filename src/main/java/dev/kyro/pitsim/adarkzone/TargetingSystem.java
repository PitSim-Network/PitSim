package dev.kyro.pitsim.adarkzone;

import org.bukkit.entity.Player;

// This code strictly handles literal attacks, not abilities and other "attacks"
public class TargetingSystem {
	public State targetingState;
	public Player target;

//	TODO: Figure out if ranged attacks are all going to be shooting bows or if we are going to abstract and allow other stuff
//	TODO: (maybe like snowballs, fireballs, particle beams, homing particles, thrown entities)
	public enum State {
		ATTACKING_MELEE,
		ATTACKING_RANGED
	}
}
