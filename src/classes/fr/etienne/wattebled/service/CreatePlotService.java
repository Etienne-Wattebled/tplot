package fr.etienne.wattebled.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

import fr.etienne.wattebled.util.TPlotUtils;

public class CreatePlotService {
	
	public void createPlot(SignChangeEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (!TPlotUtils.isASignForThisPlugin(block)) {
			return;
		}
		if (!player.hasPermission("tplot.create")) {
			block.breakNaturally();
			player.sendMessage("Sorry, but you don't have permission.");
			return;
		}
		Sign sign = (Sign) block.getState();
		
		String l2 = sign.getLine(1);
		String l3 = sign.getLine(2);
		String l4 = sign.getLine(3);
		
		if (StringUtils.isEmpty(l2) || StringUtils.isEmpty(l3) || StringUtils.isEmpty(l4)) {
			block.breakNaturally();
			player.sendMessage("Sorry, check lines !");
			return;
		}
		Pattern p = Pattern.compile("^\\d+;\\d+$");
		Matcher m = p.matcher(l2);
		if (!m.matches()) {
			block.breakNaturally();
			player.sendMessage("Sorry, check ids !");
			return;
		}
		p = Pattern.compile("^\\d+(\\.(\\d)+)?(\\$|€|£)$");
		m = p.matcher(l3);
		if (!m.matches()) {
			block.breakNaturally();
			player.sendMessage("Sorry, check price !");
			return;
		}
		player.sendMessage("The plot is created !");
	}
}
