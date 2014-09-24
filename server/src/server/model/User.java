package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import server.model.devices.Device;

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
								((Boolean)values.get("Receptor_Alerta")).booleanValue()).tagDB();
					
			}
			return null;
		}
		
		static User getFromJson(String userid, JsonObject params) {
			if(params.containsKey("name") && params.containsKey("pwd") && 
			   params.containsKey("admin") && params.containsKey("alert_receptor")) {
				
				String mail = null;
				if(params.containsKey("email"))
					mail = params.getString("email");
				
				User ret = new User(userid, params.getString("name"),
								params.getString("pwd"),
								mail,
								params.getBoolean("admin"),
								params.getBoolean("alert_receptor"));
				if(params.containsKey("profiles")) {
					List<JsonValue> prfparam = params.getJsonArray("profiles");
					List<String> profiles = new ArrayList<String>();
					Iterator<JsonValue> it = prfparam.iterator();
					while(it.hasNext()) {
						JsonString value = (JsonString) it.next();
						profiles.add(value.getString());
					}
					ret.setProfiles(profiles);
				}
				return ret;
			}
			return null;
		}
		
		private boolean mFromDB = false;
		private String mId = null;
		private String mName = null;
		private String mPassword = null;
		private String mEmail = "";
		private boolean mAdmin = false;
		private boolean mAlertReceptor = false;
		private List<String> mProfiles = null;
		
		public User(String id, String name, String pwd, String email, boolean admin, boolean receptor) {
			mId = id;
			mName = name;
			mPassword = pwd;
			mEmail = (email!=null)?email:"";
			mAdmin = admin;
			mAlertReceptor = receptor;
			mProfiles = new ArrayList<String>();
			
			DBRequestHandler request = new DBRequestHandler();
			List<Object> ids = request.getUserProfileIds(id);
			if(!ids.isEmpty()) {
				for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
					mProfiles.add((String)it.next());
				}
			}
		}
		
		private User tagDB() {
			mFromDB = true;
			return this;
		}
		
		public boolean commitToDB() {
			DBRequestHandler request = new DBRequestHandler();
			if(mFromDB) {
				return request.updateUser(this);
			} else {
				return request.addUser(this);
			}
		}
		
		public boolean deleteFromDB() {
			if(mFromDB) {
				DBRequestHandler request = new DBRequestHandler();
				return request.deleteUser(mId);
			}
			return true;
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
		
		public void updateWithParams(JsonObject values, boolean commit) {
			if(values.containsKey("name")) {
				mName = values.getString("name");
			}
			if(values.containsKey("pwd")) {
				mPassword = values.getString("pwd");
			}
			if(values.containsKey("email")) {
				mEmail = values.getString("email");
			}
			if(values.containsKey("admin")) {
				mAdmin = values.getBoolean("admin");
			}
			if(values.containsKey("alert_receptor")) {
				mAlertReceptor = values.getBoolean("alert_receptor");
			}
			if(values.containsKey("profiles")) {
				List<JsonValue> prfparam = values.getJsonArray("profiles");
				List<String> profiles = new ArrayList<String>();
				Iterator<JsonValue> it = prfparam.iterator();
				while(it.hasNext()) {
					JsonString value = (JsonString) it.next();
					profiles.add(value.getString());
				}
				setProfiles(profiles);
			}
			if(commit) {
				commitToDB();
			}
		}
		
		public boolean isValidPassword(String pwd) {
			return pwd.compareTo(mPassword) == 0;
		}
		
		public Profile getProfile(String profileid) {
			if(mProfiles.contains(profileid)) {
				return Profile.getFromDB(profileid);
			}
			return null;
		}
		
		public Device getDevice(String deviceid) {
			Device dv = null;
			Iterator<String> it = mProfiles.iterator();
			while(it.hasNext()) {
				Profile prf = Profile.getFromDB(it.next());
				dv = prf.getDevice(deviceid);
				if(dv != null) {
					return dv;
				}
			}
			return null;
		}
		
		public boolean isAdmin(){
			return mAdmin;
		}
		
		public void setAdmin(boolean b) {
			mAdmin = b;
		}
		
		public boolean isReceptor() {
			return mAlertReceptor;
		}
		
		public void setReceptor(boolean b) {
			mAlertReceptor = b;
		}
		
		public String getId() {
			return mId;
		}
		
		public String getName() {
			return mName;
		}
		
		public void setName(String name) {
			mName = name;
		}
		
		public String getPwd() {
			return mPassword;
		}
		
		public void setPwd(String pwd) {
			mPassword = pwd;
		}
		
		public String getEmail() {
			return mEmail;
		}
		
		public void setEmail(String email) {
			mEmail = email;
		}
		
		public List<String> getProfiles() {
			return mProfiles;
		}
		
		public void setProfiles(List<String> l) {
			mProfiles = l;
		}
}