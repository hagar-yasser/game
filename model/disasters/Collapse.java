package model.disasters;

import model.infrastructure.ResidentialBuilding;

public class Collapse extends Disaster {

	public Collapse(int startCycle, ResidentialBuilding target) {

		super(startCycle, target);

	}
	public void strike(){
	    super.strike();
       ResidentialBuilding B= ((ResidentialBuilding)(this.getTarget()));
       B.setFoundationDamage(B.getFoundationDamage()+10);

    }

    @Override
    public void cycleStep() {
        ResidentialBuilding B= ((ResidentialBuilding)(this.getTarget()));
        B.setFoundationDamage(B.getFoundationDamage()+10);
    }
}
