package dev.kyro.pitsim.adarkzone.aaold.slayers;

import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.adarkzone.aaold.OldPitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.adarkzone.aaold.OldSubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ThrowBlockEvent;
import dev.kyro.pitsim.misc.ThrowableBlock;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;


public class ChargedCreeperBossOld extends OldPitBoss {

    /*

    This is purely experimental and will probably not be the version creeper boss on full release (Basically needs bugs sorted out)

     */


	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lCreeper Boss";
	public OldSubLevel oldSubLevel = OldSubLevel.CREEPER_CAVE;
	public SimpleBoss boss;

	public ChargedCreeperBossOld(Player target) throws Exception {
		super(target, OldSubLevel.CREEPER_CAVE, 35);
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, oldSubLevel, 4, SimpleSkin.CREEPER, this) {
			@Override
			protected void attackHigh() {
				target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lNUCLEAR REACTOR! &7Ouch, you're on full blast!"));

				target.getWorld().playEffect(target.getLocation(), Effect.EXPLOSION_LARGE, 1);
				if(npc.getEntity() != null) {
					for(Entity player : npc.getEntity().getNearbyEntities(10, 10, 10)) {
						if(player != target) continue;
						PitPlayer.getPitPlayer((Player) player).damage(3.0, (LivingEntity) npc.getEntity());
					}
				}
			}

			@Override
			protected void attackMedium() {
				Vector dirVector = ChargedCreeperBossOld.this.target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
				Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));

				if(npc.getEntity() != null)
					ThrowBlockEvent.addThrowableBlock(new ThrowableBlock(npc.getEntity(), Material.TNT, pullVector.multiply((0.5 * 0.2) + 1.15)));
			}

			@Override
			protected void attackLow() {
				target.getWorld().createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 2, false, false);
			}

			@Override
			protected void defend() {

			}
		};
		this.entity = (Player) npc.getEntity();
		this.target = target;

		boss.run();
	}

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
	}

	@Override
	public void setNPC(NPC npc) {
		this.npc = npc;
	}

	@Override
	public Player getEntity() {
		return (Player) npc.getEntity();
	}

}
