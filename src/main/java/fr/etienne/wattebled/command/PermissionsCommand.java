package fr.etienne.wattebled.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.etienne.wattebled.model.Permission;
import fr.etienne.wattebled.model.TPlotModel;

public class PermissionsCommand implements CommandExecutor {

	private String listePermissions;
	
	public PermissionsCommand(TPlotModel tplotModel) {
		StringBuilder sb = new StringBuilder();
		sb.append("===== LISTE DES PERMISSIONS =====");
		List<Permission> permissions = tplotModel.getPermissions();
		for (Permission permission : permissions) {
			sb.append("\n");
			sb.append(permission.getId());
			sb.append(": ");
			sb.append(permission.getNom());
		}
		listePermissions = sb.toString();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			sender.sendMessage("Aucun argument n'est attendu !");
			return false;
		}
		sender.sendMessage(listePermissions);
		return true;
	}
}
