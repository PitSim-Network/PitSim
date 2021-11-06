package dev.kyro.pitsim.controllers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LeaderboardManager {
    public static Map<String, String> finalLevels = new HashMap<>();
    public static TreeMap<String, Integer> finalSorted = new TreeMap<>();
    public static TreeMap<String, Integer> eventSorted = new TreeMap<>();

//    public static void calculate() {
//        Map<String, Integer> levels = new HashMap<>();
//        File directory = new File("plugins/PitRemake/playerdata");
//        File[] files = directory.listFiles();
//        for(File file : files) {
//            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
//            if(data.getString("name") == null) file.delete();
////            if(data.getInt("playerkills") == 0 && data.getInt("level") == 1) {
////                file.delete();
////                System.out.println("deleted file");
////            }
//            levels.put(file.getName(), (1000 * data.getInt("prestige") + data.getInt("level")));
//        }
//
//        ValueComparator bvc =  new ValueComparator(levels);
//        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
//        sorted_map.putAll(levels);
//        finalSorted.clear();
//        finalSorted = sorted_map;
//
//        for(File file : files) {
//            for(Map.Entry<String, Integer> s :  finalSorted.entrySet()) {
////                Bukkit.broadcastMessage(file.getName());
////                Bukkit.broadcastMessage(s.getKey());
//                if(s.getKey().equals(file.getName())) {
//                    Bukkit.broadcastMessage("test!");
//                    FileConfiguration data = YamlConfiguration.loadConfiguration(file);
//                    PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(data.getInt("prestige"));
//                    if(data.getInt("prestige") != 0) finalLevels.put(data.getString("name"), ChatColor.translateAlternateColorCodes('&', info.getOpenBracket() + "&e"
//                            + AUtil.toRoman(data.getInt("prestige")) + info.bracketColor + "-" + PrestigeValues.getLevelColor(data.getInt("level")
//                            + data.getInt("level" + info.getCloseBracket()))));
//                }
//            }
//        }
//
//
//    }
//
    public static TreeMap<String, Integer> calculateEvent(Map<String, Integer> data) {

        ValueComparator bvc =  new ValueComparator(data);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        sorted_map.putAll(data);

        eventSorted = sorted_map;
        return eventSorted;
    }
//
//static {
//    new BukkitRunnable() {
//
//        @Override
//        public void run() {
//
//            for(Player player : Bukkit.getOnlinePlayers()) {
//                PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
//                String message = "%luckperms_prefix%";
//                if(pitPlayer.megastreak.isOnMega()) {
//                    pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(player, message);
//                } else {
//                    pitPlayer.prefix = PrestigeValues.getPlayerPrefix(pitPlayer.player) + PlaceholderAPI.setPlaceholders(player, message);
//                }
//            }
//
//            calculate();
//        }
//    }.runTaskTimer(PitSim.INSTANCE, 0L, 4000L);
//}

}




class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}

