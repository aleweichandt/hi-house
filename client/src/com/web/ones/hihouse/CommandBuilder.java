package com.web.ones.hihouse;

import java.text.Normalizer;
import java.util.ArrayList;

import android.widget.Toast;

public class CommandBuilder {
	private ArrayList<String> irrelevantWords;
	private HiHouse hiHouse;
	
	
	public CommandBuilder(HiHouse activity) {
		
		this.hiHouse = activity; 
		
		irrelevantWords = new ArrayList<String>();
		irrelevantWords.add("a");
		irrelevantWords.add("la");
	}

	public Command generateCommand(ArrayList<String> matches) {
		int i;
		String command;
		String delims = "[ ]+";
		
		//checkeo el comando
		for (String s : matches){
			if(s.indexOf(' ')<=0) return null; //si no hay espacio es una sola palabra
			String[] tokens = s.split(delims);
			i = 0;
			try{
				command = tokens[i++];
				
				String voice_desc = s.substring(s.indexOf(' ')+1);
				Device device = hiHouse.getUser().getDeviceByVoiceDesc(voice_desc);
				if(device==null){ //probamos nuevamente removiendo acentos
					voice_desc = Normalizer.normalize(voice_desc, Normalizer.Form.NFD);
					voice_desc = voice_desc.replaceAll("[^\\p{ASCII}]", "");
					device = hiHouse.getUser().getDeviceByVoiceDesc(voice_desc);
				}
				if(device!=null){
					switch(device.getType()){
					case Device.DEVICE_TYPE_AC_DOOR: // abrir/cerrar
						if(command.equals("abrir")) return setDeviceStateCommand(device, true);
						else if(command.equals("cerrar")) return setDeviceStateCommand(device, false);
						break;
					case Device.DEVICE_TYPE_AC_LIGHT: // prender-encender/apagar
					case Device.DEVICE_TYPE_AC_TERMAL:
						if(command.equals("prender")||command.equals("encender")) return setDeviceStateCommand(device, true);
						else if(command.equals("apagar")) return setDeviceStateCommand(device, false);
						break;
					case Device.DEVICE_TYPE_SN_LIGHT: // activar/desactivar
					case Device.DEVICE_TYPE_SN_TERMAL:
					case Device.DEVICE_TYPE_SN_DOOR:
						if(command.equals("activar")) return setDeviceStateCommand(device, true);
						else if(command.equals("desactivar")) return setDeviceStateCommand(device, false);
					}
				}
				else if(command.equals("subir") || command.equals("bajar")) return tempCommand(tokens, i);
				else if(command.equals("temperatura")) return tempCommand(tokens, --i);
				else if(command.equals("apagar")) return tempOffCommand(tokens, i);
				else if(command.equals("activar")) return alarmSimuCommand(tokens, i, true, matches);
				else if(command.equals("desactivar")) return alarmSimuCommand(tokens, i, false, matches);
				else{
					Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();
					return null;
				}
			}
			catch(ArrayIndexOutOfBoundsException e){Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();return null;}
		}
		Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();
		return null;
	}

	private Command setDeviceStateCommand(Device d, boolean b) {
		return new Command(Request.SET_DEVICE_STATE,false, "devices/"+d.getId()+"/state?enabled="+b+"&token="+hiHouse.getUser().getToken(), "");
	}

	private Command alarmSimuCommand(String[] tokens, int i, boolean b, ArrayList<String> matches) {
		i = skipIrrelevantWord(tokens[i], i);
		String command = tokens[i];
		if(command.equals("alarma") || command.equals("seguridad")){
			return new Command(Request.ALARM_STATE, false, "security/state?token="+hiHouse.getUser().getToken(), ""+b);
		}
		else if(command.equals("simulador")){
			for (String s : matches){
				String[] toks = s.split("[ ]+");
				String profName = toks[i+1];
				int profId = hiHouse.getUser().getProfileByName(profName);
				if(profId>00)
					return new Command(Request.SIMULATOR_STATE, false, "simulation/"+profId+"/state?token="+hiHouse.getUser().getToken()+"&enabled="+b, "");
			}
		}
		Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();
		return null;
	}

	private Command tempOffCommand(String[] tokens, int i) {
		i = skipIrrelevantWord(tokens[i], i);
		String command = tokens[i++];
		if(command.equals("temperatura")){
			return new Command(Request.VOICE_SET_DESIRED_TEMP, false, "temperature?token="+hiHouse.getUser().getToken(), "-1");
		}
		Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();
		return null;
	}
	
	private Command tempCommand(String[] tokens, int i) {
		i = skipIrrelevantWord(tokens[i], i);
		String command = tokens[i++];
		if(command.equals("temperatura")){
			//obtenemos los grados
			i = skipIrrelevantWord(tokens[i], i);
			int temp;
			try{temp = Integer.parseInt(tokens[i++]);}catch(NumberFormatException e){Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();return null;}
			if(temp<15 || temp>30){
				Toast.makeText(hiHouse, "La temperatura debe estar entre 15 y 30°", Toast.LENGTH_SHORT).show();
				return null;
			}
			return new Command(Request.VOICE_SET_DESIRED_TEMP, false, "temperature?token="+hiHouse.getUser().getToken(), ""+temp);
		}
		Toast.makeText(hiHouse, "Comando no reconocido", Toast.LENGTH_LONG).show();
		return null;
	}

	private int skipIrrelevantWord(String token, int i) {
		if(irrelevantWords.contains(token)) return i+1;
		return i;
	}
}
