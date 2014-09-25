package com.web.ones.hihouse;

public class Command {

	private final static String serverURL = "http://192.168.1.110:8080/HiHouse/";
	private boolean method;
	private String requestURL;
	private String bodyParams;
	
	public Command(boolean method, String requestURL, String params){
		this.method = method;
		this.requestURL = requestURL;
		this.bodyParams = params;
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
