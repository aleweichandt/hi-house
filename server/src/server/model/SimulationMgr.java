package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationMgr {
	// Singleton begin
	private static SimulationMgr sInstance = null;
	
	public static SimulationMgr getInstance() {
		if(sInstance == null) {
			sInstance = new SimulationMgr();
		}
		return sInstance;
	}
	// Singleton end
	
	private int mTime, mNextStopTime;
	private Random mRandom;
	private List<SimulationRoutine> mActiveRoutines;
	public SimulationMgr() {
		mActiveRoutines = new ArrayList<SimulationRoutine>();
		mRandom = new Random();
		mTime = 0;
		mNextStopTime = getNextStop();
	}
	
	private int getNextStop() {
		return mRandom.nextInt(C.Config.SIMULATION_MAX_UPDATE_TIME - 
				C.Config.SIMULATION_MIN_UPDATE_TIME) +
				C.Config.SIMULATION_MIN_UPDATE_TIME;
	}
	
	public void update(int dt) {
		mTime += dt;
		if(mTime > mNextStopTime) {
			mTime -= mNextStopTime;
			mNextStopTime = getNextStop();
			if(!mActiveRoutines.isEmpty()) {
				boolean add = mRandom.nextBoolean();
				int index = mRandom.nextInt(mActiveRoutines.size());
				SimulationRoutine sr = mActiveRoutines.get(index);
				if(add) {
					sr.pushDevice();
				} else {
					sr.popDevice();
				}
			}
		}
	}
	
	public void addSimulation(SimulationRoutine sr) {
		mActiveRoutines.add(sr);
	}
	
	public void removeSimulation(String routineid) {
		SimulationRoutine sr = getSimulationWithId(routineid);
		if(sr != null) {
			removeSimulation(sr);
		}
	}
	
	public void removeSimulation(SimulationRoutine sr) {
		mActiveRoutines.remove(sr);
		sr.stop();
	}
	
	public SimulationRoutine getSimulationWithId(String id) {
		for(Iterator<SimulationRoutine> it = mActiveRoutines.iterator();it.hasNext();) {
			SimulationRoutine sr = it.next();
			if(sr.getId().compareTo(id) == 0) {
				return sr;
			}
		}
		return null;
	}
	
	public List<SimulationRoutine> getSimulationsWithDevice(String deviceid) {
		List<SimulationRoutine> ret = new ArrayList<SimulationRoutine>();
		for(Iterator<SimulationRoutine> it = mActiveRoutines.iterator();it.hasNext();) {
			SimulationRoutine sr = it.next();
			if(sr.getDevices().contains(deviceid)) {
				ret.add(sr);
			}
		}
		return ret;
	}
}
