package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonObject;

public class AdminRights {
	public AdminRights() {
		
	}
	
	public List<String> listAllUsers() {
		List<String> ret = new ArrayList<String>();
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllUsers();
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				ret.add((String)it.next());
			}
		}
		return ret;
	}
	
	public User getUser(String userid) {
		return User.getFromDB(userid);
	}
	
	public boolean addUser(String userid, JsonObject params) {
		User usr = User.getFromDB(userid);
		if(usr != null){
			return false;
		}
		usr = User.getFromJson(userid, params);
		if(usr == null) {
			return false;
		}
		return usr.commitToDB();
	}
	
	public boolean deleteUser(String userid) {
		User usr = User.getFromDB(userid);
		if(usr == null){
			return false;
		}
		return usr.deleteFromDB();
	}
}
