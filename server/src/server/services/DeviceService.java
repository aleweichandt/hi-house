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
import server.model.devices.Device;

@Path("/devices")
public class DeviceService {
	@GET
	@Path("/all")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response listDevices(@QueryParam("token")String tkn) {
		return Response.status(200).entity("devices").build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("device:"+ deviceid).build();
	}
	
	@GET
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response getDeviceState(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("device:"+ deviceid).build();
	}
	
	@POST
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response addDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("device:"+ deviceid).build();
	}
	
	@POST
	@Path("/{id}/update")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response updateDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("device:"+ deviceid).build();
	}
	
	@POST
	@Path("{id}/delete")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDevice(@PathParam("id") String deviceid, @QueryParam("token")String tkn) {
		return Response.status(200).entity("device:"+ deviceid).build();
	}
	
	@POST
	@Path("{id}/state")
	@Produces(MediaType.TEXT_PLAIN)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response setDeviceState(@PathParam("id") String deviceid, @QueryParam("enabled")boolean enabled, @QueryParam("token")String tkn) {
		return Response.status(200).entity("device:"+ deviceid).build();
	}
}
