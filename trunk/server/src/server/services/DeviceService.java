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
import server.model.SessionHandler;
import server.model.UserSession;
import server.model.devices.Device;

@Path("/devices")
public class DeviceService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response listDevices(@QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		List<String> devices = newSession.getAdmin().listAllDevices();
		if(devices == null) {
			return Response.status(500).entity("not found").build();
		}
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = devices.iterator();it.hasNext();) {
			builder.add(it.next());
		}
		return Response.status(200).entity(builder.build().toString()).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		Device dv = newSession.getUser().getDevice(deviceid);
		if(dv == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			dv = newSession.getAdmin().getDevice(deviceid);
			if(dv == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		return Response.status(200).entity(dv.asJson().toString()).build();
	}
	
	@GET
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getDeviceState(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		Device dv = newSession.getUser().getDevice(deviceid);
		if(dv == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			dv = newSession.getAdmin().getDevice(deviceid);
			if(dv == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		boolean state = dv.getState();
		JsonObject ret = Json.createObjectBuilder()
				.add("id", deviceid)
				.add("state", state)
				.build();
		return Response.status(200).entity(ret.toString()).build();
	}
	
	@POST
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		
		JsonObject params = C.getJsonFromString(body);
		if(!newSession.getAdmin().addDevice(deviceid, params)){
			return Response.status(500).entity(deviceid + " already exist").build();
		}
		return Response.status(200).entity(deviceid + " added").build();
	}
	
	@POST
	@Path("/{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn, String body) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		Device dv = newSession.getAdmin().getDevice(deviceid);
		if(dv == null) {
			return Response.status(500).entity("not found").build();
		}
		JsonObject params = C.getJsonFromString(body);
		dv.updateWithParams(params, true);
		return Response.status(200).entity(deviceid + " updated").build();
	}
	
	@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		if(newSession.getAdmin() == null) {
			return Response.status(403).entity("no admin rights").build();
		}
		if(!newSession.getAdmin().deleteDevice(deviceid)) {
			return Response.status(500).entity("not found").build();
		}
		return Response.status(200).entity(deviceid + " removed").build();
	}
	
	@POST
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setDeviceState(@PathParam("id") String deviceid, @QueryParam("enabled")boolean enabled, @QueryParam("token")String tkn) {
		UserSession newSession = SessionHandler.getInstance().getSession(tkn);
		if(newSession == null) {
			return Response.status(401).entity("invalid token").build();
		}
		Device dv = newSession.getUser().getDevice(deviceid);
		if(dv == null) {
			if(newSession.getAdmin() == null) {
				return Response.status(403).entity("no admin rights").build();
			}
			dv = newSession.getAdmin().getDevice(deviceid);
			if(dv == null) {
				return Response.status(500).entity("not found").build();
			}
		}
		if(!dv.setState(enabled)) {
			return Response.status(500).entity("internal server error").build();
		}
		return Response.status(200).entity("").build();
	}
}
