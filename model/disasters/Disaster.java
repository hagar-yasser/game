package model.disasters;

import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Disaster implements Simulatable {

	private int startCycle;
	private Rescuable target;
	private boolean active;

	public Disaster(int startCycle, Rescuable target) {

		this.startCycle = startCycle;
		this.target = target;

	}
	public void strike(){
	    this.setActive(true);
	  if(this.target instanceof Citizen)
          (Citizen)this.target.struckBy(this);
        if(this.target instanceof ResidentialBuilding)
            (ResidentialBuilding)this.target.struckBy(this);
    }
    public abstract void cycleStep();

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getStartCycle() {
		return startCycle;
	}

	public Rescuable getTarget() {
		return target;
	}

}
