package fr.etienne.wattebled.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.etienne.wattebled.util.RegionUtils;
import fr.etienne.wattebled.util.TPlotUtils;

public class RenommerRegionCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 2) {
			sender.sendMessage("Commande : /renommer <ancienNom> <nouveauNom>");
			sender.sendMessage("2 arguments sont donc attendus !");
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Cette commande ne peut être saisie que par un joueur !");
			return false;
		}
		
		Player player = (Player) sender;
		RegionManager regionManager = TPlotUtils.worldguard.getRegionManager(player.getWorld());
		
		ProtectedRegion oldRegion = regionManager.getRegion(args[0]);
		
		if (oldRegion == null) {
			sender.sendMessage("Cette région n'existe pas, il est donc impossible de la renommer !");
			return false;
		}
		
		RegionUtils.renommerRegion(regionManager, oldRegion,args[1]);
		
		sender.sendMessage("La région a bien été renommée !");
		
		return true;
	}

}
