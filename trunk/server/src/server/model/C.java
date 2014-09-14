package server.model;

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
	}
}
