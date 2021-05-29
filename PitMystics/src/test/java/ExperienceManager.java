import java.util.HashMap;
import java.util.Map;

public class ExperienceManager {

	public static Map<Integer, Long> levelMap = new HashMap<>();

	static {

		for(int i = 0; i < 2000; i++) {
			levelMap.put(i, getXP(i));
		}

		for(Map.Entry<Integer, Long> entry : levelMap.entrySet()) {
			System.out.println(entry.getValue());
		}
	}

	public static void main(String[] args) {

//		System.out.println(getXP(101) - getXP(100));
		System.out.println(getXPToNextLvl(getXP(500)));
	}

	public static int getLevel(long xp) {

		for(int i = 0; i < levelMap.entrySet().size(); i++) {

//			levelMap.get
		}
		return -1;
	}

	public static long getXP(long level) {

		return (long) (10 + 10 * level + Math.pow(level, 2.3) + Math.pow(1.015, level));
	}

	public static long getXPToNextLvl(long currentXP) {

		int currentLvl = getLevel(currentXP);
		return getXP(currentLvl + 1) - getXP(currentLvl);
	}

	public static double logN(double base, double num) {

		return Math.log(num) / Math.log(base);
	}
}
