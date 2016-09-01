//package com.hcl.tokbox;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//public class Utility {
//
//	public static void setSession(String sessionId) {
//		SessionStore session = new SessionStore();
//		session.setmSessionId(sessionId);
//		try {
//			FileOutputStream fileOut = new FileOutputStream("/webapps/session.ser");
//			ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			out.writeObject(session);
//			out.close();
//			fileOut.close();
//			System.out.printf("Serialized data is saved in /webapps/session.ser");
//		} catch (IOException i) {
//			i.printStackTrace();
//		}
//	}
//
//	public static String getSessionID() {
//		SessionStore s = null;
//		try {
//			FileInputStream fileIn = new FileInputStream("/temp/session.ser");
//			ObjectInputStream in = new ObjectInputStream(fileIn);
//			s = (SessionStore) in.readObject();
//			in.close();
//			fileIn.close();
//		} catch (IOException i) {
//			i.printStackTrace();
//			return null;
//		} catch (ClassNotFoundException c) {
//			System.out.println("Session class not found");
//			c.printStackTrace();
//			return null;
//		}
//		return s.getmSessionId();
//	}
//}
