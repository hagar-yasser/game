package simulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

import model.SOSListener;
import model.disasters.*;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.*;

public class Simulator {

	private int currentCycle =0;
	private ArrayList<ResidentialBuilding> buildings;
	private ArrayList<Citizen> citizens;


	private ArrayList<Unit> emergencyUnits;
	private ArrayList<Disaster> plannedDisasters;



	private ArrayList<Disaster> executedDisasters;
	private Address[][] world;

	private SOSListener emergencyService;


	public Simulator(SOSListener emergencyService) throws Exception {
        this.emergencyService=emergencyService;
		buildings = new ArrayList<ResidentialBuilding>();
		citizens = new ArrayList<Citizen>();
		emergencyUnits = new ArrayList<Unit>();
		plannedDisasters = new ArrayList<Disaster>();
		executedDisasters = new ArrayList<Disaster>();

		world = new Address[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				world[i][j] = new Address(i, j);
			}
		}

		loadUnits("units.csv");
		loadBuildings("buildings.csv");
		loadCitizens("citizens.csv");
		loadDisasters("disasters.csv");

		for (int i = 0; i < buildings.size(); i++) {

			ResidentialBuilding building = buildings.get(i);
			for (int j = 0; j < citizens.size(); j++) {

				Citizen citizen = citizens.get(j);
				if (citizen.getLocation() == building.getLocation())
					building.getOccupants().add(citizen);

			}
		}
	}

	private void loadUnits(String path) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();

		while (line != null) {

			String[] info = line.split(",");
			String id = info[1];
			int steps = Integer.parseInt(info[2]);

			switch (info[0]) {

			case "AMB":
				emergencyUnits.add(new Ambulance(id, world[0][0], steps));
				break;

			case "DCU":
				emergencyUnits.add(new DiseaseControlUnit(id, world[0][0], steps));
				break;

			case "EVC":
				emergencyUnits.add(new Evacuator(id, world[0][0], steps, Integer.parseInt(info[3])));
				break;

			case "FTK":
				emergencyUnits.add(new FireTruck(id, world[0][0], steps));
				break;

			case "GCU":
				emergencyUnits.add(new GasControlUnit(id, world[0][0], steps));
				break;

			}

			line = br.readLine();
		}

		br.close();
	}

	private void loadBuildings(String path) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();

		while (line != null) {

			String[] info = line.split(",");
			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);

			buildings.add(new ResidentialBuilding(world[x][y]));

			line = br.readLine();

		}
		br.close();
	}

	private void loadCitizens(String path) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();

		while (line != null) {

			String[] info = line.split(",");
			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			String id = info[2];
			String name = info[3];
			int age = Integer.parseInt(info[4]);

			citizens.add(new Citizen(world[x][y], id, name, age));

			line = br.readLine();

		}
		br.close();
	}

	private void loadDisasters(String path) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();

		while (line != null) {

			String[] info = line.split(",");
			int startCycle = Integer.parseInt(info[0]);
			ResidentialBuilding building = null;
			Citizen citizen = null;

			if (info.length == 3)
				citizen = getCitizenByID(info[2]);
			else {

				int x = Integer.parseInt(info[2]);
				int y = Integer.parseInt(info[3]);
				building = getBuildingByLocation(world[x][y]);

			}

			switch (info[1]) {

			case "INJ":
				plannedDisasters.add(new Injury(startCycle, citizen));
				break;

			case "INF":
				plannedDisasters.add(new Infection(startCycle, citizen));
				break;

			case "FIR":
				plannedDisasters.add(new Fire(startCycle, building));
				break;

			case "GLK":
				plannedDisasters.add(new GasLeak(startCycle, building));
				break;
			}

			line = br.readLine();
		}
		br.close();
		
	}

	private Citizen getCitizenByID(String id) {

		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.get(i).getNationalID().equals(id))
				return citizens.get(i);
		}

		return null;
	}

	private ResidentialBuilding getBuildingByLocation(Address location) {

		for (int i = 0; i < buildings.size(); i++) {
			if (buildings.get(i).getLocation() == location)
				return buildings.get(i);
		}

		return null;
	}
    public int calculateCasualties(){
	    int c=0;
        for (int i = 0; i <citizens.size() ; i++) {
            if(citizens.get(i).getState()==CitizenState.DECEASED)
                c++;
        }
        return c;

    }
    public void setEmergencyService(SOSListener emergencyService) {
        this.emergencyService = emergencyService;
    }


	public ArrayList<Unit> getEmergencyUnits() {
        return emergencyUnits;
    }
    public void assignAddress(Simulatable sim, int x , int y){
	    if(sim instanceof Citizen)
	        ((Citizen) sim).setLocation(world[x][y]);
	    if(sim instanceof Unit )
            ((Unit)sim).setLocation(world[x][y]);
    }
    public void nextCycle(){
		currentCycle++;
		if(!plannedDisasters.isEmpty()&&plannedDisasters.get(0).getStartCycle()==currentCycle){
			Disaster disaster=plannedDisasters.get(0);
			if(disaster instanceof Fire){
				ResidentialBuilding r=(ResidentialBuilding)disaster.getTarget();
				if(r.getGasLevel()==0)
					((Fire)disaster).strike();
				if(r.getGasLevel()>0&&r.getGasLevel()<70){
					 disaster=new Collapse(disaster.getStartCycle(),r);
					r.setFireDamage(0);
					disaster.strike();
				}
				if(r.getGasLevel()<=70){
					r.setStructuralIntegrity(0);
				}

			}
			if(disaster instanceof GasLeak){
				ResidentialBuilding r=(ResidentialBuilding)disaster.getTarget();
				if(r.getDisaster()!=null&&r.getDisaster() instanceof Fire){
					disaster=new Collapse(disaster.getStartCycle(),r);
					r.setFireDamage(0);
					disaster.strike();
				}

			}
			plannedDisasters.remove(disaster);

			for (int i = 0; i <executedDisasters.size() ; i++) {
				if(executedDisasters.get(i).getTarget() instanceof ResidentialBuilding){
					ResidentialBuilding r=(ResidentialBuilding)executedDisasters.get(i).getTarget();
					if(r.getFireDamage()==100){
						Collapse c=new Collapse(currentCycle,r);
						c.strike();
					}
				}


			}
			executedDisasters.add(disaster);


		}
		for (int i = 0; i <emergencyUnits.size() ; i++) {
			emergencyUnits.get(i).cycleStep();
		}
		for (int i = 0; i <executedDisasters.size() ; i++) {
			if(executedDisasters.get(i).isActive()&&executedDisasters.get(i).getStartCycle()<currentCycle)
				executedDisasters.get(i).cycleStep();

		}
		for (int i = 0; i <buildings.size() ; i++) {
		      buildings.get(i).cycleStep();
		}
		for (int i = 0; i <citizens.size() ; i++) {
			citizens.get(i).cycleStep();
		}

	}
	public boolean checkGameOver(){
		boolean plannedD=!(plannedDisasters.isEmpty());
		boolean activeD=false;
		for (int i = 0; i <executedDisasters.size()&&!activeD ; i++) {
			if(executedDisasters.get(i).getTarget()instanceof Citizen)
				activeD=!(((Citizen)executedDisasters.get(i).getTarget()).getState()==CitizenState.DECEASED);
			if(executedDisasters.get(i).getTarget() instanceof ResidentialBuilding)
				activeD=!(((ResidentialBuilding)executedDisasters.get(i).getTarget()).getStructuralIntegrity()==0);
		}
		boolean activeU=false;
		for (int i = 0; i <emergencyUnits.size()&&!activeU; i++) {
             if(emergencyUnits.get(i).getState()!=UnitState.IDLE)
             	activeU=true;
		}
		return !activeD&&!activeU&&!plannedD;
	}


}
