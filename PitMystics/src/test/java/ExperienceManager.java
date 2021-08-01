import java.util.ArrayList;
import java.util.List;

public class ExperienceManager {

	public static List<Long> levelMap = new ArrayList<>();

	static {

		for(int i = 0; i < 2000; i++) {
			levelMap.add(getXP(i));
		}

		for(int i = 0; i < levelMap.size(); i++) {
//			System.out.println(i + " " + levelMap.get(i));
		}
	}

	public static void main(String[] args) {

//		System.out.println(getXP(101) - getXP(100));
//		System.out.println(getXPToNextLvl(getXP(500)));
	}

	public static int getLevel(long xp) {

		for(int i = 0; i < levelMap.size(); i++) {

			long lvlXP = levelMap.get(i);
			if(xp < lvlXP) continue;
			return i;
		}

		return -1;
	}

	public static long getXP(long level) {

		return (long) (9 + 10 * level + Math.pow(level, 2.3) + Math.pow(1.015, level));
	}

	public static long getXPToNextLvl(long currentXP) {

		int currentLvl = getLevel(currentXP);
		return getXP(currentLvl + 1) - getXP(currentLvl);
	}

	public static double logN(double base, double num) {

		return Math.log(num) / Math.log(base);
	}
}
