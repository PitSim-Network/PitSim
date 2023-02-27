import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPassItems {
	public static List<String> items = new ArrayList<>();

	static {
		items.add("xp");
		items.add("gold");
		items.add("pitsim key");
		items.add("booster");
		items.add("vile");
		items.add("vile");
		items.add("vile");
		items.add("jsword");
		items.add("jbow");
		items.add("jpants");
		items.add("darkzone drop");
		items.add("feather");
		items.add("shards");
		items.add("renown");
		items.add("darkzone drop");
	}

	public static void main(String[] args) {
		String previousItem = null;
		for(int i = 0; i < 32; i++) {
			List<String> items = new ArrayList<>(RandomPassItems.items);
			if(previousItem != null) items.remove(previousItem);
			String item = items.get(new Random().nextInt(items.size()));
			System.out.println(item);
			previousItem = item;
			if(i % 8 == 7) System.out.println();
		}
	}
}
