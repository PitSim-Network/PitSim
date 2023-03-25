package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.*;
import dev.kyro.pitsim.controllers.ItemFactory;

public class DarkzoneBalancing {
	public static double SCYTHE_DAMAGE = 7.5;

	public static int getAttributeAsInt(SubLevelType type, Attribute attribute) {
		return (int) getAttribute(type, attribute);
	}

	public static double getAttribute(SubLevelType subLevelType, Attribute attribute) {
		return Math.floor(attribute.getBaseValue() * Math.pow(subLevelType.getIndex() + 1, attribute.getScalar()));
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

	public enum ShredValue {
		JEWEL_PANTS(50, MysticPants.class),
		JEWEL_SWORD(50, MysticSword.class),
		JEWEL_BOW(50, MysticBow.class),
		TAINTED_SCYTHE(25, TaintedScythe.class),
		TAINTED_CHESTPLATE(25, TaintedChestplate.class),
		;

		private final int souls;
		private final Class<? extends PitItem> item;

		ShredValue(int souls, Class<? extends PitItem> item) {
			this.souls = souls;
			this.item = item;
		}

		public static ShredValue getShredValue(PitItem item) {
			for(ShredValue shredValue : values()) {
				if(shredValue.getItem().getClass().equals(item.getClass())) return shredValue;
			}
			return null;
		}

		public int getSouls() {
			return souls;
		}

		public PitItem getItem() {
			return ItemFactory.getItem(item);
		}
	}

	public static int getTravelCost(SubLevel subLevel) {
		return subLevel.getIndex() + 1;
	}
}
