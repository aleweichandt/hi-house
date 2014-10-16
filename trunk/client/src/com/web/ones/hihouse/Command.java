package com.web.ones.hihouse;

public class Command {

	public static String serverURL = "http://192.168.26.85:8080";
	private boolean method;
	private String requestURL;
	private String bodyParams;
	private int type;
	
	public static void setCommandServerBase(String url) {
		serverURL = url;
	}
	
	public static String getServerUrl() {
		return serverURL + "/HiHouse/";
	}

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
		return getServerUrl() + requestURL;
	}

	public String getParams() {
		return bodyParams;
	}
}
