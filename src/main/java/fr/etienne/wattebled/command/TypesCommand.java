package fr.etienne.wattebled.command;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.etienne.wattebled.model.Permission;
import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.model.facteur.Type;
import fr.etienne.wattebled.model.facteur.Ville;

public class TypesCommand implements CommandExecutor {

	private String listeTypes;
	
	public TypesCommand(TPlotModel tplotModel) {
		StringBuilder sb = new StringBuilder();
		Ville ville = null;
		sb.append("===== LISTE DES TYPES =====");
		List<Type> types = tplotModel.getTypes();
		for (Type type : types) {
			sb.append("\n");
			sb.append(type.getId());
			sb.append(": ");
			sb.append(type.getNom());
		}
		listeTypes = sb.toString();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			sender.sendMessage("Aucun argument n'est attendu !");
			return false;
		}
		sender.sendMessage(listeTypes);
		return true;
	}
}
