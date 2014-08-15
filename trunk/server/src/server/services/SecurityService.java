package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/security")
public class SecurityService {
	@GET
	@Path("/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getState(@QueryParam("token") String tkn) {
		return Response.status(200).entity("alarm state").build();
	}
	
	@GET
	@Path("/alarm_conf")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getAlarmConfig(@QueryParam("token") String tkn) {
		return Response.status(200).entity("alarm destination").build();
	}
	
	@POST
	@Path("/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setState(@QueryParam("token") String tkn) {
		return Response.status(200).entity("alarm state in").build();
	}
	
	@POST
	@Path("/alarm_conf")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setAlarmConfig(@QueryParam("token") String tkn) {
		return Response.status(200).entity("alarm destination in").build();
	}
}
