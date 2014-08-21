package server.model;

import java.util.ArrayList;
import java.util.List;

public class AdminRights {
	public AdminRights() {
		
	}
	
	public List<String> listAllUsers() {
		return new ArrayList<String>();
	}
	
	public User getUser(String userid) {
		return SupportDataManager.getInstance().getUser(userid);
	}
	
	
}
