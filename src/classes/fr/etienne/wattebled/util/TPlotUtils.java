package fr.etienne.wattebled.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class TPlotUtils {
	public static String firstLign = "[TPlot]";
	public static Server server;
	public static PluginManager pluginManager;
	
	// Dependencies
	public static WorldGuardPlugin worldguard;
	public static Essentials essentials;
	
	static {
		server = Bukkit.getServer();
		pluginManager = server.getPluginManager();
		worldguard = (WorldGuardPlugin) pluginManager.getPlugin("WorldGuard");
		essentials = (Essentials) pluginManager.getPlugin("Essentials");
	}
	
	public static boolean isASignForThisPlugin(Block block) {
		Material type = block.getType();
		if (!Material.SIGN.equals(type)) {
			return false;
		}
		Sign sign = (Sign) block.getState();
		if (!firstLign.equals(sign.getLine(0))) {
			return false;
		}
		return true;
	}
}
