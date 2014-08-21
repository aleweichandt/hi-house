package server.services;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		if(newSession != null) {
			List<String> users = newSession.listAllUsers();
			if(users != null) {
				return Response.status(200).entity("users").build();
			}
			else {
				return Response.status(403).entity("no admin rights").build();
			}
		} 
		else {
			return Response.status(401).entity("invalid token").build();
		}
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			User user = newSession.getUser(userid);
			if(user != null) {
				return Response.status(200).entity(user.asJson().toString()).build();
			}
			else {
				return Response.status(404).entity("user not found").build();
			}
		}
		else {
			return Response.status(401).entity("invalid token").build();
		}
	}
	
	@GET
	@Path("{id}/profiles")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserProfiles(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			User user = newSession.getUser(userid);
			if(user != null) {
				Set<String> profiles = user.getProfiles();
				JsonArrayBuilder builder = Json.createArrayBuilder();
				for(Iterator<String> it = profiles.iterator();it.hasNext();) {
					builder.add(it.next());
				}
				return Response.status(200).entity(builder.build().toString()).build();
			}
			else {
				return Response.status(404).entity("user not found").build();
			}
		}
		else {
			return Response.status(401).entity("invalid token").build();
		}
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response loginUser(@QueryParam("id") String userid, @QueryParam("pwd") String password) {
		UserSession newSession = SessionHandler.getInstance().createSessionForUser(userid, password);
		if(newSession != null) {
			JsonObject ret = Json.createObjectBuilder()
								.add("token", newSession.getToken())
								.add("admin", newSession.getUser().isAdmin())
								.build();
			return Response.status(200).entity(ret.toString()).build();
		}
		else {
			return Response.status(401).entity("unautorized user or password").build();
		}
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(401).entity("invalid token").build();
		}
	}
	
	@POST
	@Path("{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(401).entity("invalid token").build();
		}
	}
	
	@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(401).entity("invalid token").build();
		}
	}
}
