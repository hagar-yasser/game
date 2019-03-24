package model.disasters;

import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;

public class Infection extends Disaster {

	public Infection(int startCycle, Citizen target) {

		super(startCycle, target);

	}
	public void strike(){
		super.strike();
		Citizen B= ((Citizen)(this.getTarget()));
		B.setToxicity(B.getToxicity()+25);

	}

	@Override
	public void cycleStep() {
		Citizen B= ((Citizen)(this.getTarget()));
		B.setToxicity(B.getToxicity()+15);
	}
}
