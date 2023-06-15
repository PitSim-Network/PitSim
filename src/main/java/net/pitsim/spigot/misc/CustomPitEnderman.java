package net.pitsim.spigot.misc;

import com.google.common.collect.Sets;
import net.pitsim.spigot.adarkzone.SubLevel;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.*;

public class CustomPitEnderman extends EntityMonster {
	private static final UUID a = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier b;
	private static final Set<Block> c;
	private boolean bm;
	public SubLevel subLevel;

	private PathfinderGoalAvoidTarget<EntityHuman> bo;

	public CustomPitEnderman(World var1, SubLevel subLevel) {
		super(var1);
		this.subLevel = subLevel;
		this.setSize(0.6F, 2.9F);
		this.S = 1.0F;
		this.bo = new PathfinderGoalAvoidTarget(this, EntityHuman.class, 16.0F, 0.8, 1.33);

		this.goalSelector.a(3, bo);
	}

	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0);
		this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.60000001192092896);
		this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0);
		this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0);
	}

	protected void h() {
		super.h();
		this.datawatcher.a(16, new Short((short)0));
		this.datawatcher.a(17, new Byte((byte)0));
		this.datawatcher.a(18, new Byte((byte)0));
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		IBlockData var2 = this.getCarried();
		var1.setShort("carried", (short)Block.getId(var2.getBlock()));
		var1.setShort("carriedData", (short)var2.getBlock().toLegacyData(var2));
	}

	public void a(NBTTagCompound var1) {
		super.a(var1);
		IBlockData var2;
		if (var1.hasKeyOfType("carried", 8)) {
			var2 = Block.getByName(var1.getString("carried")).fromLegacyData(var1.getShort("carriedData") & '\uffff');
		} else {
			var2 = Block.getById(var1.getShort("carried")).fromLegacyData(var1.getShort("carriedData") & '\uffff');
		}

		this.setCarried(var2);
	}

	private boolean c(EntityHuman var1) {
		ItemStack var2 = var1.inventory.armor[3];
		if (var2 != null && var2.getItem() == Item.getItemOf(Blocks.PUMPKIN)) {
			return false;
		} else {
			Vec3D var3 = var1.d(1.0F).a();
			Vec3D var4 = new Vec3D(this.locX - var1.locX, this.getBoundingBox().b + (double)(this.length / 2.0F) - (var1.locY + (double)var1.getHeadHeight()), this.locZ - var1.locZ);
			double var5 = var4.b();
			var4 = var4.a();
			double var7 = var3.b(var4);
			return var7 > 1.0 - 0.025 / var5 ? var1.hasLineOfSight(this) : false;
		}
	}

	public float getHeadHeight() {
		return 2.55F;
	}

	public void m() {
		if (this.world.isClientSide) {
			for(int var1 = 0; var1 < 2; ++var1) {
				this.world.addParticle(EnumParticle.PORTAL, this.locX + (this.random.nextDouble() - 0.5) * (double)this.width, this.locY + this.random.nextDouble() * (double)this.length - 0.25, this.locZ + (this.random.nextDouble() - 0.5) * (double)this.width, (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0, new int[0]);
			}
		}

		this.aY = false;
		super.m();
	}

	protected void E() {
		BlockPosition bp = new BlockPosition(this);
		Location currentLocation = new Location(this.world.getWorld(), bp.getX(), bp.getY(), bp.getZ());
		if(currentLocation.distance(subLevel.getMiddle()) > 20) {
			Location middle = subLevel.getMiddle().add(0, 2 ,0);
//			teleport(middle.getX(), middle.getY(), middle.getZ());
		}


		super.E();
	}

	protected boolean n() {
		double var1 = this.locX + (this.random.nextDouble() - 0.5) * 64.0;
		double var3 = this.locY + (double)(this.random.nextInt(64) - 32);
		double var5 = this.locZ + (this.random.nextDouble() - 0.5) * 64.0;
		return this.k(var1, var3, var5);
	}

	protected boolean b(Entity var1) {
		Vec3D var2 = new Vec3D(this.locX - var1.locX, this.getBoundingBox().b + (double)(this.length / 2.0F) - var1.locY + (double)var1.getHeadHeight(), this.locZ - var1.locZ);
		var2 = var2.a();
		double var3 = 16.0;
		double var5 = this.locX + (this.random.nextDouble() - 0.5) * 8.0 - var2.a * var3;
		double var7 = this.locY + (double)(this.random.nextInt(16) - 8) - var2.b * var3;
		double var9 = this.locZ + (this.random.nextDouble() - 0.5) * 8.0 - var2.c * var3;
		return this.k(var5, var7, var9);
	}

	protected boolean k(double var1, double var3, double var5) {
		double var7 = this.locX;
		double var9 = this.locY;
		double var11 = this.locZ;
		this.locX = var1;
		this.locY = var3;
		this.locZ = var5;
		boolean var13 = false;
		BlockPosition var14 = new BlockPosition(this.locX, this.locY, this.locZ);
		if (this.world.isLoaded(var14)) {
			boolean var15 = false;

			while(!var15 && var14.getY() > 0) {
				BlockPosition var16 = var14.down();
				Block var17 = this.world.getType(var16).getBlock();
				if (var17.getMaterial().isSolid()) {
					var15 = true;
				} else {
					--this.locY;
					var14 = var16;
				}
			}

			if (var15) {
				EntityTeleportEvent var29 = new EntityTeleportEvent(this.getBukkitEntity(), new Location(this.world.getWorld(), var7, var9, var11), new Location(this.world.getWorld(), this.locX, this.locY, this.locZ));
				this.world.getServer().getPluginManager().callEvent(var29);
				if (var29.isCancelled()) {
					return false;
				}

				Location var31 = var29.getTo();
				this.enderTeleportTo(var31.getX(), var31.getY(), var31.getZ());
				if (this.world.getCubes(this, this.getBoundingBox()).isEmpty() && !this.world.containsLiquid(this.getBoundingBox())) {
					var13 = true;
				}
			}
		}

		if (!var13) {
			this.setPosition(var7, var9, var11);
			return false;
		} else {
			this.world.makeSound(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
			this.makeSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

	protected String z() {
		return this.co() ? "mob.endermen.scream" : "mob.endermen.idle";
	}

	protected String bo() {
		return "mob.endermen.hit";
	}

	protected String bp() {
		return "mob.endermen.death";
	}

	protected Item getLoot() {
		return Items.ENDER_PEARL;
	}

	protected void dropDeathLoot(boolean var1, int var2) {
		Item var3 = this.getLoot();
		if (var3 != null) {
			int var4 = this.random.nextInt(2 + var2);

			for(int var5 = 0; var5 < var4; ++var5) {
				this.a(var3, 1);
			}
		}

		Item var6 = Item.getItemOf(this.getCarried().getBlock());
		if (var6 != null) {
			this.a(var6, 1);
		}

	}

	public void setCarried(IBlockData var1) {
		this.datawatcher.watch(16, (short)(Block.getCombinedId(var1) & '\uffff'));
	}

	public IBlockData getCarried() {
		return Block.getByCombinedId(this.datawatcher.getShort(16) & '\uffff');
	}

	public boolean damageEntity(DamageSource var1, float var2) {
		if (this.isInvulnerable(var1)) {
			return false;
		} else {
			if (var1.getEntity() == null || !(var1.getEntity() instanceof EntityEndermite)) {
				if (!this.world.isClientSide) {
					this.a(true);
				}

				if (var1 instanceof EntityDamageSource && var1.getEntity() instanceof EntityHuman) {
					if (var1.getEntity() instanceof EntityPlayer && ((EntityPlayer)var1.getEntity()).playerInteractManager.isCreative()) {
						this.a(false);
					} else {
						this.bm = true;
					}
				}

				if (var1 instanceof EntityDamageSourceIndirect) {
					this.bm = false;

					for(int var4 = 0; var4 < 64; ++var4) {
						if (this.n()) {
							return true;
						}
					}

					return false;
				}
			}

			boolean var3 = super.damageEntity(var1, var2);
			if (var1.ignoresArmor() && this.random.nextInt(10) != 0) {
				this.n();
			}

			return var3;
		}
	}

	public boolean co() {
		return this.datawatcher.getByte(18) > 0;
	}

	public void a(boolean var1) {
		this.datawatcher.watch(18, (byte)(var1 ? 1 : 0));
	}

	static {
		b = (new AttributeModifier(a, "Attacking speed boost", 0.15000000596046448, 0)).a(false);
		c = Sets.newIdentityHashSet();
		c.add(Blocks.GRASS);
		c.add(Blocks.DIRT);
		c.add(Blocks.SAND);
		c.add(Blocks.GRAVEL);
		c.add(Blocks.YELLOW_FLOWER);
		c.add(Blocks.RED_FLOWER);
		c.add(Blocks.BROWN_MUSHROOM);
		c.add(Blocks.RED_MUSHROOM);
		c.add(Blocks.TNT);
		c.add(Blocks.CACTUS);
		c.add(Blocks.CLAY);
		c.add(Blocks.PUMPKIN);
		c.add(Blocks.MELON_BLOCK);
		c.add(Blocks.MYCELIUM);
	}

	static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget {
		private EntityHuman g;
		private int h;
		private int i;
		private net.minecraft.server.v1_8_R3.EntityEnderman j;

		public PathfinderGoalPlayerWhoLookedAtTarget(net.minecraft.server.v1_8_R3.EntityEnderman var1) {
			super(var1, EntityHuman.class, true);
			this.j = var1;
		}

		public boolean a() {
			double var1 = this.f();
			List var3 = this.e.world.a(EntityHuman.class, this.e.getBoundingBox().grow(var1, 4.0, var1), this.c);
			Collections.sort(var3, this.b);
			if (var3.isEmpty()) {
				return false;
			} else {
				this.g = (EntityHuman)var3.get(0);
				return true;
			}
		}

		public void c() {
			this.h = 5;
			this.i = 0;
		}

		public void d() {
			super.d();
		}

		public boolean b() {
			return super.b();
		}

		public void e() {
			super.e();
		}
	}

	static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {
		private net.minecraft.server.v1_8_R3.EntityEnderman a;

		public PathfinderGoalEndermanPlaceBlock(net.minecraft.server.v1_8_R3.EntityEnderman var1) {
			this.a = var1;
		}

		public boolean a() {
			return false;
		}

		public void e() {
			Random var1 = this.a.bc();
			World var2 = this.a.world;
			int var3 = MathHelper.floor(this.a.locX - 1.0 + var1.nextDouble() * 2.0);
			int var4 = MathHelper.floor(this.a.locY + var1.nextDouble() * 2.0);
			int var5 = MathHelper.floor(this.a.locZ - 1.0 + var1.nextDouble() * 2.0);
			BlockPosition var6 = new BlockPosition(var3, var4, var5);
			Block var7 = var2.getTypeIfLoaded(var6).getBlock();
			if (var7 != Blocks.AIR) {
				Block var8 = var2.getType(var6.down()).getBlock();
				if (this.a(var2, var6, this.a.getCarried().getBlock(), var7, var8) && !CraftEventFactory.callEntityChangeBlockEvent(this.a, var6.getX(), var6.getY(), var6.getZ(), this.a.getCarried().getBlock(), this.a.getCarried().getBlock().toLegacyData(this.a.getCarried())).isCancelled()) {
					var2.setTypeAndData(var6, this.a.getCarried(), 3);
					this.a.setCarried(Blocks.AIR.getBlockData());
				}

			}
		}

		private boolean a(World var1, BlockPosition var2, Block var3, Block var4, Block var5) {
			return !var3.canPlace(var1, var2) ? false : (var4.getMaterial() != Material.AIR ? false : (var5.getMaterial() == Material.AIR ? false : var5.d()));
		}
	}

	static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {
		private net.minecraft.server.v1_8_R3.EntityEnderman enderman;

		public PathfinderGoalEndermanPickupBlock(net.minecraft.server.v1_8_R3.EntityEnderman var1) {
			this.enderman = var1;
		}

		public boolean a() {
			return !this.enderman.world.getGameRules().getBoolean("mobGriefing") ? false : (this.enderman.getCarried().getBlock().getMaterial() != Material.AIR ? false : this.enderman.bc().nextInt(20) == 0);
		}

		public void e() {

		}
	}
}
