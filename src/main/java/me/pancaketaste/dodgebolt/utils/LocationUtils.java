package me.pancaketaste.dodgebolt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    // Convert a Location to a String
    public static String locationToString(Location location) {
        if (location == null) {
            return null;
        }
        return String.format("%s;%f;%f;%f;%f;%f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    // Convert a String back to a Location
    public static Location stringToLocation(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return null;
        }

        String[] parts = locationString.split(";");
        if (parts.length != 6) {
            return null; // Invalid format
        }

        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null; // Invalid world name
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}
