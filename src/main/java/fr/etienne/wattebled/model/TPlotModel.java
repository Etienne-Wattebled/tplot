package fr.etienne.wattebled.model;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.etienne.wattebled.model.facteur.Type;
import fr.etienne.wattebled.model.facteur.Ville;
import fr.etienne.wattebled.util.TPlotUtils;

public class TPlotModel {
	
	private List<Type> types;
	private List<Permission> permissions;
	private List<Ville> villes;
	private List<Ville.Quartier> quartiers;
	
	// Clé: nom région
	private Map<String,Ville> mapRegionVille;
	private Map<String,Ville.Quartier> mapRegionQuartier;
	
	// Clé: id
	private Map<Integer,Permission> mapIdPermission;
	private Map<Integer,Type> mapIdType;
	
    private GroupManager groupManager;
    
    public TPlotModel() {
    	
    	permissions = new LinkedList<Permission>();
    	quartiers = new LinkedList<Ville.Quartier>();
    	villes = new LinkedList<Ville>();
    	types = new LinkedList<Type>();
    	
    	mapRegionVille = new HashMap<String,Ville>();
    	mapRegionQuartier = new HashMap<String,Ville.Quartier>();
    	
    	mapIdPermission = new HashMap<Integer,Permission>();
    	mapIdType = new HashMap<Integer,Type>();
    	
    	this.groupManager = (GroupManager) Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
    	chargerConfiguration();
    }
    
    public void chargerConfiguration() {
    	File file = new File(TPlotUtils.CONFIG_PATH);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	ConsoleCommandSender console = Bukkit.getConsoleSender();
    	try {
        	if (!file.exists()) {
        		file.getParentFile().mkdirs();
        		file.createNewFile();
        		FileWriter fw = new FileWriter(file);
        		fw.write("<TPlot>");
        		fw.write("\n");
        		fw.write("</TPlot>");
        		fw.flush();
        		fw.close();
        	}
    		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			if ("TPlot".equals(doc.getDocumentElement())) {
				throw new Exception("La balise principale doit être <TPlot>");
			}
			chargerTypes(doc);
			chargerPermissions(doc);
			chargerVilles(doc);
			// chargerQuartier est appelé par chargerVilles
			
    	} catch (Exception e) {
			console.sendMessage("Une erreur lors du chargement de la configuration a eu lieu.");
			console.sendMessage(e.getMessage());
		}
    }
    
    public void chargerPermissions(Document doc) throws Exception {
    	NodeList permissionsRoot = doc.getElementsByTagName("permissions");
		if (permissionsRoot != null && permissionsRoot.getLength() > 1) {
			throw new Exception("Il ne doit y avoir qu'une seule balise <permissions>");
		}
		NodeList permissions = permissionsRoot.item(0).getChildNodes();
		int nbPermissions = permissions.getLength();
		
		Integer idPermission;
		String nom;
		Permission permission;
		
		for (int i = 0;i<nbPermissions;i++) {
			Node nodePermission = permissions.item(i);
			nom = null;
			if (("permission".equals(nodePermission.getNodeName()) && (Node.ELEMENT_NODE == nodePermission.getNodeType()))) {
				Element elementPermission = (Element) nodePermission;
				idPermission = Integer.parseInt(elementPermission.getAttribute("id"));
				nom = elementPermission.getTextContent();
				permission = new Permission(idPermission,nom);
				this.permissions.add(permission);
				this.mapIdPermission.put(idPermission,permission);
			}
		}
    }
    
    public void chargerVilles(Document doc) throws Exception {
    	NodeList villesRoot = doc.getElementsByTagName("villes");
    	
    	if (villesRoot == null || villesRoot.getLength() != 1) {
			throw new Exception("Il ne doit y avoir qu'une seule balise <villes>. La balise <villes> est obligatoire.");
    	}
    	
    	NodeList villes = villesRoot.item(0).getChildNodes();
    	int nbVilles = villes.getLength();
    	
    	Integer idVille;
    	String nom = null;
    	Ville ville = null;
    	double poids = 0;
    	NodeList nodeList;
    	String region = null;
    	
    	for (int i = 0;i<nbVilles;i++) {
    		Node nodeVille = villes.item(i);
    		ville = null;
    		if (("ville").equals(nodeVille.getNodeName()) && (Node.ELEMENT_NODE == nodeVille.getNodeType())) {
    			Element elementVille = (Element) nodeVille;
    			idVille = Integer.parseInt(elementVille.getAttribute("id"));
    			nodeList = elementVille.getElementsByTagName("nom");
    			if (nodeList != null && nodeList.getLength() > 0) {
					nom = nodeList.item(0).getTextContent();
				}
				nodeList = elementVille.getElementsByTagName("poids");
				if (nodeList != null && nodeList.getLength() > 0) {
					poids = Double.parseDouble(nodeList.item(0).getTextContent());
				}
				nodeList = elementVille.getElementsByTagName("région");
				if (nodeList != null && nodeList.getLength() > 0) {
					region = nodeList.item(0).getTextContent();
				}
				ville = new Ville(idVille, nom, poids, region);
				this.mapRegionVille.put(region,ville);
				this.villes.add(ville);
				nodeList = elementVille.getElementsByTagName("quartiers");
				if (nodeList != null && nodeList.getLength() > 0) {
					chargerQuartier(ville,nodeList.item(0).getChildNodes());
				}
    		}
    	}
    }
    
