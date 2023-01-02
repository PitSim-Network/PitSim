package dev.kyro.pitsim.adarkzone.aaold.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.adarkzone.aaold.OldPitBoss;
import dev.kyro.pitsim.adarkzone.aaold.OldSubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ZombieBossOld extends OldPitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lZombie Boss";
	public OldSubLevel oldSubLevel = OldSubLevel.ZOMBIE_CAVE;
	public SimpleBoss boss;

	public ZombieBossOld(Player target) throws Exception {
		super(target, OldSubLevel.ZOMBIE_CAVE, 6);
		this.target = target;

		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, oldSubLevel, 1, SimpleSkin.ZOMBIE, this) {
			@Override
			protected void attackHigh() {

			}

			@Override
			protected void attackMedium() {

			}

			@Override
			protected void attackLow() {

			}

			@Override
			protected void defend() {

			}
		};
		this.entity = (Player) npc.getEntity();
		this.target = target;

		boss.run();
	}

	@Override
	public void onAttack(AttackEvent.Apply event) throws Exception {
		boss.attackAbility(event);
	}

	@Override
	public void onDefend() {
		double health = ((LivingEntity) npc.getEntity()).getHealth();
		double maxHealth = ((LivingEntity) npc.getEntity()).getMaxHealth();
		float progress = (float) health / (float) maxHealth;
		boss.getActiveBar().progress(progress);

		npc.getNavigator().setTarget(target, true);
	}

	@Override
	public void onDeath() {
		boss.hideActiveBossBar();
		NoteBlockAPI.stopPlaying(target);
	}

	@Override
	public Player getEntity() {
		return (Player) npc.getEntity();
	}

	@Override
	public void setNPC(NPC npc) {
		this.npc = npc;
	}
}
