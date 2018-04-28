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

public class QuartiersCommand implements CommandExecutor {

	private String listeQuartiers;
	
	public QuartiersCommand(TPlotModel tplotModel) {
		StringBuilder sb = new StringBuilder();
		Ville ville = null;
		sb.append("===== LISTE DES QUARTIERS =====");
		List<Ville.Quartier> quartiers = tplotModel.getQuartiers();
		for (Ville.Quartier quartier : quartiers) {
			sb.append("\n");
			sb.append(quartier.getNom());
			sb.append(" (");
			ville = quartier.getVille();
			if (ville != null) {
				sb.append(ville.getNom());
			}
			sb.append(")");
			sb.append(" / ");
			sb.append(quartier.getNomRegion());
			sb.append(" ");
			sb.append(ville.getNomRegion());
		}
		listeQuartiers = sb.toString();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			sender.sendMessage("Aucun argument n'est attendu !");
			return false;
		}
		sender.sendMessage(listeQuartiers);
		return true;
	}
}
