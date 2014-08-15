package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import server.model.SessionHandler;
import server.model.UserSession;

@Path("/users")
public class UserService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response listUsers(@QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("users").build();
		}
		else {
			return Response.status(300).entity("invalid token").build();
		}
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(300).entity("invalid token").build();
		}
	}
	
	@GET
	@Path("{id}/profiles")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfiles(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid + "profiles").build();
		}
		else {
			return Response.status(300).entity("invalid token").build();
		}
	}
	
	@GET
	@Path("{id}/login")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response loginUser(@PathParam("id") String userid, @QueryParam("pwd") String password) {
		UserSession newSession = SessionHandler.getInstance().createSessionForUser(userid, password);
		if(newSession != null) {
			return Response.status(200).entity("user:" + userid + " logged in. token=" + newSession.getToken()).build();
		}
		else {
			return Response.status(404).entity("user:" + userid + " was not found").build();
		}
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(300).entity("invalid token").build();
		}
	}
	
	@POST
	@Path("{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(300).entity("invalid token").build();
		}
	}
	
	@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("id") String userid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession != null) {
			return Response.status(200).entity("user:"+ userid).build();
		}
		else {
			return Response.status(300).entity("invalid token").build();
		}
	}
}
