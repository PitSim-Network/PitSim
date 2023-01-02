package dev.kyro.pitsim.adarkzone.aaold.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.adarkzone.aaold.OldSubLevel;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.adarkzone.aaold.slayers.tainted.SimpleSkin;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitBoss;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.events.AttackEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.ChatColor;

import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SkeletonBossOld extends OldPitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lSkeleton Boss";
	public OldSubLevel oldSubLevel = OldSubLevel.SKELETON_CAVE;
	public SimpleBoss boss;

	public SkeletonBossOld(Player target) throws Exception {
		super(target, OldSubLevel.SKELETON_CAVE, 12);

		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, oldSubLevel, 2, SimpleSkin.SKELETON, this) {
			@Override
			protected void attackHigh() {

			}

			@Override
			protected void attackMedium() {

			}

			@Override
			protected void attackLow() {
				if(npc.getEntity().isOnGround()) {
					((Player) npc.getEntity()).setVelocity(new Vector(0, .39, 0));
				}
			}

			@Override
			protected void defend() {
				if(npc.getEntity() != null) {
					// Really efficient way to do this trust

					Equipment equipment = npc.getTrait(Equipment.class);
					try {
						equipment.set(Equipment.EquipmentSlot.HAND, getExplosive());
					} catch(Exception e) {
						e.printStackTrace();
					}
					LivingEntity shooter = ((LivingEntity) npc.getEntity());
					shooter.launchProjectile(Arrow.class);
					target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lBONE PLATING!&7 Your attack bounced off and dealt the damage back!"));
					((LivingEntity) npc.getEntity()).setHealth(Math.min(((LivingEntity) npc.getEntity()).getHealth() + target.getLastDamage(), ((LivingEntity) npc.getEntity()).getMaxHealth()));
					npc.getEntity().setVelocity(new Vector(0, 0, 0));
					target.damage(target.getLastDamage(), npc.getEntity());
				}
			}

			@Override
			public void attackDefault(AttackEvent.Apply event) throws Exception {
				if(npc.getEntity() != null) {
					Equipment equipment = npc.getTrait(Equipment.class);
					npc.faceLocation(target.getLocation());
					equipment.set(Equipment.EquipmentSlot.HAND, getSkeletonBow());
					LivingEntity shooter = ((LivingEntity) npc.getEntity());
					shooter.launchProjectile(Arrow.class);
				}

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

	public static ItemStack getSkeletonBow() throws Exception {
		ItemStack itemStack;
		itemStack = FreshCommand.getFreshItem(MysticType.BOW, PantColor.GREEN);
//        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pull"), 3, false);
		itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("robin"), 3, false);
		itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("luckyshot"), 3, false);
		itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("parasite"), 3, false);
		itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mlb"), 7, false);
		return itemStack;
	}

}
