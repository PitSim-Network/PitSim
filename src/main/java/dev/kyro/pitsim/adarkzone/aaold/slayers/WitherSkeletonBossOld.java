package dev.kyro.pitsim.adarkzone.aaold.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.adarkzone.aaold.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ThrowBlockEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.TempBlock;
import dev.kyro.pitsim.misc.ThrowableBlock;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WitherSkeletonBossOld extends OldPitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lWither Skelly";
	public SubLevel subLevel = SubLevel.WITHER_CAVE;
	public SimpleBoss boss;

	public WitherSkeletonBossOld(Player target) {
		super(target, SubLevel.WITHER_CAVE, 50);
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, subLevel, 5, SimpleSkin.WITHER_SKELETON, this) {

			@Override
			protected void attackHigh() {
				AOutput.send(target, "&7&oThe Wither is forming its cage...");

				new BukkitRunnable() {
					@Override
					public void run() {
						MapManager.getDarkzone().spigot().playEffect(npc.getEntity().getLocation(), Effect.EXPLOSION_HUGE);
						Sounds.EXPLOSIVE_1.play(target);
						if(!npc.getEntity().getNearbyEntities(10, 10, 10).contains(target)) return;

						Location location = new Location(MapManager.getDarkzone(), 241.5, 21, -168.5);

						new TempBlock("plugins/WorldEdit/schematics/witherCage.schematic", new Location(MapManager.getDarkzone(), 236.5, 27, -172.5), 5);

						Misc.sendTitle(target, "&c&lTrapped!", 60);
						Misc.sendSubTitle(target, "&7For 5 Seconds", 60);
						Sounds.COMBO_STUN.play(target);

						AOutput.send(target, "&7&oYou have been trapped in the cage!");
						target.teleport(location);
						npc.teleport(WitherSkeletonBossOld.this.target.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
					}
				}.runTaskLater(PitSim.INSTANCE, 40);
			}

			@Override
			protected void attackMedium() {

			}

			@Override
			protected void attackLow() {
				Vector dirVector = WitherSkeletonBossOld.this.target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
				Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));

				if(npc.getEntity() != null)


					ThrowBlockEvent.addThrowableBlock(new ThrowableBlock(npc.getEntity(), Material.SOUL_SAND, pullVector.multiply((0.5 * 0.2) + 1.15)) {
						@Override
						public void run(EntityChangeBlockEvent event) {

							for(Entity player : event.getEntity().getNearbyEntities(5, 5, 5)) {

								if(!(player instanceof Player)) continue;

								if(target.hasPotionEffect(PotionEffectType.WITHER)) {
									target.removePotionEffect(PotionEffectType.WITHER);
									target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 9, true, true));
								} else
									target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 9, true, true));

								Sounds.WITHER_SHOOT.play((LivingEntity) player);
								PitPlayer.getPitPlayer((Player) player).damage(5.0, (LivingEntity) this.owner);
							}
						}
					});
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