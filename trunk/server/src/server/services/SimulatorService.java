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

import server.model.C;
import server.model.Profile;
import server.model.SessionHandler;
import server.model.SimulationMgr;
import server.model.SimulationRoutine;
import server.model.UserSession;

@Path("/simulation")
public class SimulatorService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	public Response listRoutines(@QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		List<String> sims = newSession.getAdmin().listAllSimulators();
		if(sims == null) {
			return Response.status(500).entity("not found").build();
		}
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = sims.iterator();it.hasNext();) {
			Profile prf = Profile.getFromDB(it.next());
			JsonObject jo = Json.createObjectBuilder().add("id", prf.getId())
								.add("name", prf.getName()).build();
			builder.add(jo);
		}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		SimulationRoutine sr = newSession.getUser().getSimulator(routineid);
		if(sr == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			sr = newSession.getAdmin().getSimulator(routineid);
			if(sr == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		return Response.status(200).entity(sr.asJson().toString()).build();
	}
	
	@GET
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRoutineState(@PathParam("id") String routineid, @QueryParam("token") String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		SimulationRoutine sr = newSession.getUser().getSimulator(routineid);
		if(sr == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			sr = newSession.getAdmin().getSimulator(routineid);
			if(sr == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		boolean state = (SimulationMgr.getInstance().getSimulationWithId(routineid) != null);
		JsonObject ret = Json.createObjectBuilder()
				.add("id", routineid)
				.add("state", state)
				.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@POST
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setRoutineState(@PathParam("id") String routineid, @QueryParam("enabled")boolean enabled, @QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		SimulationRoutine sr = newSession.getUser().getSimulator(routineid);
		if(sr == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			sr = newSession.getAdmin().getSimulator(routineid);
			if(sr == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		if(enabled) {
			SimulationMgr.getInstance().addSimulation(sr);
		} else {
			SimulationMgr.getInstance().removeSimulation(sr);
		}
		JsonObject ret = Json.createObjectBuilder()
				.add("id", routineid)
				.add("state", enabled)
				.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	/*@POST
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		JsonObject params = C.getJsonFromString(body);
		if(!newSession.getAdmin().addSimulator(routineid, params)) {
			Response.status(200).entity("routine:"+ routineid + " already exist").build();
		}
		return Response.status(200).entity("routine:"+ routineid).build();
	}*/
	
	@POST
	//@Path("{id}/update")
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		JsonObject params = C.getJsonFromString(body);
		SimulationRoutine sr = newSession.getAdmin().getSimulator(routineid);
		if(sr == null) {
			//then add it
			if(!newSession.getAdmin().addSimulator(routineid, params)) {
				Response.status(200).entity("server error").build();
			}
			return Response.status(200).entity("routine:"+ routineid).build();
		}
		sr.updateWithJson(params, true);
		return Response.status(200).entity("routine:"+ routineid).build();
	}
	
	/*@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteRoutine(@PathParam("id") String routineid, @QueryParam("token") String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		if(!newSession.getAdmin().deleteSimulator(routineid)) {
			Response.status(500).entity("not found").build();
		}
		return Response.status(200).entity("routine:"+ routineid + " deleted").build();
	}*/
}
