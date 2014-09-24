package server.services;

import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import server.model.DBRequestHandler;
import server.model.SessionHandler;
import server.model.UserSession;
import server.model.devices.Device;

public class RequestService {
	
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response obtainDeviceAndSendRequest(@QueryParam("token")String tkn, @QueryParam("deviceName")String deviceName,
			@QueryParam("request")String request ) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		
		Object device = Device.getFromDBByName(deviceName);
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
		//for(Iterator<String> it = devices.iterator();it.hasNext();) {
			//builder.add(it.next());
		//}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	
	

}
