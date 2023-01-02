package dev.kyro.pitsim.adarkzone;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class PitBoss {

//	Player related
	public Player summoner;
	public Map<UUID, Double> damageMap = new HashMap<>();
	public DropPool dropPool;

//	Boss related
	public NPC npcBoss;
	public Player boss;
	public List<PitBossAbility> abilities = new ArrayList<>();
	public TargetingSystem targetingSystem;

	public abstract int getMaxHealth();
	public abstract int getReach();

	public abstract void onSpawn();
}
