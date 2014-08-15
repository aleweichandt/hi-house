package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/profiles")
public class ProfileService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response listProfiles(@QueryParam("token")String tkn) {
		return Response.status(200).entity("profiles").build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("profile:"+ profileid).build();
	}
	
	@GET
	@Path("/{id}/devices")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getProfileDevices(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("profile:"+ profileid + "devices").build();
	}
	
	@POST
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response addProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("profile:"+ profileid).build();
	}
	
	@POST
	@Path("/{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response updateProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("profile:"+ profileid).build();
	}
	
	@POST
	@Path("/{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProfile(@PathParam("id") String profileid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("profile:"+ profileid).build();
	}
}
