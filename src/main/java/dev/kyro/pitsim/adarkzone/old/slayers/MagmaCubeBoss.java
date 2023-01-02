package dev.kyro.pitsim.adarkzone.old.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.old.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.old.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Effect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MagmaCubeBoss extends PitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lMagma Cube";
	public SubLevel subLevel = SubLevel.MAGMA_CAVE;
	public SimpleBoss boss;

	public MagmaCubeBoss(Player target) {
		super(target, SubLevel.MAGMA_CAVE, 35);
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, subLevel, 5, SimpleSkin.MAGMA, this) {

			@Override
			protected void attackHigh() {

			}

			@Override
			protected void attackMedium() {


				new BukkitRunnable() {
					@Override
					public void run() {
						Sounds.KILL_FIRE.play(target);
						Vector vector = new Vector(0, 5, 0);
						target.setVelocity(vector);
						target.spigot().playEffect(target.getLocation(), Effect.LAVADRIP, 0, 0, 10, 10, 10, 1, 128, 5);

						target.damage(10, npc.getEntity());
					}
				}.runTaskLater(PitSim.INSTANCE, 10);

			}

			@Override
			protected void attackLow() {

				Vector vector = new Vector(0, 5, 0);
				target.setVelocity(vector);

				new BukkitRunnable() {
					@Override
					public void run() {
						Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
						Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
						npc.getEntity().setVelocity(pullVector.multiply(1.25));
					}
				}.runTaskLater(PitSim.INSTANCE, 10);

				new BukkitRunnable() {
					@Override
					public void run() {
						target.damage(20, npc.getEntity());
						Sounds.LUCKY_SHOT.play(target);
						target.spigot().playEffect(target.getLocation(), Effect.ZOMBIE_CHEW_WOODEN_DOOR, 0, 0, 10, 10, 10, 1, 128, 5);

					}
				}.runTaskLater(PitSim.INSTANCE, 20);


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