package fr.etienne.wattebled.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.service.CreatePlotService;

public class CreatePlotListener implements Listener {
	
	private CreatePlotService createPlotService;
	
	public CreatePlotListener(TPlotModel tplotModel) {
		createPlotService = new CreatePlotService(tplotModel);
	}
	
	// Create a plot
	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		createPlotService.createPlot(event);
	}
}
