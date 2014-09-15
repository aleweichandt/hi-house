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

	public String generateCommand(ArrayList<String> matches) {
		String result="";
		int i;
		String command="", device="", com, dev;
		boolean command_set=false, device_set=false;
		
		for (String s : matches){
			if((i = s.indexOf(' '))<=0) return "Comando no reconocido.";
			com = s.substring(0, i);
			dev = s.substring(i+1);
			
			if(!command_set && commands.contains(com)){
				command_set = true;
				command = com;
			}
			
			if(!device_set){
				String str = hiHouse.mydb.getDevice(dev);
				if(str!=""){
					device_set = true;
					device = str;
				}
			}

		}
		
		if(command_set && device_set){
			result += "Comando:\""+command+"\" Device:\""+device+"\"\n";
			return result;
		}
		return "Comando no reconocido.";
	}

}
