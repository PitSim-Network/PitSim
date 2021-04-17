public class Main {

	public static void main(String[] args) {

		System.out.println(getDamageReduction(3));
		System.out.println(getDamageMultiplier(3));
	}

	public static int getDamageReduction(int enchantLvl) {

		return (int) Math.max(Math.floor(Math.pow(enchantLvl, 1.3) * 2) + 2, 0);
	}

	public static double getDamageMultiplier(int enchantLvl) {

		return (100D - getDamageReduction(enchantLvl)) / 100;
	}
}
