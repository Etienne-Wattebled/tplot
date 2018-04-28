package fr.etienne.wattebled.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.etienne.wattebled.service.CreatePlotService;

public class CreatePlotListener implements Listener {
	
	private CreatePlotService createPlotService;
	
	public CreatePlotListener() {
		createPlotService = new CreatePlotService();
	}
	
	// Create a plot
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		createPlotService.createPlot(event);
	}
}
