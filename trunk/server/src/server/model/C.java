package server.model;

import server.model.devices.Device;
import server.model.devices.TermalActuator;

public final class C {
	
//Configuration
	public final static class Config {
	//Arduino serial port
		public static final String ARDUINO_PORT_NAME = "COM6";
		public static final int ARDUINO_PORT_READ = 9600;
		
	//MySQL Database
		public static final String DATABASE_LOCATION ="localhost:3306/hihouse";
		public static final String DATABASE_USER = "hihouse";
		public static final String DATABASE_PASSWORD = "hihouse";
	}

	//MySQL Queries
	public final static class Queries {
		
		public static final String GET_ALL_USER_IDS = 
				"SELECT distinct ID_Usuario from usuarios";
		
		public static final String GET_ALL_PROFILE_IDS = 
				"SELECT distinct ID_Perfil from perfiles";
		
		public static final String GET_ALL_DEVICE_IDS = 
				"SELECT distinct ID_Dispositivo from dispositivos";
		
		public static final String GET_USER_WITH_ID(String userid){
			return "SELECT * from usuarios where ID_Usuario = '" + userid + "'";
		}
		public static final String GET_PROFILE_IDS_FOR_USER_WITH_ID(String userid){
			return "SELECT distinct perfiles.ID_Perfil from usuarios, usuario_perfil, perfiles where usuarios.ID_Usuario = usuario_perfil.ID_Usuario and usuario_perfil.ID_Perfil = perfiles.ID_Perfil and usuarios.ID_Usuario = '" + userid + "'";
		}
		public static final String GET_PROFILE_WITH_ID(String profileid){
			return "SELECT * from perfiles where ID_Perfil = '" + profileid + "'";
		}
		public static final String GET_DEVICES_IDS_FOR_PROFILE_WITH_ID(String profileid){
			return "SELECT distinct dispositivos.ID_Dispositivo from perfiles, perfil_dispositivo, dispositivos where perfiles.ID_Perfil = perfil_dispositivo.ID_Perfil and perfil_dispositivo.ID_Dispositivo = dispositivos.ID_Dispositivo and perfil.ID_Perfil = '" + profileid + "'";
		}
		public static final String GET_DEVICE_WITH_ID(String deviceid){
			return "SELECT * from dispositivos where ID_Dispositivo = '" + deviceid + "'";
		}
		
		public static final String INSERT_USER(User user){
			String id = "'" + user.getId() + "'";
			String name = "'" + user.getName() + "'";
			String pwd = user.getPwd();
			String email = (user.getEmail().isEmpty())?("'" + user.getEmail() + "'"): "NULL";
			String adm = user.isAdmin()?"1":"0";
			String recv = user.isReceptor()?"1":"0";
			return "INSERT INTO usuarios VALUES (" +id + "," + name + "," + pwd +
												 "," + email + "," + adm + "," + recv + ")";
		}
		
		public static final String UPDATE_USER(User user){
			String id = "'" + user.getId() + "'";
			String name = "'" + user.getName() + "'";
			String pwd = user.getPwd();
			String email = (user.getEmail().isEmpty())?("'" + user.getEmail() + "'"): "NULL";
			String adm = user.isAdmin()?"1":"0";
			String recv = user.isReceptor()?"1":"0";
			return "UPDATE usuarios SET Nombre=" + name + ",Password=" + pwd +
				   ",Email=" + email + ",Admin=" + adm + ",Receptor_Alerta" + recv +
				   "WHERE ID_Usuario="+id;
		}
		
		public static final String DELETE_USER(String userid) {
			return "DELETE * FROM usuarios WHERE ID_Usuario='"+userid+"'";
		}

		public static final String INSERT_PROFILE(Profile prf){
			String id = "'" + prf.getId() + "'";
			String name = "'" + prf.getName() + "'";
			String desc = (prf.getDescription().isEmpty())?("'" + prf.getDescription() + "'"): "NULL";
			return "INSERT INTO perfiles VALUES (" +id + "," + name + "," + desc + ")";
		}
		
