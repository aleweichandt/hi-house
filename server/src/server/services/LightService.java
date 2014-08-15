package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("lights")
public class LightService {
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getState(@PathParam("id") String lightid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("ligth:"+ lightid + " state=on").build();
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setState(@PathParam("id") String lightid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("light:"+ lightid + " state changed").build();
	}
}
