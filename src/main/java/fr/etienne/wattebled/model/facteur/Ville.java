package fr.etienne.wattebled.model.facteur;

public class Ville extends Region {
	
	public Ville(int id, String nom, double poids, String nomRegion) {
		super(id, nom, poids, nomRegion);
	}

	public class Quartier extends Region {
		
		public Quartier(int id, String nom, double poids, String region) {
			super(id, nom, poids, region);
		}
		
		public Ville getVille() {
			return Ville.this;
		}
	}
}
