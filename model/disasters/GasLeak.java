package model.disasters;

import model.infrastructure.ResidentialBuilding;

public class GasLeak extends Disaster {

	public GasLeak(int startCycle, ResidentialBuilding target) {

		super(startCycle, target);

	}
	public void strike(){
		super.strike();
		ResidentialBuilding B= ((ResidentialBuilding)(this.getTarget()));
		B.setGasLevel(B.getGasLevel()+10);

	}

	@Override
	public void cycleStep() {
		ResidentialBuilding B= ((ResidentialBuilding)(this.getTarget()));
		B.setGasLevel(B.getGasLevel()+15);
	}
}
