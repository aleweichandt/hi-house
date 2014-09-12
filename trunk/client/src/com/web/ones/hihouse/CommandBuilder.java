package com.web.ones.hihouse;

import java.util.ArrayList;

public class CommandBuilder {
	private ArrayList<String> commands,devices;
	
	
	public CommandBuilder() {
		commands = new ArrayList<String>();
		commands.add("abrir");
		commands.add("cerrar");
		commands.add("prender");
		commands.add("encender");
		commands.add("apagar");
		commands.add("activar");
		
		 devices = new ArrayList<String>();
		 devices.add("luz cocina");
		 devices.add("luz living");
		 devices.add("luz jardin");
		 devices.add("luz habitacion 1");
		 devices.add("luz habitacion 2");
		 devices.add("luz chicos");
		 devices.add("luz comedor");
		 devices.add("puerta principal");
		 devices.add("puerta trasera");
		 devices.add("persiana living");
		 devices.add("alarma central");
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
			
			if(!device_set && devices.contains(dev)){
				device_set = true;
				device = dev;
			}

		}
		
		if(command_set && device_set){
			result += "Comando:\""+command+"\" Device:\""+device+"\"\n";
			return result;
		}
		return "Comando no reconocido.";
	}

}
