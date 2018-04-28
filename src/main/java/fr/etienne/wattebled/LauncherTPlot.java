package fr.etienne.wattebled; 

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import fr.etienne.wattebled.command.PermissionsCommand;
import fr.etienne.wattebled.command.QuartiersCommand;
import fr.etienne.wattebled.command.RenommerRegionCommand;
import fr.etienne.wattebled.command.TypesCommand;
import fr.etienne.wattebled.command.VillesCommand;
import fr.etienne.wattebled.controller.BuyPlotListener;
import fr.etienne.wattebled.controller.CreatePlotListener;
import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.util.TPlotUtils;

public class LauncherTPlot extends JavaPlugin {
	
    private ConsoleCommandSender console;
    
    public LauncherTPlot() {
    	this.console = Bukkit.getConsoleSender();
    }
    
	@Override
    public void onEnable() {
		TPlotModel tPlotModel = new TPlotModel();
    	TPlotUtils.pluginManager.registerEvents(new CreatePlotListener(tPlotModel),this);
    	TPlotUtils.pluginManager.registerEvents(new BuyPlotListener(tPlotModel),this);
    	getCommand("renommer").setExecutor(new RenommerRegionCommand());
    	getCommand("permissions").setExecutor(new PermissionsCommand(tPlotModel));
    	getCommand("quartiers").setExecutor(new QuartiersCommand(tPlotModel));
    	getCommand("types").setExecutor(new TypesCommand(tPlotModel));
    	getCommand("villes").setExecutor(new VillesCommand(tPlotModel));
		console.sendMessage("TPlot est actif.");
	}
	
	@Override
	public void onDisable() {
		console.sendMessage("TPlot n'est plus actif.");
	}
}
