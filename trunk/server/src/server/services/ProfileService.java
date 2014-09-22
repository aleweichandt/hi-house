package server.services;

import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import server.model.C;
import server.model.Profile;
import server.model.SessionHandler;
import server.model.UserSession;

@Path("/profiles")
public class ProfileService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	public Response listProfiles(@QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		List<String> profiles = newSession.getAdmin().listAllProfiles();
		if(profiles == null) {
			return Response.status(500).entity("not found").build();
		}
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = profiles.iterator();it.hasNext();) {
			builder.add(it.next());
		}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		Profile prf = newSession.getUser().getProfile(profileid);
		if(prf == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			prf = newSession.getAdmin().getProfile(profileid);
			if(prf == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		return Response.status(200).entity(prf.asJson().toString()).build();
	}
	
	@GET
	@Path("/{id}/devices")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getProfileDevices(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		Profile prf = newSession.getUser().getProfile(profileid);
		if(prf == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			prf = newSession.getAdmin().getProfile(profileid);
			if(prf == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		List<String> devices = prf.getDevices();
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = devices.iterator();it.hasNext();) {
			builder.add(it.next());
		}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	@POST
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		
		JsonObject params = C.getJsonFromString(body);
		if(!newSession.getAdmin().addProfile(profileid, params)){
			return Response.status(500).entity(profileid + " already exist").build();
		}
		return Response.status(200).entity(profileid + " added").build();
	}
	
	@POST
	@Path("/{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		Profile prf = newSession.getAdmin().getProfile(profileid);
		if(prf == null) {
			return Response.status(500).entity("not found").build();
		}
		JsonObject params = C.getJsonFromString(body);
		prf.updateWithParams(params, true);
		return Response.status(200).entity(profileid + " updated").build();
	}
	
	@POST
	@Path("/{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		if(!newSession.getAdmin().deleteProfile(profileid)) {
			return Response.status(500).entity("not found").build();
		}
		return Response.status(200).entity(profileid + " removed").build();
	}
}
