package me.pancaketaste.dodgebolt;

import me.pancaketaste.dodgebolt.arena.Arena;
import me.pancaketaste.dodgebolt.utils.LocationUtils;
import org.bukkit.Location;

import java.sql.*;

public class Database {
    private final Dodgebolt dodgeboltPlugin;
    private static Connection connection = null;

    public Database(Dodgebolt dodgeboltPlugin, String path) throws SQLException {
        this.dodgeboltPlugin = dodgeboltPlugin;

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS arenas (
                    name TEXT UNIQUE,
                    worldName TEXT,
                    blueSpawn TEXT,
                    redSpawn TEXT,
                    PRIMARY KEY(name)
                );
            """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Arena
    public void insertArena(String name, String worldName) {
        String sql = "INSERT INTO arenas (name, worldName) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, worldName);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteArena(String name) {
        String sql = "DELETE FROM arenas WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateArenaSpawns(String name, Location blueSpawn, Location redSpawn) {
        String sql = "UPDATE arenas SET blueSpawn = ?, redSpawn = ? WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, LocationUtils.locationToString(blueSpawn));
            preparedStatement.setString(2, LocationUtils.locationToString(redSpawn));
            preparedStatement.setString(3, name);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // This function load all arenas from the database into the game
    public void loadArenas() {
        String sql = "SELECT * FROM arenas";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String arenaName = resultSet.getString("name");
                String worldName = resultSet.getString("worldName");
                String blueSpawn = resultSet.getString("blueSpawn");
                String redSpawn = resultSet.getString("redSpawn");

                Arena arena = new Arena(dodgeboltPlugin, arenaName, worldName);
                arena.setBlueSpawn(LocationUtils.stringToLocation(blueSpawn));
                arena.setRedSpawn(LocationUtils.stringToLocation(redSpawn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
