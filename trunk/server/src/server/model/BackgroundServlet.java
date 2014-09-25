package server.model;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
		TimerTask srTimer = new ServicesTask();
		TimerTask arTimer = new ArduinoTask();
		Timer timer = new Timer();
		timer.schedule(srTimer, 1000, 1000);
		timer = new Timer();
		timer.schedule(arTimer, 1000, 1000);
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
