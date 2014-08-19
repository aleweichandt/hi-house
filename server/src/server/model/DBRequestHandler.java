package server.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBRequestHandler {
	
	private Connection mConnect = null;
	private Statement mStatement = null;
	
	private String mLocation ="localhost:3306/hihouse";
	private String mUser = "hihouse";
	private String mPwd = "hihouse";
	
	public DBRequestHandler() {
		
	}
	
	public DBRequestHandler(String location, String user, String pwd) {
		if(!location.isEmpty()) {
			mLocation = location;
		}
		if(!user.isEmpty()){
			mUser = user;
		}
		if(!pwd.isEmpty()){
			mPwd = pwd;
		}
	}
	
	private void open() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			mConnect = DriverManager.getConnection("jdbc:mysql://"+ mLocation
													+ "?user=" + mUser
													+ "&password=" +mPwd);
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
	
	//interface
	public Map<String,Object> getUser(String userid) {
		Map<String, Object> values = new HashMap<String, Object>();
		try {
			open();
			ResultSet result = mStatement.executeQuery("SELECT * from usuarios where ID_Usuario = '" + userid + "'");
			if(result != null) {
				while (result.next()) {
					values.put("ID_Usuario", result.getString("ID_Usuario"));
					values.put("Nombre", result.getString("Nombre"));
					values.put("Password", result.getString("Password"));
					values.put("Email", result.getString("Email"));
					values.put("Admin", result.getBoolean("Admin"));
					values.put("Receptor_Alerta", result.getBoolean("Receptor_Alerta"));
				}
				result.close();
			}
		} catch (Exception e) {
		} finally {
			close();
		}
		return values;
	}
}
