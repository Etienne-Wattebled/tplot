package fr.etienne.wattebled.util;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.sk89q.worldguard.protection.regions.RegionType;

import java.text.SimpleDateFormat;

public class TPlotUtils {
	
	public static final String CONFIG_PATH = "./plugins/TPlot/config.xml";
	
	public static final PluginManager pluginManager;
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.FRENCH);
	
	// Dependencies
	public static final WorldGuardPlugin worldguard;
	// And Vault but useless here
	
	public static final Pattern PRICE_PATTERN = Pattern.compile("^(\\d+(\\.\\d+)?)\\$$");
	public static final Pattern SIGN_FIRST_LINE_PATTERN = Pattern.compile("^\\[(!?)TPlot(=(.+))?\\]$");
	public static final Pattern STRING_ID_PATTERN = Pattern.compile("^([A-Za-z0-9]*) \\/ (\\d+)$");
	public static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9]*$");
	public static final String STRING_ID_FORMAT = "%s / %d";
	public static final String PRICE_FORMAT = "%s$";
	static {
		pluginManager = Bukkit.getServer().getPluginManager();
		worldguard = (WorldGuardPlugin) pluginManager.getPlugin("WorldGuard");
	}
	
	public static double calculer(double poidsVille, double poidsQuartier, double poidsType, int nbBlocs) {
		return (nbBlocs * poidsVille + poidsType) * poidsQuartier;
	}
}
