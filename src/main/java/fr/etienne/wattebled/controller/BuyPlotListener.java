package fr.etienne.wattebled.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.service.BuyPlotService;

public class BuyPlotListener implements Listener {
	
	private BuyPlotService buyPlotService;
	
	public BuyPlotListener(TPlotModel tPlotModel) {
		buyPlotService = new BuyPlotService(tPlotModel);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onInteract(PlayerInteractEvent event) {
		buyPlotService.buyPlot(event);
	}
}
