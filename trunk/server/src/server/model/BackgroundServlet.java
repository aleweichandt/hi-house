package server.model;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BackgroundServlet extends GenericServlet implements Runnable{

	private long mLast = 0;
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void init() throws ServletException {
		mLast = System.currentTimeMillis();
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(true) {
			long now = System.currentTimeMillis();
			int dt = (int) (now - mLast);
			ArduinoHandler.getInstance().update(dt);
			mLast = now;
		}
	}

}