    public void chargerQuartier(Ville ville, NodeList quartiers) {
    	int nbQuartiers = quartiers.getLength();
    	
    	NodeList nodeList;
    	int idQuartier = 0;
    	String nom = null;
    	String region = null;
    	double poids = 0;
    	Ville.Quartier quartier = null;
    	
    	if (nbQuartiers > 0) {
    		for (int i = 0;i<nbQuartiers;i++) {
    			Node nodeQuartier = quartiers.item(i);
    			if ("quartier".equals(nodeQuartier.getNodeName()) && (Node.ELEMENT_NODE == nodeQuartier.getNodeType())) {
    				Element elementQuartier = (Element) nodeQuartier;
    				idQuartier = Integer.parseInt(elementQuartier.getAttribute("id"));
    				nodeList = elementQuartier.getElementsByTagName("nom");
    				if (nodeList != null && nodeList.getLength() > 0) {
    					nom = nodeList.item(0).getTextContent();
    				}
    				nodeList = elementQuartier.getElementsByTagName("région");
    				if (nodeList != null && nodeList.getLength() > 0) {
    					region = nodeList.item(0).getTextContent();
    				}
    				nodeList = elementQuartier.getElementsByTagName("poids");
    				if (nodeList != null && nodeList.getLength() > 0) {
    					poids = Double.parseDouble(nodeList.item(0).getTextContent());
    				}
    				quartier = ville.new Quartier(idQuartier, nom, poids, region);
    				this.mapRegionQuartier.put(region,quartier);
    				this.quartiers.add(quartier);
    			}
    		}
    	}
    }
    
    public void chargerTypes(Document doc) throws Exception {
		
		NodeList typesRoot = doc.getElementsByTagName("types");
		if (typesRoot == null || typesRoot.getLength() != 1) {
			throw new Exception("Il ne doit y avoir qu'une seule balise <types>. La balise <types> est obligatoire.");
		}
		NodeList types = typesRoot.item(0).getChildNodes();
		int nbTypes = types.getLength();
		
		Integer idType;
		String nom = null;
		Type type;
		double poids = 0;
		NodeList nodeList;
		
		for (int i =0;i<nbTypes;i++) {
			Node nodeType = types.item(i);
			type = null;
			if (("type".equals(nodeType.getNodeName())) && (Node.ELEMENT_NODE == nodeType.getNodeType())) {
				Element elementType = (Element) nodeType;
				idType = Integer.parseInt(elementType.getAttribute("id"));
				nodeList = elementType.getElementsByTagName("nom");
				if (nodeList != null && nodeList.getLength() > 0) {
					nom = nodeList.item(0).getTextContent();
				}
				nodeList = elementType.getElementsByTagName("poids");
				if (nodeList != null && nodeList.getLength() > 0) {
					poids = Double.parseDouble(nodeList.item(0).getTextContent());
				}
				type = new Type(idType, nom, poids);
				this.mapIdType.put(idType,type);
				this.types.add(type);
			}
		}
    }
    
    public boolean peutAcheter(String groupePermission, Player player) {
    	if (StringUtils.isEmpty(groupePermission)) {
    		return true;
    	}
    	
    	WorldsHolder worldsHolder = groupManager.getWorldsHolder();
    	AnjoPermissionsHandler anjoPermissionsHandler = worldsHolder.getWorldPermissions(player);
    	OverloadedWorldHolder overloadedWorldHolder = worldsHolder.getWorldData(player);
    	
    	String gJ = anjoPermissionsHandler.getGroup(player.getName());
    	Group groupeJoueur = overloadedWorldHolder.getGroup(gJ);
    	
	    if (groupePermission != null) {
	    	if (!anjoPermissionsHandler.hasGroupInInheritance(groupeJoueur,groupePermission)) {
	    		return false;
	    	}
	    }
    	return true;
    }
    
    public List<Permission> getPermissions() {
    	return permissions;
    }
    
    public Type getType(int type) {
    	return mapIdType.get(type);
    }
    
    public Ville getVille(String nom) {
    	return mapRegionVille.get(nom);
    }
    
    public Ville.Quartier getQuartier(String nom) {
    	return mapRegionQuartier.get(nom);
    }
    
    public List<Ville.Quartier> getQuartiers() {
    	return quartiers;
    }
    
    public List<Type> getTypes() {
    	return types;
    }
    
    public Permission getPermission(int idType) {
    	return mapIdPermission.get(idType);
    }
    public List<Ville> getVilles() {
    	return villes;
    }
}
