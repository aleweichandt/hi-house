package server.services;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import server.model.SecurityMgr;
import server.model.SessionHandler;
import server.model.UserSession;

@Path("/security")
public class SecurityService {
	@GET
	@Path("/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getState(@QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		JsonObject ret = Json.createObjectBuilder()
				.add("state", SecurityMgr.getInstance().getState())
				.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@GET
	@Path("/alarm_conf")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getAlarmConfig(@QueryParam("token") String tkn) {
		return Response.status(200).entity("alarm destination").build();
	}
	
	@POST
	@Path("/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setState(@QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		boolean enabled = Boolean.parseBoolean(body);
		SecurityMgr.getInstance().setState(enabled);
		JsonObject ret = Json.createObjectBuilder()
				.add("state", SecurityMgr.getInstance().getState())
				.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@POST
	@Path("/alarm_conf")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setAlarmConfig(@QueryParam("token") String tkn, String body) {
		return Response.status(200).entity("alarm destination in").build();
	}
}
