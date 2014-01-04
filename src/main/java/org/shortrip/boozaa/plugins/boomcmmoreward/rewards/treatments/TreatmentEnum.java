package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments;

public enum TreatmentEnum {

	// Il faut appeler l'un des constructeurs déclarés :
    COMMAND("command"),
    GROUP("group"),
    ITEM("item"),
    MESSAGE("message"),
    MONEY("money"),
    PERM("perm"),
    POWER("power"),
    SKILL("skill"),
    WORLD("world"); 
 
    // Membres :
    private final String nom;
 
    TreatmentEnum(String nom)
    { 
    	this.nom = nom; 
    }
 
 
    public String getNom(){ return this.nom; }
	
	
	
}
