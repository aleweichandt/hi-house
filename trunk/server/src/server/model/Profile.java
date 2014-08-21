package server.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Profile {
	static Profile getFromDB(String profileid) {
		DBRequestHandler request = new DBRequestHandler();
		Map<String, Object> values = request.getProfile(profileid);
		if(!values.isEmpty()){
			return new Profile((String)values.get("ID_Perfil"),
							(String)values.get("Ambiente"),
							(String)values.get("Descripcion"));
				
		}
		return null;
	}
	
	private String mId = null;
	private String mName = null;
	private String mDescription = null;
	private Map<String, Device> mDevices = null;
	
	public Profile(String id, String name, String description) {
		if(!id.isEmpty()) mId = id;
		if(!name.isEmpty()) mName = name;
		if(!description.isEmpty()) mDescription = description;
		mDevices = new HashMap<String, Device>();
		
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.getProfileDeviceIds(id);
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				mDevices.put((String)it.next(), null);
			}
		}
	}
}
