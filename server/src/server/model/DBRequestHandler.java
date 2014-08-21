package server.model;

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
	public Map<String,Object> getUser(String userid) {
		return getFirstFromQuery(C.Queries.GET_USER_WITH_ID(userid));
	}
	public List<Object> getUserProfileIds(String userid) {
		return getAllColumnFromQuery("ID_Perfil", C.Queries.GET_PROFILE_IDS_FOR_USER_WITH_ID(userid));
	}
	
//perfiles
	public Map<String,Object> getProfile(String profileid) {
		return getFirstFromQuery(C.Queries.GET_PROFILE_WITH_ID(profileid));
	}
	public List<Object> getProfileDeviceIds(String profileid) {
		return getAllColumnFromQuery("ID_Dispositivo", C.Queries.GET_DEVICES_IDS_FOR_PROFILE_WITH_ID(profileid));
	}
	
//dispositivos
	public Map<String,Object> getDevice(String deviceid) {
		return getFirstFromQuery(C.Queries.GET_DEVICE_WITH_ID(deviceid));
	}
}
