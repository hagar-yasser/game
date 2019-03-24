package model.disasters;

import model.infrastructure.ResidentialBuilding;

public class Fire extends Disaster {

	public Fire(int startCycle, ResidentialBuilding target) {

		super(startCycle, target);

	}
	public void strike(){
		super.strike();
		ResidentialBuilding B= ((ResidentialBuilding)(this.getTarget()));
		B.setFireDamage(B.getFireDamage()+10);

	}

	@Override
	public void cycleStep() {
		ResidentialBuilding B= ((ResidentialBuilding)(this.getTarget()));
		B.setFireDamage(B.getFireDamage()+10);
	}
}
