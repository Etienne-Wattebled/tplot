package fr.etienne.wattebled.service;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.earth2me.essentials.register.payment.Method.MethodAccount;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;

import fr.etienne.wattebled.util.TPlotUtils;

public class BuyPlotService {
	
	public void buyPlot(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!TPlotUtils.isASignForThisPlugin(block)) {
			return;
		}

		Action action = event.getAction();
		if (!Action.RIGHT_CLICK_BLOCK.equals(action)) {
			return;
		}
		if (!event.getPlayer().hasPermission("tplot.buy")) {
			player.sendMessage("Sorry, but you don't have permission.");
			return;
		}
		Sign sign = (Sign) block.getState();
		
		String ids[] = sign.getLine(1).split(";");
		
		String priceS = sign.getLine(2);
		double price = Double.parseDouble(priceS.substring(0, priceS.length() - 2));

		String oldRegion = sign.getLine(3);
		String newRegion = new StringBuilder().append(event.getPlayer().getName()).append("_").append(ids[0])
				.append("_").append(ids[1]).toString();

		RegionManager regionManager = TPlotUtils.worldguard.getRegionManager(event.getPlayer().getWorld());

		ProtectedRegion oldRG = regionManager.getRegion(oldRegion);
		ProtectedRegion newRG = regionManager.getRegion(newRegion);

		if (newRG != null) {
			player.sendMessage("Sorry, you aready have this type of plot in this city.");
			return;
		}

		MethodAccount account = TPlotUtils.essentials.getPaymentMethod().getMethod().getAccount(player.getName());

		if (!account.hasEnough(price)) {
			player.sendMessage("Sorry, you need more money to buy this plot !");
			return;
		}

		account.subtract(price);
		
		if (RegionType.CUBOID.equals(oldRG)) {
			newRG = new ProtectedCuboidRegion(newRegion,oldRG.getMinimumPoint(),oldRG.getMaximumPoint());
		} else {
			newRG = new ProtectedPolygonalRegion(newRegion,oldRG.getPoints(),oldRG.getMinimumPoint().getBlockY(),oldRG.getMaximumPoint().getBlockY());
		}
		
		newRG.copyFrom(oldRG);
		
		regionManager.addRegion(newRG);
		
		regionManager.removeRegion(oldRegion);

		try {
			regionManager.saveChanges();
		} catch (StorageException e) {
			e.printStackTrace();
		}

		player.sendMessage("This plot is now to you !");
	}
}
