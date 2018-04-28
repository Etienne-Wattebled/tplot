package fr.etienne.wattebled.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.etienne.wattebled.service.BuyPlotService;

public class BuyPlotListener implements Listener {
	
	private BuyPlotService buyPlotService;
	
	public BuyPlotListener() {
		this.buyPlotService = new BuyPlotService();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		this.buyPlotService.buyPlot(event);
	}
}
