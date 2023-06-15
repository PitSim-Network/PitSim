package net.pitsim.pitsim.misc;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class CustomPitBlaze extends EntityBlaze {
	public static final double MINIMUM_RANGE = 3;

	public Entity target;

	public CustomPitBlaze(World world) {
		super(world);
		this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.13000000417232513);
	}

	@Override
	protected void E() {
		try {
			Field bField = EntityBlaze.class.getDeclaredField("b");
			bField.setAccessible(true);

			Field aField = EntityBlaze.class.getDeclaredField("a");
			aField.setAccessible(true);

			float a = aField.getFloat(this);

			bField.set(this, bField.getInt(this) - 1);

			if (bField.getInt(this) <= 0) {
				bField.setInt(this, 100);
				aField.setFloat(this, 0.5F + (float)this.random.nextGaussian() * 3.0F);
			}

			EntityLiving var1 = this.getGoalTarget();
			if (var1 != null && var1.locY + (double)var1.getHeadHeight() > this.locY + (double)this.getHeadHeight() + (double) aField.getFloat(this)) {
				this.motY += (0.20000001192092896 - this.motY) * 0.20000001192092896;
				this.ai = true;
			}
		} catch(NoSuchFieldException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}

		if(this.getGoalTarget() == null) return;
		BlockPosition targetPos = new BlockPosition(this.getGoalTarget());

		Location blazeLoc = new Location(this.world.getWorld(), this.locX, this.locY, this.locZ);
		Location targetLoc = new Location(this.world.getWorld(), targetPos.getX(), targetPos.getY(), targetPos.getZ());
		if(blazeLoc.distance(targetLoc) < MINIMUM_RANGE) return;

		double var3 = (double)targetPos.getX() + 0.5 - this.locX;
		double var5 = (double)targetPos.getY() + 0.1 - this.locY;
		double var7 = (double)targetPos.getZ() + 0.5 - this.locZ;
		this.motX += (Math.signum(var3) * 0.5 - this.motX) * 0.10000000149011612;
		this.motY += (Math.signum(var5) * 0.699999988079071 - this.motY) * 0.10000000149011612;
		this.motZ += (Math.signum(var7) * 0.5 - this.motZ) * 0.10000000149011612;
	}
}