package fr.etienne.wattebled.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.etienne.wattebled.model.Permission;
import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.model.facteur.Ville;

public class VillesCommand implements CommandExecutor {

	private String listeVilles;
	
	public VillesCommand(TPlotModel tplotModel) {
		StringBuilder sb = new StringBuilder();
		sb.append("===== LISTE DES VILLES =====");
		List<Ville> villes = tplotModel.getVilles();
		for (Ville ville : villes) {
			sb.append("\n");
			sb.append(ville.getId());
			sb.append(": ");
			sb.append(ville.getNom());
		}
		listeVilles = sb.toString();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			sender.sendMessage("Aucun argument n'est attendu !");
			return false;
		}
		sender.sendMessage(listeVilles);
		return true;
	}
}
