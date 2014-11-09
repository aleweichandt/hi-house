package server.model;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BackgroundServlet extends GenericServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7609162475370456548L;
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void init() throws ServletException {
		readConfig();
		TimerTask srTimer = new ServicesTask();
		TimerTask arTimer = new ArduinoTask();
		Timer timer = new Timer();
		timer.schedule(srTimer, 1000, 1000);
		timer = new Timer();
		timer.schedule(arTimer, 1000, 1000);
	}
	
	private void readConfig() {
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");

		//Arduino serial port
			String ardPort = (String) envCtx.lookup("ARDUINO_PORT_NAME");
			if(ardPort != null) C.Config.ARDUINO_PORT_NAME = ardPort;
			Integer ardRead = (Integer) envCtx.lookup("ARDUINO_PORT_READ");
			if(ardRead != null) C.Config.ARDUINO_PORT_READ = ardRead;
		//MySQL Database			
			String dbLoc = (String) envCtx.lookup("DATABASE_LOCATION");
			if(dbLoc != null) C.Config.DATABASE_LOCATION = dbLoc;
			String dbUsr = (String) envCtx.lookup("DATABASE_USER");
			if(dbUsr != null) C.Config.DATABASE_USER = dbUsr;
			String dbpwd = (String) envCtx.lookup("DATABASE_PASSWORD");
			if(dbpwd != null) C.Config.DATABASE_PASSWORD = dbpwd;
		//Ambient Manager
			Integer ambUT = (Integer) envCtx.lookup("AMBIENT_UPDATE_TIME");
			if(ambUT != null) C.Config.AMBIENT_UPDATE_TIME = ambUT;
			Float ambDeg = (Float) envCtx.lookup("AMBIENT_MAX_DIFF_DEGREES");
			if(ambDeg != null) C.Config.AMBIENT_MAX_DIFF_DEGREES = ambDeg;
		//Security Manager
			Integer secUT = (Integer) envCtx.lookup("SECURITY_UPDATE_TIME");
			if(secUT != null) C.Config.SECURITY_UPDATE_TIME = secUT;
			Float secLgL = (Float) envCtx.lookup("SECURITY_SN_LIGHT_LIMIT");
			if(secLgL != null) C.Config.SECURITY_SN_LIGHT_LIMIT = secLgL;
			Float secTmL = (Float) envCtx.lookup("SECURITY_SN_TERMAL_LIMIT");
			if(secTmL != null) C.Config.SECURITY_SN_TERMAL_LIMIT = secTmL;
			Integer secTO = (Integer) envCtx.lookup("SECURITY_ALERT_TIME_OFFSET");
			if(secTO != null) C.Config.SECURITY_ALERT_TIME_OFFSET = secTO;
			String secTtl = (String) envCtx.lookup("SECURITY_ALERT_MSG_TITLE");
			if(secTtl != null) C.Config.SECURITY_ALERT_MSG_TITLE = secTtl;
			String secMsg = (String) envCtx.lookup("SECURITY_ALERT_MSG_CONTENT");
			if(secMsg != null) C.Config.SECURITY_ALERT_MSG_CONTENT = secMsg;
		//Simulation Manager
			Integer simMinUT = (Integer) envCtx.lookup("SIMULATION_MIN_UPDATE_TIME");
			if(simMinUT != null) C.Config.SIMULATION_MIN_UPDATE_TIME = simMinUT;
			Integer simMaxUT = (Integer) envCtx.lookup("SIMULATION_MAX_UPDATE_TIME");
			if(simMaxUT != null) C.Config.SIMULATION_MAX_UPDATE_TIME = simMaxUT;
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	class ServicesTask extends TimerTask {
		private long mLast = 0;
		public ServicesTask() {
			mLast = System.currentTimeMillis();
		}
		@Override
		public void run() {
			long now = System.currentTimeMillis();
			int dt = (int) (now - mLast);
		//update all services
			AmbientMgr.getInstance().update(dt);
			SecurityMgr.getInstance().update(dt);
			SimulationMgr.getInstance().update(dt);
			mLast = now;
		}
	}
	
	class ArduinoTask extends TimerTask {
		private long mLast = 0;
		public ArduinoTask() {
			mLast = System.currentTimeMillis();
		}
		@Override
		public void run() {
			long now = System.currentTimeMillis();
			int dt = (int) (now - mLast);
		//update all services
			ArduinoHandler.getInstance().update(dt);
			mLast = now;
		}
	}
}
