package fr.etienne.wattebled.model.facteur;

public abstract class Region extends Facteur {
	private String nomRegion;
	
	public Region(int id, String nom, double poids, String nomRegion) {
		super(id, nom, poids);
		this.nomRegion = nomRegion;
	}
	
	public void setNomRegion(String nomRegion) {
		this.nomRegion = nomRegion;
	}
	
	public String getNomRegion() {
		return nomRegion;
	}
}
