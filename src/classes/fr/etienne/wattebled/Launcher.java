package fr.etienne.wattebled; 

import java.io.Console;

import javax.swing.event.DocumentEvent.EventType;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.etienne.wattebled.controller.BuyPlotListener;
import fr.etienne.wattebled.controller.CreatePlotListener;

public class Launcher extends JavaPlugin {
	private Listener buyPlotListener;
	private Listener createPlotListener;
	
    private ConsoleCommandSender console;
    
    public Launcher() {
    	this.console = Bukkit.getConsoleSender();
    	
    	this.buyPlotListener = new BuyPlotListener();
    	this.createPlotListener = new CreatePlotListener();
    	
    	PluginManager pluginManager = Bukkit.getServer().getPluginManager();
    	pluginManager.registerEvents(this.buyPlotListener,this);
    	pluginManager.registerEvents(this.createPlotListener,this);
    }
    
	@Override
    public void onEnable(){
		console.sendMessage("TPlot est actif.");
	}
	
	@Override
	public void onDisable() {
		console.sendMessage("TPlot n'est plus actif.");
	}
}
