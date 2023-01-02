package dev.kyro.pitsim.adarkzone.aaold.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitBoss;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.adarkzone.aaold.OldSubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.TempBlock;
import dev.kyro.pitsim.mobs.OldPitSpiderBrute;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CaveSpiderBossOld extends OldPitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lCave Spider";
	public OldSubLevel oldSubLevel = OldSubLevel.DEEP_SPIDER_CAVE;
	public SimpleBoss boss;

	public List<OldPitMob> spiderBrutes = new ArrayList<>();

	public CaveSpiderBossOld(Player target) {
		super(target, OldSubLevel.DEEP_SPIDER_CAVE, 30);
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, oldSubLevel, 5, SimpleSkin.CAVE_SPIDER, this) {

			@Override
			protected void attackHigh() {
				AOutput.send(target, "&7&oThe Spider is forming its web...");

				new BukkitRunnable() {
					@Override
					public void run() {
						MapManager.getDarkzone().spigot().playEffect(npc.getEntity().getLocation(), Effect.EXPLOSION_HUGE);
						Sounds.EXPLOSIVE_1.play(target);
						if(!npc.getEntity().getNearbyEntities(5, 5, 5).contains(target)) return;

						Location location = new Location(MapManager.getDarkzone(), 349.5, 18, 22.5, 135, 0);

						new TempBlock("plugins/WorldEdit/schematics/web.schematic", location, 5);

						Misc.sendTitle(target, "&c&lStuck!", 60);
						Misc.sendSubTitle(target, "&7For 5 Seconds", 60);
						Sounds.COMBO_STUN.play(target);

						AOutput.send(target, "&7&oYou have been trapped in the web!");
						target.teleport(location);
					}
				}.runTaskLater(PitSim.INSTANCE, 40);

			}

			@Override
			protected void attackMedium() {

			}

			@Override
			protected void attackLow() {
				if(target.getLocation().distance(npc.getEntity().getLocation()) > 5) return;

				OldPitSpiderBrute brute = new OldPitSpiderBrute(npc.getEntity().getLocation().add(0, 2, 0));
				spiderBrutes.add(brute);

				Vector dirVector = target.getLocation().toVector().subtract(brute.entity.getLocation().toVector()).setY(0);
				Vector pullVector = dirVector.clone().normalize().setY(0.5).multiply(2.5).add(dirVector.clone().multiply(0.03));
				brute.entity.setVelocity(pullVector.multiply(1.25));

				new BukkitRunnable() {
					@Override
					public void run() {
						for(OldPitMob spiderBrute : spiderBrutes) {
							((Monster) spiderBrute.entity).setTarget(target);
						}
					}
				}.runTaskLater(PitSim.INSTANCE, 5);

				Sounds.VENOM.play(target);
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

		for(OldPitMob spiderBrute : spiderBrutes) {
			spiderBrute.entity.damage(1000);
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
