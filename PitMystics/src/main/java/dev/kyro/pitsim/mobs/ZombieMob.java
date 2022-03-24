package dev.kyro.pitsim.mobs;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Zombie;

public class ZombieMob extends EntityZombie {
	public ZombieMob(World world) {
		super(((CraftWorld) world).getHandle());

		Zombie craftZombie = (Zombie) this.getBukkitEntity();
		craftZombie.setMaxHealth(50);
		this.setHealth(50);

		this.setCustomName(ChatColor.RED + "Zombie");

		EntityLiving en = (EntityLiving) ((CraftEntity) craftZombie).getHandle();
		AttributeInstance speed = en.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
		AttributeModifier speedModifier = new AttributeModifier(craftZombie.getUniqueId(), "SpeedIncreaser", 0.6, 1);
		speed.b(speedModifier);
		speed.a(speedModifier);
		this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(10D);
		this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(50D);

		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
		this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
		this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 5.0D));
		this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 100.0F));
		this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
		this.getWorld().addEntity(this);

		this.setEquipment(0, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.DIAMOND_SWORD)));
		this.setEquipment(1, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.DIAMOND_HELMET)));
		this.setEquipment(2, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.DIAMOND_CHESTPLATE)));
		this.setEquipment(3, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.DIAMOND_LEGGINGS)));
		this.setEquipment(4, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.DIAMOND_BOOTS)));

	}

	@Override
	public boolean r(Entity var1) {
		return super.r(var1);
	}

	@Override
	public void m() {
		if (this.world.w() && !this.world.isClientSide && !this.isBaby()) {
			float var1 = this.c(1.0F);
			BlockPosition var2 = new BlockPosition(this.locX, (double)Math.round(this.locY), this.locZ);
			if (var1 > 0.5F && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.world.i(var2)) {
				boolean var3 = true;
				ItemStack var4 = this.getEquipment(4);
				if (var4 != null) {
					if (var4.e()) {
						var4.setData(var4.h() + this.random.nextInt(2));
						if (var4.h() >= var4.j()) {
							this.b(var4);
							this.setEquipment(4, (ItemStack)null);
						}
					}

					var3 = false;
				}
			}
		}

		if (this.au() && this.getGoalTarget() != null && this.vehicle instanceof EntityChicken) {
			((EntityInsentient)this.vehicle).getNavigation().a(this.getNavigation().j(), 1.5D);
		}

		super.m();
	}

	@Override
	protected Item getLoot() {
		return null;
	}
}
