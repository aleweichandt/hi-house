package server.model;

import java.util.HashMap;
import java.util.Map;

public class SupportDataManager {
	// Singleton begin
	private static SupportDataManager sInstance = null;
	
	public static SupportDataManager getInstance() {
		if(sInstance == null) {
			sInstance = new SupportDataManager();
		}
		return sInstance;
	}
	// Singleton end
	
	private Map<String, User> mUsers;
	private Map<String, Profile> mProfiles;
	private Map<String, Device> mDevices;
	
	protected SupportDataManager() {
		mUsers= new  HashMap<String, User>();
		mProfiles = new HashMap<String, Profile>();
		mDevices = new HashMap<String, Device>();
	}
	
	public User getUser(String userid) {
		if(!mUsers.containsKey(userid)){
			User user = User.getFromDB(userid);
			if(user != null) {
				mUsers.put(userid, user);
			}
			return user;
		}
		return mUsers.get(userid);
	}
	
	public Profile getProfile(String profileid) {
		if(!mProfiles.containsKey(profileid)){
			Profile profile = Profile.getFromDB(profileid);
			if(profile != null) {
				mProfiles.put(profileid, profile);
			}
			return profile;
		}
		return mProfiles.get(profileid);
	}
	
	public Device geDevice(String deviceid) {
		if(!mDevices.containsKey(deviceid)){
			Device device = Device.getFromDB(deviceid);
			if(device != null) {
				mDevices.put(deviceid, device);
			}
			return device;
		}
		return mDevices.get(deviceid);
	}
}
