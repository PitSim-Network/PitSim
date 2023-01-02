package dev.kyro.pitsim.adarkzone.aaold.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.adarkzone.aaold.OldPitBoss;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.adarkzone.aaold.OldSubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.mobs.OldPitStrongPigman;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ZombiePigmanBossOld extends OldPitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lPigman";
	public OldSubLevel oldSubLevel = OldSubLevel.PIGMEN_CAVE;
	public SimpleBoss boss;

	public List<OldPitMob> pigmen = new ArrayList<>();

	public ZombiePigmanBossOld(Player target) {
		super(target, OldSubLevel.PIGMEN_CAVE, 60);
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, oldSubLevel, 5, SimpleSkin.PIGMAN, this) {

			@Override
			protected void attackHigh() {

			}

			@Override
			protected void attackMedium() {

				for(OldPitMob pigman : pigmen) {
					if(!pigman.entity.isDead()) return;
				}

				Sounds.REPEL.play(target.getLocation());

				for(Entity entity : target.getNearbyEntities(6, 6, 6)) {
					Vector dirVector = entity.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
					Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
					if(entity == npc.getEntity()) continue;
					entity.setVelocity(pullVector);
				}

				pigmen.add(new OldPitStrongPigman(target.getLocation().clone().add(2, 0, 0)));
				pigmen.add(new OldPitStrongPigman(target.getLocation().clone().add(-2, 0, 0)));
				pigmen.add(new OldPitStrongPigman(target.getLocation().clone().add(0, 0, 2)));
				pigmen.add(new OldPitStrongPigman(target.getLocation().clone().add(0, 0, -2)));

				new BukkitRunnable() {
					@Override
					public void run() {
						for(OldPitMob pigman : pigmen) {
							((PigZombie) pigman.entity).setTarget(target);
						}
					}
				}.runTaskLater(PitSim.INSTANCE, 5);

				for(int i = 0; i < 4; i++) {
					new BukkitRunnable() {
						@Override
						public void run() {
							Sounds.ANVIL_LAND.play(target);
						}
					}.runTaskLater(PitSim.INSTANCE, i * 5);
				}


			}

			@Override
			protected void attackLow() {
				Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
				Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
				npc.getEntity().setVelocity(pullVector.multiply(1.25));

				new BukkitRunnable() {
					@Override
					public void run() {
						Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).normalize();
						Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
						target.setVelocity(pullVector);

						Sounds.LUCKY_SHOT.play(target);
						target.damage(15, npc.getEntity());
					}
				}.runTaskLater(PitSim.INSTANCE, 10);
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
		boss.defendAbility();
	}

	@Override
	public void onDeath() {
		boss.hideActiveBossBar();
		NoteBlockAPI.stopPlaying(target);
		for(OldPitMob pigman : pigmen) {
			pigman.entity.damage(1000);
		}
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