/*
    ThemePark Expansion - Provides PlaceholderAPI placeholders for ThemePark
    Copyright (C) 2020 SBDeveloper
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.sbdeveloper.themeparkexpansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.iobyte.themepark.api.API;
import nl.iobyte.themepark.api.attraction.Attraction;
import nl.iobyte.themepark.api.attraction.component.Status;
import nl.iobyte.themepark.api.attraction.manager.StatusManager;
import nl.iobyte.themepark.ridecount.RideCountAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

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
        return "1.4.1";
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin());
    }

    private String color(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        if(identifier == null)
            return "";

        /* Non-player placeholders */

        //%tp_status:ID%
        if(identifier.startsWith("status")) {
            String[] args = identifier.split(":");
            if(args.length < 2)
                return "";

            String id = args[1];
            if(!API.isAttraction(id))
                return "";

            Status status = API.getAttraction(id).getStatus();
            return (StatusManager.getName(status));
        }

        //%tp_name:ID%
        if(identifier.startsWith("name")) {
            String[] args = identifier.split(":");
            if(args.length < 2)
                return "";

            String id = args[1];
            if(!API.isAttraction(id))
                return "";

            return color(API.getAttraction(id).getName());
        }

        if (player == null) {
            return "";
        }

        /* Player placeholders */

        //%tp_ridecount%
        if (identifier.equals("ridecount")) {
            CompletableFuture<Integer> result = CompletableFuture.completedFuture(0);
            for (Attraction att : API.getAttractions().values()) {
                result = result.thenCombine(RideCountAPI.getCount(player.getUniqueId(), att), Integer::sum);
            }

            return "" + result.join();
        } else if (identifier.startsWith("ridecount")) {
            String[] args = identifier.split(":");
            if(args.length < 2)
                return "";

            String id = args[1];
            if(!API.isAttraction(id))
                return "";

            return "" + RideCountAPI.getCount(player.getUniqueId(), API.getAttraction(id));
        }

        return null;
    }

}