package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments;

public class Parent {

	
	private TreatmentEnum type;
	
	
	public Parent( TreatmentEnum t ){
		this.type = t;
	}


	public void setType(TreatmentEnum type) {
		this.type = type;
	}


	public TreatmentEnum getType() {
		return type;
	}
	
	
}
