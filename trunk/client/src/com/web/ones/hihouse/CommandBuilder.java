package com.web.ones.hihouse;

import java.util.ArrayList;

public class CommandBuilder {
	private ArrayList<String> commands;
	private HiHouse hiHouse;
	
	
	public CommandBuilder(HiHouse activity) {
		
		this.hiHouse = activity; 
		
		commands = new ArrayList<String>();
		commands.add("abrir");
		commands.add("cerrar");
		commands.add("prender");
		commands.add("encender");
		commands.add("apagar");
		commands.add("activar");
		commands.add("desactivar");
	}

	public Command generateCommand(ArrayList<String> matches) {
		String result="";
		int i;
		String command="", device="", com, dev;
		boolean command_set=false, device_set=false;
		
		//primero obtengo el comando
		for (String s : matches){
			if((i = s.indexOf(' '))<=0) return null;
			com = s.substring(0, i);
			
			if("prender".equals(com) || "encender".equals(com) || "abrir".equals(com)){
				command = com;
				int deviceId = getDeviceId(matches);
				if(deviceId!=-1)
					return new Command(Request.SET_DEVICE_STATE,false, "devices/"+deviceId+"/state?enabled=true&token="+hiHouse.getUser().getToken(), "");
				return null;
			}
			if("apagar".equals(com) || "cerrar".equals(com)){
				command = com;
				int deviceId = getDeviceId(matches);
				if(deviceId!=-1)
					return new Command(Request.SET_DEVICE_STATE,false, "devices/"+deviceId+"/state?enabled=false&token="+hiHouse.getUser().getToken(), "");
				return null;
			}
		}
		
		return null;
	}

	private int getDeviceId(ArrayList<String> matches) {		
		int i;
		for (String s : matches){
			if((i = s.indexOf(' '))<=0) return -1;
			int deviceId = hiHouse.getUser().getDeviceByVoiceDesc(s.substring(i+1));
			if(deviceId!=-1) return deviceId;
		}
		return -1;
	}

}
