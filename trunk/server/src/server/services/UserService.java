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

import server.model.SessionHandler;
import server.model.User;
import server.model.UserSession;

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
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response loginUser(@QueryParam("id") String userid, @QueryParam("pwd") String password) {
		UserSession newSession = SessionHandler.getInstance().createSessionForUser(userid, password);
		if(newSession == null) {
			return Response.status(401).entity("unautorized user or password").build();
		}
		JsonObject ret = Json.createObjectBuilder()
							.add("token", newSession.getToken())
							.add("admin", newSession.getUser().isAdmin())
							.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		return Response.status(200).entity("").build();
	}
	
	@POST
	@Path("{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		return Response.status(200).entity("").build();
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
		return Response.status(200).entity("").build();
	}
}
