package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/temperature")
public class AmbientService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getTemperature(@QueryParam("token") String tkn) {
		return Response.status(200).entity("system:1, real:2").build();
	}
	
	@POST
	@Path("/system")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setTemperature(@QueryParam("id") Float doorid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("door:"+ doorid + " state changed").build();
	}
}
