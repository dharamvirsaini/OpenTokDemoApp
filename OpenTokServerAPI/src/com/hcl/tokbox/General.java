package com.hcl.tokbox;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.opentok.Archive;
import com.opentok.MediaMode;
import com.opentok.OpenTok;
import com.opentok.Role;
import com.opentok.Session;
import com.opentok.SessionProperties;
import com.opentok.TokenOptions;
import com.opentok.exception.OpenTokException;

@Path("/session")
public class General {
	OpenTok opentok;
	int apiKey = 45644842; // YOUR API KEY
	String apiSecret = "329a0c2e95f38a9dd4c06eab96a8564d62318331";
	@GET
	@Path("/getToken")
	@Produces("application/json")
	public Response createOpenTokSession() {
		String mStoreSessionId = "1_MX40NTY0NDg0Mn5-MTQ3MjczMDQzNzkxNH5oalBoNHJER0tITE12eEx2SGl3Q01YQVJ-fg";
		int apiKey = 45630132; // YOUR API KEY
		String apiSecret = "77a2b6bc6c63ceb507e64e86b5f854d1642048a0";
		opentok = new OpenTok(apiKey, apiSecret);
		Session session = null;
		String token = "";
		try {
		
			String sessionId = mStoreSessionId;// Utility.getSessionID();
			if (sessionId == null || sessionId.equalsIgnoreCase("")) {
				session = opentok.createSession(new SessionProperties.Builder().mediaMode(MediaMode.ROUTED).build());
				sessionId = session.getSessionId();
				System.out.println("SessionID" + sessionId);
				// Utility.setSession(sessionId);
				mStoreSessionId = sessionId;
			}
			token = opentok.generateToken(sessionId, new TokenOptions.Builder().role(Role.PUBLISHER)
					.expireTime((System.currentTimeMillis() / 1000L) + (3 * 60 * 60)).build());
			System.out.println("token" + token);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Session", sessionId);// sessionId
			jsonObject.put("token", token);
			String result = jsonObject.toString();
			return Response.status(200).entity(result).build();

		} catch (OpenTokException e1) {
			e1.printStackTrace();
			System.out.println("Exception" + e1.getMessage());
			return Response.status(404).entity("Could not generate").build();

		}
	}
	
	@GET
	@Path("/startArchive/{sessionID}")
	@Produces("application/json")
	public Response startArchive(@PathParam("sessionID") String sessionID) {
		opentok = new OpenTok(apiKey, apiSecret);
		Archive archive = null;
		Session session = null;
		JSONObject jsonObject = new JSONObject();
		try {
			session = opentok.createSession(new SessionProperties.Builder().mediaMode(MediaMode.ROUTED).build());
			archive = opentok.startArchive(sessionID);
		} catch (OpenTokException e) {
			// TODO Auto-generated catch block
			jsonObject.put("error",e.getMessage());
		}

		// Store this archiveId in the database for later use
		String archiveId = archive.getId();
		
		jsonObject.put("id", archiveId);// sessionId
		//jsonObject.put("token", token);
		String result = jsonObject.toString();
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Path("/stopArchive/{id}")
	@Produces("application/json")
	public void stopArchive(@PathParam("id") String archiveID) {
		opentok = new OpenTok(apiKey, apiSecret);
		try {
			opentok.stopArchive(archiveID);
		} catch (OpenTokException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
