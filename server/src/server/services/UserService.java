package server.services;

import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
import server.model.User;
import server.model.UserSession;
import server.model.devices.Device;

@Path("/users")
public class UserService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	public Response listUsers(@QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		List<String> users = newSession.getAdmin().listAllUsers();
		if(users == null) {
			return Response.status(500).entity("not found").build();
		}
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = users.iterator();it.hasNext();) {
			builder.add(it.next());
		}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		User user = newSession.getUser(userid);
		if(user == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			user = newSession.getAdmin().getUser(userid);
			if(user == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		return Response.status(200).entity(user.asJson().toString()).build();
	}
	
	@GET
	@Path("{id}/profiles")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserProfiles(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		User user = newSession.getUser(userid);
		if(user == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			user = newSession.getAdmin().getUser(userid);
			if(user == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		List<String> profiles = user.getProfiles();
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = profiles.iterator();it.hasNext();) {
			builder.add(it.next());
		}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	@GET
	@Path("{id}/devices")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserDevices(@PathParam("id") String userid, @QueryParam("token") String tkn,
								   @QueryParam("add_voice_id") boolean addVoice,
								   @QueryParam("add_state") boolean addState) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		User user = newSession.getUser(userid);
		if(user == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			user = newSession.getAdmin().getUser(userid);
			if(user == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		JsonObjectBuilder obuild = Json.createObjectBuilder();
		List<String> devices;
		List<String> profiles = user.getProfiles();
		Iterator<String> itt = profiles.iterator();
		while(itt.hasNext()) {
			Profile prf = newSession.getAdmin().getProfile(itt.next());
			if(prf != null) {
				JsonArrayBuilder abuild = Json.createArrayBuilder();
				devices = prf.getDevices();
				for(Iterator<String> it = devices.iterator();it.hasNext();) {
					Device dv = Device.getFromDB(it.next());
					if(dv != null) {
						JsonObjectBuilder dvbuild = Json.createObjectBuilder();
						dvbuild.add("id", dv.getId());
						if(addVoice) dvbuild.add("voice_id", dv.getVoiceId());
						if(addState) dvbuild.add("state", dv.getState());
						abuild.add(dvbuild.build());
					}
				}
				obuild.add(prf.getId(), abuild);
			}
		}
		return Response.status(200).entity(obuild.build().toString()).build();
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response loginUser(@QueryParam("id") String userid, @QueryParam("pwd") String password) {
		UserSession newSession = SessionHandler.getInstance().createSessionForUser(userid, password);
		if(newSession == null) {
			return Response.status(401).entity("unautorized user or password").build();
		}
		JsonObject ret = Json.createObjectBuilder()
							.add("token", newSession.getToken())
							.add("admin", newSession.getAdmin() != null)
							.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addUser(@PathParam("id") String userid, @QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		
		JsonObject params = C.getJsonFromString(body);
		if(!newSession.getAdmin().addUser(userid, params)){
			return Response.status(500).entity(userid + " already exist").build();
		}
		return Response.status(200).entity(userid + " added").build();
	}
	
	@POST
	@Path("{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateUser(@PathParam("id") String userid, @QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		User user = newSession.getUser(userid);
		if(user == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			user = newSession.getAdmin().getUser(userid);
			if(user == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		JsonObject params = C.getJsonFromString(body);
		user.updateWithParams(params, true);
		return Response.status(200).entity(userid + " updated").build();
	}
	
	@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		if(!newSession.getAdmin().deleteUser(userid)) {
			return Response.status(500).entity("not found").build();
		}
		return Response.status(200).entity(userid + " removed").build();
	}
}
