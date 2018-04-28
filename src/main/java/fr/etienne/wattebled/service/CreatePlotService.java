package fr.etienne.wattebled.service;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import com.avaje.ebean.text.StringFormatter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.etienne.wattebled.model.Element;
import fr.etienne.wattebled.model.Permission;
import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.model.facteur.Type;
import fr.etienne.wattebled.model.facteur.Ville;
import fr.etienne.wattebled.model.facteur.Ville.Quartier;
import fr.etienne.wattebled.util.Message;
import fr.etienne.wattebled.util.RegionUtils;
import fr.etienne.wattebled.util.TPlotUtils;

public class CreatePlotService {
	
	private TPlotModel tPlotModel;
	
	public CreatePlotService (TPlotModel tPlotModel) {
		this.tPlotModel = tPlotModel;
	}
	
	public void createPlot(SignChangeEvent event) {
		String ligne1 = event.getLine(0);
		String ligne2 = event.getLine(1);
		String ligne3 = event.getLine(2);
		String ligne4 = event.getLine(3);
		
		Matcher matcherLigne1 = TPlotUtils.SIGN_FIRST_LINE_PATTERN.matcher(ligne1);
		
		if (!matcherLigne1.matches()) {
			return;
		}
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if (!player.hasPermission("tplot.creer")) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_PERMISSION);
			return;
		}
		
		String nomRegion = matcherLigne1.group(3);	
		int idType = 0;
		int idPermission = 0;
		
		if (StringUtils.isEmpty(nomRegion)) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_REGION_OBLIGATOIRE);
			return;
		}
		
		Matcher matcherNomRegion = TPlotUtils.ALPHANUMERIC_PATTERN.matcher(nomRegion);
		
		if (!matcherNomRegion.matches()) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_REGION_FORMAT);
			return;
		}
		
		RegionManager regionManager = TPlotUtils.worldguard.getRegionManager(player.getWorld());
		ProtectedRegion rg = regionManager.getRegion(nomRegion);
		
		if (rg == null) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_REGION_INEXISTANTE);
			return;
		}

		if (StringUtils.isNotEmpty(ligne2)) {
			if (StringUtils.isNumeric(ligne2)) {
				idType = Integer.parseInt(ligne2);
			} else {
				block.breakNaturally();
				player.sendMessage(Message.ERREUR_TYPE_FORMAT);
				return;
			}
		}
		
		if (StringUtils.isNotEmpty(ligne3)) {
			if (StringUtils.isNumeric(ligne3)) {
				idPermission = Integer.parseInt(ligne3);
			} else {
				block.breakNaturally();
				player.sendMessage(Message.ERREUR_PERMISSION_FORMAT);
				return;	
			}
		}
		
		Element[] villeQuartier = RegionUtils.getVilleQuartier(tPlotModel, rg, block, player,true);
		
		if (villeQuartier == null) {
			return;
		}
		
		Ville ville = (Ville) villeQuartier[0];
		Quartier quartier = (Quartier) villeQuartier[1];
		
		Type type = tPlotModel.getType(idType);
		Permission permission = tPlotModel.getPermission(idPermission);
		Matcher matcherPrix = TPlotUtils.PRICE_PATTERN.matcher(ligne4);
		int nbBlocs = (int) (rg.volume() / (rg.getMaximumPoint().getY()-rg.getMinimumPoint().getY()+1));
		
		if (type == null) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_TYPE_INEXISTANT);
			return;
		}
		
		if (permission == null ) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_PERMISSION_INEXISTANTE);
			block.breakNaturally();
		}
		
		if (StringUtils.isEmpty(ligne4)) {
			double prix = TPlotUtils.calculer(ville.getPoids(),quartier.getPoids(),type.getPoids(),nbBlocs);
			event.setLine(3,String.format(TPlotUtils.PRICE_FORMAT,prix));
			
		} else if (!matcherPrix.matches()) {
			block.breakNaturally();
			player.sendMessage(Message.ERREUR_PRIX_FORMAT);
			return;
		}
		
		event.setLine(1,String.format(TPlotUtils.STRING_ID_FORMAT, type.getNom(), type.getId()));
		event.setLine(2,String.format(TPlotUtils.STRING_ID_FORMAT, permission.getNom(), permission.getId()));
		
		player.sendMessage(Message.TPLOT_CREEE);
	}
}
