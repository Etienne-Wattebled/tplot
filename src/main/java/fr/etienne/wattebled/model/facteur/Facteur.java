package fr.etienne.wattebled.model.facteur;

import fr.etienne.wattebled.model.Element;

public abstract class Facteur extends Element {
	
	private double poids;

	public Facteur(int id, String nom, double poids) {
		super(id, nom);
		this.poids = poids;
	}
	
	public double getPoids() {
		return poids;
	}

	public void setPoids(double poids) {
		this.poids = poids;
	}
	
}
