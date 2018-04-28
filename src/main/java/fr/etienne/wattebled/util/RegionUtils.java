package fr.etienne.wattebled.util;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

import fr.etienne.wattebled.model.Element;
import fr.etienne.wattebled.model.TPlotModel;
import fr.etienne.wattebled.model.facteur.Ville;

public class RegionUtils {

	public static Element[] getVilleQuartier(TPlotModel tplotModel, ProtectedRegion region, Block block, Player player, boolean creation) {
		ProtectedRegion regionCourante = region;
		
		Ville.Quartier quartier = null;
		Ville ville = null;
		while (((regionCourante = regionCourante.getParent()) != null) && (ville == null || quartier == null)) {
			if (quartier == null) {
				quartier = tplotModel.getQuartier(regionCourante.getId());
			}
			if (ville == null) {
				ville = tplotModel.getVille(regionCourante.getId());
			}
		}
		
		if (ville == null) {
			if (creation) {
				block.breakNaturally();
				player.sendMessage(Message.ERREUR_PAS_PARENT_VILLE);
			} else {
				block.setType(Material.GLOWSTONE);
				player.sendMessage(Message.ERREUR_TPLOT_BUY);
			}
			return null;
		}
		
		if (quartier == null) {
			if (creation) {
				block.breakNaturally();
				player.sendMessage(Message.ERREUR_PAS_PARENT_QUARTIER);

			} else {
				block.setType(Material.GLOWSTONE);
				player.sendMessage((Message.ERREUR_TPLOT_BUY));
			}
			return null;
		}
		
		if (quartier.getVille() != ville) {
			if (creation) {
				block.breakNaturally();
				player.sendMessage(Message.ERREUR_COHERENCE_VILLE_QUARTIER);
			} else {
				block.setType(Material.GLOWSTONE);
				player.sendMessage(Message.ERREUR_TPLOT_BUY);
			}
			return null;
		}
		return new Element[] { ville, quartier};
	}
	
	public static ProtectedRegion renommerRegion(RegionManager regionManager, ProtectedRegion oldRegion, String nom) {
		ProtectedRegion newRegion = null;
		
		if (oldRegion instanceof ProtectedCuboidRegion) {
			newRegion = new ProtectedCuboidRegion(nom,oldRegion.getMinimumPoint(),oldRegion.getMaximumPoint());
		} else {
			newRegion = new ProtectedPolygonalRegion(nom,oldRegion.getPoints(),oldRegion.getMinimumPoint().getBlockY(),oldRegion.getMaximumPoint().getBlockY());
		}
		
		newRegion.copyFrom(oldRegion);
		
		regionManager.addRegion(newRegion);
		
		Map<String,ProtectedRegion> allRegions = regionManager.getRegions();
		ProtectedRegion pr = null;
		for (Map.Entry<String,ProtectedRegion> e : allRegions.entrySet()) {
			pr = e.getValue();
			if (pr.getParent() == oldRegion) {
				try {
					pr.setParent(newRegion);
				} catch (CircularInheritanceException e1) {
				}
			}
		}
		
		regionManager.removeRegion(oldRegion.getId());
		
		try {
			regionManager.saveChanges();
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
		return newRegion;
	}
	
}
