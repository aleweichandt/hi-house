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

import server.model.AmbientMgr;
import server.model.SessionHandler;
import server.model.UserSession;

@Path("/temperature")
public class AmbientService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getTemperature(@QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		float temp = AmbientMgr.getInstance().getTemperature();
		JsonObject ret = Json.createObjectBuilder()
				.add("value", temp)
				.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setTemperature(@QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		float desired = Float.parseFloat(body);
		AmbientMgr.getInstance().setTemperature(desired);
		return Response.status(200).entity("temperature set up").build();
	}
}
