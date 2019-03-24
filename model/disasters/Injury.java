package model.disasters;

import model.people.Citizen;

public class Injury extends Disaster {

	public Injury(int startCycle, Citizen target) {

		super(startCycle, target);

	}
	public void strike(){
		super.strike();
		Citizen B= ((Citizen)(this.getTarget()));
		B.setBloodLoss(B.getBloodLoss()+30);

	}

	@Override
	public void cycleStep() {
		Citizen B= ((Citizen)(this.getTarget()));
		B.setBloodLoss(B.getBloodLoss()+10);
	}
}
