package dev.kyro.pitsim.misc;

import net.minecraft.server.v1_8_R3.*;

import java.lang.reflect.Field;

public class CustomPitBat extends EntityBat {
	public Entity target;

	public CustomPitBat(World world, Entity target) {
		super(world);
		this.target = target;

		setAsleep(false);
	}

	@Override
	public void E() {
		setTargetPos(new BlockPosition((int)target.locX, (int)target.locY + 2.5, (int)target.locZ));

//		if (this.getTargetPos() != null && (!this.world.isEmpty(this.getTargetPos()) || this.getTargetPos().getY() < 1)) {
//			this.setTargetPos(null);
//		}
//
//		if (this.getTargetPos() == null || this.random.nextInt(30) == 0 || this.getTargetPos().c((int)this.locX, (int)this.locY, (int)this.locZ) < 4.0) {
//			this.setTargetPos(new BlockPosition((int)this.locX + this.random.nextInt(7) - this.random.nextInt(7),
//					(int)this.locY + this.random.nextInt(6) - 2, (int)this.locZ + this.random.nextInt(7) - this.random.nextInt(7)));
//		}


		double var3 = (double)this.getTargetPos().getX() + 0.5 - this.locX;
		double var5 = (double)this.getTargetPos().getY() + 0.1 - this.locY;
		double var7 = (double)this.getTargetPos().getZ() + 0.5 - this.locZ;
		this.motX += (Math.signum(var3) * 0.5 - this.motX) * 0.10000000149011612;
		this.motY += (Math.signum(var5) * 0.699999988079071 - this.motY) * 0.10000000149011612;
		this.motZ += (Math.signum(var7) * 0.5 - this.motZ) * 0.10000000149011612;
		float var9 = (float)(MathHelper.b(this.motZ, this.motX) * 180.0 / 3.1415927410125732) - 90.0F;
		float var10 = MathHelper.g(var9 - this.yaw);
//		this.ba = 0.5F;
//		this.yaw = 0;
	}

	public void setTargetPos(BlockPosition var1) {
		Field a = EntityBat.class.getDeclaredFields()[0];
		a.setAccessible(true);
		try {
			a.set(this, var1);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public BlockPosition getTargetPos() {
		Field a = EntityBat.class.getDeclaredFields()[0];
		a.setAccessible(true);
		try {
			return (BlockPosition) a.get(this);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
