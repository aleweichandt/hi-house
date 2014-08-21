package server.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import sun.org.mozilla.javascript.internal.ast.WhileLoop;

public class User {
		static User getFromDB(String userid) {
			DBRequestHandler request = new DBRequestHandler();
			Map<String, Object> values = request.getUser(userid);
			if(!values.isEmpty()){
				return new User((String)values.get("ID_Usuario"),
								(String)values.get("Nombre"),
								(String)values.get("Password"),
								(String)values.get("Email"),
								((Boolean)values.get("Admin")).booleanValue(),
								((Boolean)values.get("Receptor_Alerta")).booleanValue());
					
			}
			return null;
		}
		
		private String mId = null;
		private String mName = null;
		private String mPassword = null;
		private String mEmail = "";
		private boolean mAdmin = false;
		private boolean mAlertReceptor = false;
		private Map<String, Profile> mProfiles = null;
		
		public User(String id, String name, String pwd, String email, boolean admin, boolean receptor) {
			mId = id;
			mName = name;
			mPassword = pwd;
			mEmail = (email!=null)?email:"";
			mAdmin = admin;
			mAlertReceptor = receptor;
			mProfiles = new HashMap<String, Profile>();
			
			DBRequestHandler request = new DBRequestHandler();
			List<Object> ids = request.getUserProfileIds(id);
			if(!ids.isEmpty()) {
				for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
					mProfiles.put((String)it.next(), null);
				}
			}
		}
		
		public boolean isValidPassword(String pwd) {
			return pwd.compareTo(mPassword) == 0;
		}
		
		public boolean isAdmin(){
			return mAdmin;
		}
		
		public String getId() {
			return mId;
		}
		
		public Set<String> getProfiles() {
			return mProfiles.keySet();
		}
		
		
		public JsonObject asJson(){
			return asJson(true);
		}
		
		public JsonObject asJson(boolean forAdm) {
			JsonObjectBuilder builder = Json.createObjectBuilder()
										.add("id", mId)
										.add("name", mName)
										.add("email", mEmail)
										.add("admin", mAdmin)
										.add("alert_receptor", mAlertReceptor);
			if(forAdm) builder.add("pwd", mPassword);
			return builder.build();
		}
}
