package nl.sbdeveloper.themeparkexpansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import nl.iobyte.themepark.ThemePark;
import nl.iobyte.themepark.api.attraction.enums.Status;
import nl.iobyte.themepark.api.attraction.objects.Attraction;
import nl.iobyte.themepark.api.ridecount.enums.TotalType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThemeParkExpansion extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "sbdeveloper";
    }

    @Override
    public String getIdentifier() {
        return "tp";
    }

    @Override
    public String getRequiredPlugin() {
        return "ThemePark";
    }

    @Override
    public String getVersion() {
        return "1.6.3";
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin());
    }

    private String color(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier == null)
            return "";

        /* Non-player placeholders */

        //%tp_status:ID%
        if (identifier.startsWith("status")) {
            String[] args = identifier.split(":");
            if (args.length < 2)
                return "";

            String id = args[1];
            if (!ThemePark.getInstance().getAPI().getAttractionService().hasAttraction(id))
                return "";

            Status status = ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id).getStatus();
            return status.getColor() + status.getName();
        }

        //%tp_name:ID%
        if (identifier.startsWith("name")) {
            String[] args = identifier.split(":");
            if (args.length < 2)
                return "";

            String id = args[1];
            if (!ThemePark.getInstance().getAPI().getAttractionService().hasAttraction(id))
                return "";

            return color(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id).getName());
        }

        //%tp_region:ID%
        if (identifier.startsWith("region")) {
            String[] args = identifier.split(":");
            if (args.length < 2)
                return "";

            String id = args[1];
            if (!ThemePark.getInstance().getAPI().getAttractionService().hasAttraction(id))
                return "";

            return ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id).getRegionID();
        }

        //%tp_ridecounttop_name:<ID>:<Position>:[Type]%
        if (identifier.startsWith("ridecounttop_name")) {
            String[] args = identifier.split(":");
            if (args.length < 3)
                return "";

            String id = args[1];
            if (!ThemePark.getInstance().getAPI().getAttractionService().hasAttraction(id))
                return "";

            int position;
            try {
                position = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                return "";
            }

            TotalType type = TotalType.TOTAL;
            if (args.length > 3) {
                type = TotalType.valueOf(args[3].toUpperCase());
            }

            Map<UUID, Integer> map;
            switch (type) {
                case DAILY:
                    map = ThemePark.getInstance().getAPI().getRideCountService().getTopData().getDay(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id)).join();
                    break;
                case WEEKLY:
                    map = ThemePark.getInstance().getAPI().getRideCountService().getTopData().getWeek(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id)).join();
                    break;
                case MONTHLY:
                    map = ThemePark.getInstance().getAPI().getRideCountService().getTopData().getMonth(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id)).join();
                    break;
                case YEARLY:
                    map = ThemePark.getInstance().getAPI().getRideCountService().getTopData().getYear(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id)).join();
                    break;
                default:
                    map = ThemePark.getInstance().getAPI().getRideCountService().getTopData().getTotal(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id)).join();
                    break;
            }

            if (map.size() < position)
                return "";

            UUID key = map.keySet().toArray(UUID[]::new)[position - 1];
            OfflinePlayer of = Bukkit.getOfflinePlayer(key);

            if (!of.isOnline() && !of.hasPlayedBefore())
                return "";

            return of.getName();
        }

        //%tp_ridecounttop_value:<ID>:<Position>%
        if (identifier.startsWith("ridecounttop_value")) {
            String[] args = identifier.split(":");
            if (args.length < 3)
                return "";

            String id = args[1];
            if (!ThemePark.getInstance().getAPI().getAttractionService().hasAttraction(id))
                return "";

            int position;
            try {
                position = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                return "";
            }

            HashMap<UUID, Integer> map = ThemePark.getInstance().getAPI().getRideCountService().getTopData().getTotal(ThemePark.getInstance().getAPI().getAttractionService().getAttraction(id)).join();

            if (map.size() < position)
                return "";

            UUID key = map.keySet().toArray(UUID[]::new)[position - 1];
            int value = map.get(key);

            return "" + value;
        }

        if (player == null) {
            return "";
        }

        /* Player placeholders */

        //%tp_ridecount%
        if (identifier.equals("ridecount")) {
            int result = 0;
            for (Attraction att : ThemePark.getInstance().getAPI().getAttractionService().getAttractions().values()) {
                result += ThemePark.getInstance().getAPI().getRideCountService().getCount(att.getID(), player.getUniqueId()).getCount();
            }

            return "" + result;
        } else if (identifier.startsWith("ridecount")) {
            String[] args = identifier.split(":");
            if (args.length < 2)
                return "";

            String id = args[1];
            if (!ThemePark.getInstance().getAPI().getAttractionService().hasAttraction(id))
                return "";

            return "" + ThemePark.getInstance().getAPI().getRideCountService().getCount(id, player.getUniqueId()).getCount();
        }

        return null;
    }

}