package fr.etienne.wattebled.service;

import java.util.Date;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

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

import net.milkbowl.vault.economy.Economy;

public class BuyPlotService {
	
	private TPlotModel tplotModel;
	
	public BuyPlotService(TPlotModel tplotModel) {
		this.tplotModel = tplotModel;
	}
	
	public void buyPlot(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		if (block == null) {
			return;
		}
		
		Material typeMaterial = block.getType();
		Player player = event.getPlayer();
		
		if (typeMaterial == null || player == null) {
			return;
		}
		
		boolean isASign = (Material.WALL_SIGN.equals(typeMaterial) || Material.SIGN_POST.equals(typeMaterial) || Material.SIGN.equals(typeMaterial));
		
		if (!isASign) {
			return;
		}
		

		Action action = event.getAction();
		
		if (!Action.RIGHT_CLICK_BLOCK.equals(action)) {
			return;
		}
		
		Sign sign = (Sign) block.getState();
		
		String ligne1 = sign.getLine(0);
		String ligne2 = sign.getLine(1);
		String ligne3 = sign.getLine(2);
		String ligne4 = sign.getLine(3);
		
		if (sign == null || ligne1 == null) {
			return;
		}
		
		Matcher matcherLigne1 = TPlotUtils.SIGN_FIRST_LINE_PATTERN.matcher(ligne1);
		
		if (!matcherLigne1.matches()) {
			return;
		}
		
		if (!player.hasPermission("tplot.acheter")) {
			player.sendMessage(Message.ERREUR_AUTORISATION_ACHETER);
			return;
		}
		
		String nomRegion = matcherLigne1.group(3);
		
		if (StringUtils.isEmpty(nomRegion)) {
			erreur(block,player);
			return;
		}
		
		RegionManager regionManager = TPlotUtils.worldguard.getRegionManager(player.getWorld());
		ProtectedRegion oldRg = regionManager.getRegion(nomRegion);
		
		if (oldRg == null) {
			erreur(block,player);
			return;
		}
		
		// Type
		Matcher matcherType = TPlotUtils.STRING_ID_PATTERN.matcher(ligne2);
		if (!matcherType.matches()) {
			erreur(block,player);
			return;
		}

		int idType = Integer.parseInt(matcherType.group(2));
		Type type = tplotModel.getType(idType);
		if (type == null) {
			erreur(block,player);
			return;
		}
				
		// Permission
		Matcher matcherPermission = TPlotUtils.STRING_ID_PATTERN.matcher(ligne3);
		if (!matcherPermission.matches()) {
			erreur(block,player);
			return;
		}
		
		int idPermission = Integer.parseInt(matcherPermission.group(2));
		Permission permission = tplotModel.getPermission(idPermission);
		if (permission == null) {
			erreur(block,player);
			return;
		}
		
		// Prix
		Matcher matcherPrix = TPlotUtils.PRICE_PATTERN.matcher(ligne4);
		if (!matcherPrix.matches()) {
			erreur(block,player);
			return;
		}
		double prix = Double.parseDouble(matcherPrix.group(1));
				
		Element villeQuartier[] = RegionUtils.getVilleQuartier(tplotModel,oldRg, block, player,false);
		
		if (villeQuartier == null) {
			return;
		}
		
		Ville ville = (Ville) villeQuartier[0];
		
		if (!tplotModel.peutAcheter(permission.getNom(), player)) {
			player.sendMessage(Message.ERREUR_GRADE_INSUFFISANT_BUY);
			return;
		}
		
		
		String newRegion = new StringBuilder().append(player.getName()).append("_").append(ville.getId())
				.append("_").append(type.getId()).toString();
		
		ProtectedRegion newRG = regionManager.getRegion(newRegion);
		
		if (newRG != null) {
			player.sendMessage(Message.ERREUR_DEJA_TPLOT_NOM);
			return;
		}
		
		Server server = Bukkit.getServer();
		
		RegisteredServiceProvider<Economy> economyProvider = server.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		Economy economy = null;
		
	    if (economyProvider != null) {
	    	economy = economyProvider.getProvider();
	    }
	    
	    if (economy == null) {
	    	player.sendMessage(Message.ERREUR_ACCES_ARGENT);
	    	return;
	    }
	    
	    OfflinePlayer offlinePlayer = server.getOfflinePlayer(player.getUniqueId());
	    if (economy.has(offlinePlayer,prix)) {
	    	economy.withdrawPlayer(offlinePlayer,prix);
	    } else {
			player.sendMessage(Message.ERREUR_MANQUE_ARGENT);
			return;
	    }
	    
	    newRG = RegionUtils.renommerRegion(regionManager,oldRg,newRegion);
	    newRG.getMembers().addPlayer(player.getName());
	    
		if ("!".equals(matcherLigne1.group(1))) {
			sign.setLine(0,type.getNom());
			sign.setLine(1,player.getName());
			sign.setLine(2,TPlotUtils.dateFormat.format(new Date()));
			sign.setLine(3,"");
			sign.update();
		} else {
			block.breakNaturally();
		}
		
		player.sendMessage(Message.TPLOT_A_VOUS);
	}
	
	private void erreur(Block block, Player player) {
		block.setType(Material.GLOWSTONE);
		player.sendMessage(Message.ERREUR_TPLOT_BUY);
	}
}
