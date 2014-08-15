package server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/simulation")
public class SimulatorService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response listRoutines(@QueryParam("token") String tkn) {
		return Response.status(200).entity("routines").build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("routine:"+ routineid).build();
	}
	
	@GET
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getRoutineState(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("routine:"+ routineid + "state").build();
	}
	
	@POST
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setRoutineState(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("routine:"+ routineid + " state in").build();
	}
	
	@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response addRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("routine:"+ routineid).build();
	}
	
	@POST
	@Path("{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response updateRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("routine:"+ routineid).build();
	}
	
	@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		return Response.status(200).entity("routine:"+ routineid).build();
	}
}
