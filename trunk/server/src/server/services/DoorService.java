package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/doors")
public class DoorService {
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getState(@PathParam("id") String doorid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("door"+ doorid + " state=on").build();
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setState(@PathParam("id") String doorid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("door:"+ doorid + " state changed").build();
	}
}
