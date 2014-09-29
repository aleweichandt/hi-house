package com.web.ones.hihouse;

public class Command {

	public final static String serverURL = "http://192.168.1.110:8080/HiHouse/";
	//public final static String serverURL = "http://190.192.169.86:8080/HiHouse/";
	private boolean method;
	private String requestURL;
	private String bodyParams;
	private int type;

	public Command(int type, boolean method, String requestURL, String params){
		this.type = type;
		this.method = method;
		this.requestURL = requestURL;
		this.bodyParams = params;
	}

	public int getType() {
		return type;
	}
	
	public boolean getMethod() {
		return method;
	}

	public String getRequestURL() {
		return serverURL + requestURL;
	}

	public String getParams() {
		return bodyParams;
	}
}