		public static final String UPDATE_PROFILE(Profile prf){
			String id = "'" + prf.getId() + "'";
			String name = "'" + prf.getName() + "'";
			String desc = (prf.getDescription().isEmpty())?("'" + prf.getDescription() + "'"): "NULL";
			return "UPDATE perfiles SET Ambiente=" + name + ",Descripcion=" + desc +
				   "WHERE ID_Perfil="+id;
		}
		
		public static final String DELETE_PROFILE(String profileid) {
			return "DELETE * FROM perfiles WHERE ID_Perfil='"+profileid+"'";
		}

		public static final String INSERT_DEVICE(Device dvc){
			String id = "'" + dvc.getId() + "'";
			String name = "'" + dvc.getName() + "'";
			String vid = "'" + dvc.getVoiceId() +"'";
			String type = Integer.toString(dvc.getClassType());
			String state = dvc.getState()?"1":"0";
			String pin1 = (dvc.getPin(0) >= 0)?Integer.toString(dvc.getPin(0)):"NULL";
			String pin2 = (dvc.getPin(1) >= 0)?Integer.toString(dvc.getPin(1)):"NULL";
			String pin3 = (dvc.getPin(2) >= 0)?Integer.toString(dvc.getPin(2)):"NULL";
			String param1 = "NULL";
			/*if(dvc.getClass().toString() == "TermalActuator") {
				param1 = ((TermalActuator)dvc).getSubType();
			}*/
			return "INSERT INTO dispositivos VALUES (" +id + "," + type + "," + name + "," + 
				   vid + "," + state + "," + pin1 + "," + pin2 + "," + pin3 + "," + param1 +")";
		}
		
		public static final String UPDATE_DEVICE(Device dvc){
			String id = "'" + dvc.getId() + "'";
			String name = "'" + dvc.getName() + "'";
			String vid = "'" + dvc.getVoiceId() +"'";
			String type = Integer.toString(dvc.getClassType());
			String state = dvc.getState()?"1":"0";
			String pin1 = (dvc.getPin(0) >= 0)?Integer.toString(dvc.getPin(0)):"NULL";
			String pin2 = (dvc.getPin(1) >= 0)?Integer.toString(dvc.getPin(1)):"NULL";
			String pin3 = (dvc.getPin(2) >= 0)?Integer.toString(dvc.getPin(2)):"NULL";
			String param1 = "NULL";
			/*if(dvc.getClass().toString() == "TermalActuator") {
				param1 = ((TermalActuator)dvc).getSubType();
			}*/
			return "UPDATE dispositivos SET Tipo=" + type + ",Ambiente=" + name + 
				   ",Descripcion_Ejec_Voz=" + vid + ",Estado=" + state + 
				   ",Pin1=" + pin1 + ",Pin2=" + pin2 + ",Pin3=" + pin3 +
				   ",Param1=" + param1 +
				   "WHERE ID_Dispositivo="+id;
		}
		
		public static final String DELETE_DEVICE(String deviceid) {
			return "DELETE * FROM dispositivos WHERE ID_Dispositivo='"+deviceid+"'";
		}
		
		public static final String INSERT_USER_PROFILE(String userid, String profileid) {
			return "INSERT INTO usuario_perfil VALUES ('" + userid + "','" + profileid + "')";
		}
		
		public static final String DELETE_USER_PROFILE(String userid, String profileid) {
			return "DELETE * FROM usuario_perfil WHERE ID_Usuario='" + userid +
				   "' AND ID_Perfil='" + profileid + "'";
		}
		
		public static final String INSERT_PROFILE_DEVICE(String profileid, String deviceid) {
			return "INSERT INTO perfil_dispositivo VALUES ('" + profileid + "','" + deviceid + "')";
		}
		
		public static final String DELETE_PROFILE_DEVICE(String profileid, String deviceid) {
			return "DELETE * FROM perfil_dispositivo WHERE ID_Perfil='" + profileid +
				   "' AND ID_Dispositivo='" + deviceid + "'";
		}
	}
}
