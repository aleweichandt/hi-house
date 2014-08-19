package server.model;

import java.util.Map;

public class User {
		static User getFromDB(String userid) {
			DBRequestHandler request = new DBRequestHandler();
			Map<String, Object> values = request.getUser(userid);
			if(!values.isEmpty()){
				return new User((String)values.get("ID_Usuario"),
								(String)values.get("Nombre"),
								(String)values.get("Password"),
								(String)values.get("Email"),
								(boolean)values.get("Admin"),
								(boolean)values.get("Receptor_Alerta"));
					
			}
			return null;
		}
		
		private String mId = null;
		private String mName = null;
		private String mPassword = null;
		private String mEmail = null;
		private boolean mIsAdmin = false;
		private boolean mAlertReceptor = false;
		public User(String id, String name, String pwd, String email, boolean admin, boolean receptor) {
			mId = id;
			mName = name;
			mPassword = pwd;
			mEmail = email;
			mIsAdmin = admin;
			mAlertReceptor = receptor;
		}
		
		public boolean isValidPassword(String pwd) {
			return pwd.compareTo(mPassword) == 0;
		}
}
