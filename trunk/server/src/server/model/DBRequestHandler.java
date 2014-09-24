package server.model;
import server.model.devices.Device;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBRequestHandler {
	
	private Connection mConnect = null;
	private Statement mStatement = null;
	
	public DBRequestHandler() {
		
	}
	
	private void open() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			mConnect = DriverManager.getConnection("jdbc:mysql://"+ C.Config.DATABASE_LOCATION
													+ "?user=" + C.Config.DATABASE_USER
													+ "&password=" + C.Config.DATABASE_PASSWORD);
			mStatement = mConnect.createStatement();
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void close() {
		try {
			if(mStatement!=null)mStatement.close();
			if(mConnect!=null)mConnect.close();
		} catch (Exception e) {
		}
	}
	
	private boolean runUpdate(String query) {
		boolean ret = true;
		try {
			open();
			mStatement.executeUpdate(query);
		}catch(Exception e){
			ret = false;
		} finally {
			close();
		}
		return ret;
	}
	
	private List<List<Object>> runQuery(String query) { 
		List<List<Object>> matrix = new ArrayList<List<Object>>();
		try {
			open();
			ResultSet result = mStatement.executeQuery(query);
			ResultSetMetaData md = result.getMetaData();
			//row0 = column names
			List<Object> names = new ArrayList<Object>();
			int len = md.getColumnCount();
			for(int i=1;i<=len;i++){
				names.add(md.getColumnName(i)); 
			}
			matrix.add(names);
			//fill with result
			while(result.next()) {
				List<Object> row = new ArrayList<Object>();
				for(int i=1;i<=len;i++) row.add(result.getObject(i));
				matrix.add(row);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			close();
		}
		return matrix;
	}
	
	private Map<String,Object> getFirstFromQuery (String query) {
		Map<String, Object> values = new HashMap<String, Object>();
		List<List<Object>>ret = runQuery(query);
		if(ret.size() > 1) {
			int len = ret.get(0).size();
			for(int i=0;i<len;i++) values.put((String) ret.get(0).get(i), ret.get(1).get(i));
		}
		return values;
	}
	
	private List<Object> getAllColumnFromQuery (String columnName, String query) {
		List<Object> values = new ArrayList<Object>();
		List<List<Object>>ret = runQuery(query);
		if(ret.size() > 1 && ret.get(0).contains(columnName)) {
			int index = ret.get(0).indexOf(columnName);
			int len = ret.size();
			for(int i=1;i<len;i++) values.add(ret.get(i).get(index));
		}
		return values;
	}
	
//interface
//usuarios
	public List<Object> listAllUsers() {
		return getAllColumnFromQuery("ID_Usuario", C.Queries.GET_ALL_USER_IDS);
	}
	public Map<String,Object> getUser(String userid) {
		return getFirstFromQuery(C.Queries.GET_USER_WITH_ID(userid));
	}
	public List<Object> getUserProfileIds(String userid) {
		return getAllColumnFromQuery("ID_Perfil", C.Queries.GET_PROFILE_IDS_FOR_USER_WITH_ID(userid));
	}
	public boolean addUser(User usr) {
		boolean ret = runUpdate(C.Queries.INSERT_USER(usr));
		if(ret) {
			List<String> users = new ArrayList<String>();
			users.add(usr.getId());
			runUpdate(C.Queries.INSERT_USER_PROFILE(users, usr.getProfiles()));
		}
		return ret;
	}
	public boolean updateUser(User usr) {
		boolean ret = runUpdate(C.Queries.UPDATE_USER(usr));
		if(ret) {
			List<String> users = new ArrayList<String>();
			users.add(usr.getId());
			runUpdate(C.Queries.INSERT_USER_PROFILE(users, usr.getProfiles()));
		}
		return ret;
	}
	public boolean deleteUser(String userid) {
		return runUpdate(C.Queries.DELETE_USER(userid));
	}
	
//perfiles
	public List<Object> listAllProfiles() {
		return getAllColumnFromQuery("ID_Perfil", C.Queries.GET_ALL_PROFILE_IDS);
	}
	public Map<String,Object> getProfile(String profileid) {
		return getFirstFromQuery(C.Queries.GET_PROFILE_WITH_ID(profileid));
	}
	public List<Object> getProfileDeviceIds(String profileid) {
		return getAllColumnFromQuery("ID_Dispositivo", C.Queries.GET_DEVICES_IDS_FOR_PROFILE_WITH_ID(profileid));
	}
	public boolean addProfile(Profile prf) {
		boolean ret = runUpdate(C.Queries.INSERT_PROFILE(prf));
		if(ret) {
			List<String> profiles = new ArrayList<String>();
			profiles.add(prf.getId());
			runUpdate(C.Queries.INSERT_PROFILE_DEVICE(profiles, prf.getDevices()));
		}
		return ret;
	}
	public boolean updateProfile(Profile prf) {
		boolean ret = runUpdate(C.Queries.UPDATE_PROFILE(prf));
		if(ret) {
			List<String> profiles = new ArrayList<String>();
			profiles.add(prf.getId());
			runUpdate(C.Queries.INSERT_PROFILE_DEVICE(profiles, prf.getDevices()));
		}
		return ret;
	}
	public boolean deleteProfile(String profileid) {
		return runUpdate(C.Queries.DELETE_PROFILE(profileid));
	}
	
//dispositivos
	public List<Object> listAllDevices() {
		return getAllColumnFromQuery("ID_Dispositivo", C.Queries.GET_ALL_DEVICE_IDS);
	}
	public Map<String,Object> getDevice(String deviceid) {
		return getFirstFromQuery(C.Queries.GET_DEVICE_WITH_ID(deviceid));
	}
	public Map<String,Object> getDeviceByName(String deviceName) {
		return getFirstFromQuery(C.Queries.GET_DEVICE_WITH_NAME(deviceName));
	}
	public boolean addDevice(Device dvc) {
		return runUpdate(C.Queries.INSERT_DEVICE(dvc));
	}
	public boolean updateDevice(Device dvc) {
		return runUpdate(C.Queries.UPDATE_DEVICE(dvc));
	}
	public boolean deleteDevice(String deviceid) {
		return runUpdate(C.Queries.DELETE_DEVICE(deviceid));
	}
	public List<Object> listAllDevicesOfType(int type) {
		return getAllColumnFromQuery("ID_Dispositivo", C.Queries.GET_DEVICE_IDS_WITH_TYPE(Integer.toString(type)));
	}
}
