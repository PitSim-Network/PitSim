package dev.kyro.pitsim.adarkzone;

public class DarkzoneBalancing {
	public static double SCYTHE_DAMAGE = 7.5;

	public static int getAttributeAsInt(SubLevelType type, Attribute attribute) {
		return (int) getAttribute(type, attribute);
	}

	public static double getAttribute(SubLevelType subLevelType, Attribute attribute) {
		return attribute.getBaseValue() * Math.pow(subLevelType.getIndex() + 1, attribute.getScalar());
	}

	public enum Attribute {
		BOSS_DAMAGE(8, 1.3),
		BOSS_HEALTH(70, 1.3),
		BOSS_SOULS(10, 1.4),
		MOB_DAMAGE(8, 1.3),
		MOB_HEALTH(40, 1.3),
		MOB_SOULS(5, 1.4),
		;

		private final double baseValue;
		private final double scalar;

		Attribute(double baseValue, double scalar) {
			this.baseValue = baseValue;
			this.scalar = scalar;
		}

		public double getBaseValue() {
			return baseValue;
		}

		public double getScalar() {
			return scalar;
		}
	}
}
