package com.toyama.includes.utilities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import com.toyama.includes.model.AHUStatus;

//import org.apache.http.conn.util.InetAddressUtils;

import com.toyama.includes.model.Camera;
import com.toyama.includes.model.IRBlaster;
import com.toyama.includes.model.IRChannel;
import com.toyama.includes.model.IRDevice;
import com.toyama.includes.model.IRIndex;
import com.toyama.includes.model.NodeSwitch;
import com.toyama.includes.model.Room;
import com.toyama.includes.model.RoomNode;
import com.toyama.includes.model.STBChannel;
import com.toyama.includes.model.Scene;
import com.toyama.includes.model.SceneIRIndex;
import com.toyama.includes.model.OneTouchController;
import com.toyama.includes.model.SceneSensor;
import com.toyama.includes.model.SceneSwitch;
import com.toyama.includes.model.Schedule;
import com.toyama.includes.model.Sensor;
import com.toyama.includes.model.SetTopBox;
import com.toyama.includes.model.User;
import com.easylogic.usbserial.driver.UsbSerialDriver;
import com.easylogic.usbserial.driver.UsbSerialPort;
import com.easylogic.usbserial.driver.UsbSerialProber;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class Utilities {
	public static byte[] getCommandBytes(byte deviceid,byte sn,byte length,byte nh,byte nl,byte switchState,byte cdeviceid) {
		byte[] b;
		byte exor=(byte)(Globals.toyamastx ^ deviceid ^ sn ^ Globals.command ^ length ^ switchState);
		byte addsum = (byte)(Globals.toyamastx + deviceid + sn + Globals.command + length + switchState+exor);
		// the 0x put in between nl and toyamastx is the new device id for light/curtain/sensor etc..
		b=new byte[] {nh,nl,cdeviceid,Globals.toyamastx,deviceid,sn,Globals.command,length,switchState,exor,addsum};

		return b;
	}

	public static byte[] getCommandBytesUpdateAll(byte deviceid,byte sn,byte length,byte nh,byte nl,byte[] switchStates,byte cdeviceid) {
		byte[] bytes = new byte[19];
		//byte exor=(byte)(Globals.toyamastx^deviceid^sn^Globals.updateall_command^length);
		byte exor=(byte)(Globals.toyamastx^deviceid^sn^Globals.command^length);
		for(byte b:switchStates) {
			exor^=b;
		}
		//byte addsum = (byte)(Globals.toyamastx+deviceid+sn+Globals.updateall_command+length+exor);
		byte addsum = (byte)(Globals.toyamastx+deviceid+sn+Globals.command+length+exor);
		for(byte b:switchStates) {
			addsum+=b;
		}
		// the 0x put in between nl and toyamastx is the new device id for light/curtain/sensor etc..
		//byte[] tempbytes=new byte[] {nh,nl,cdeviceid,Globals.toyamastx,deviceid,sn,Globals.updateall_command,length};
		byte[] tempbytes=new byte[] {nh,nl,cdeviceid,Globals.toyamastx,deviceid,sn,Globals.command,length};
		for(int i=0;i<tempbytes.length;i++) {
			bytes[i]=tempbytes[i];
		}
		for(int i=0;i<switchStates.length;i++) {
			bytes[tempbytes.length+i]=switchStates[i];
		}
		bytes[17]=exor;
		bytes[18]=addsum;
		return bytes;
	}

	public static byte[] getAHUCommandBytes(byte deviceid,byte subid,byte length,byte nh,byte nl,byte data,byte cdeviceid) {
		byte[] b;
		byte exor=(byte)(Globals.toyamastx ^ deviceid ^ subid ^ Globals.command ^ length ^ data);
		byte addsum = (byte)(Globals.toyamastx + deviceid + subid + Globals.command + length + data+exor);
		// the 0x put in between nl and toyamastx is the new device id for light/curtain/sensor etc..
		b=new byte[] {nh,nl,cdeviceid,Globals.toyamastx,deviceid,subid,Globals.command,length,data,exor,addsum};

		return b;
	}

	public static byte[] getStatusQueryBytes(int version,byte cdeviceid,byte deviceid,byte nh,byte nl) {
		byte[] b;
		// the 0x put in between nl and toyamastx is the new device id for light/curtain/sensor etc..
		switch(version) {
			case 1:
				b=new byte[] {nh,nl,cdeviceid,Globals.StatusQueryV1[0],deviceid,Globals.StatusQueryV1[2],
						Globals.StatusQueryV1[3],Globals.StatusQueryV1[4],Globals.StatusQueryV1[5],Globals.StatusQueryV1[6]};
				break;
			case 2:
				b=new byte[] {nh,nl,cdeviceid,Globals.StatusQueryV2[0],deviceid,Globals.StatusQueryV2[2],
						Globals.StatusQueryV2[3],Globals.StatusQueryV2[4],Globals.StatusQueryV2[5],Globals.StatusQueryV2[6]};
				break;
			default:
				b=new byte[] {nh,nl,cdeviceid,Globals.StatusQueryV1[0],deviceid,Globals.StatusQueryV1[2],
						Globals.StatusQueryV1[3],Globals.StatusQueryV1[4],Globals.StatusQueryV1[5],Globals.StatusQueryV1[6]};
				break;
		}
		return b;
	}

	public static byte[] getAHUStatusQueryBytes(byte cdeviceid,byte deviceid,byte nh,byte nl) {
		byte[] b;
		// the 0x put in between nl and toyamastx is the new device id for light/curtain/sensor etc..
		b=new byte[] {nh,nl,cdeviceid,Globals.AHUStatusQuery[0],deviceid,Globals.AHUStatusQuery[2],
				Globals.AHUStatusQuery[3],Globals.AHUStatusQuery[4],Globals.AHUStatusQuery[5],Globals.AHUStatusQuery[6],
				Globals.AHUStatusQuery[7]};
		return b;
	}

	public static byte[] getIRCommandBytes(byte index,int channel,byte nh,byte nl) {
		byte[] b;
		String sn=String.valueOf(1)+String.valueOf(channel);
		byte exor=(byte)(Globals.toyamastx ^ Globals.deviceid ^ (byte)Byte.parseByte(sn, 16) ^ Globals.command ^ (byte)0x01 ^ index);
		byte addsum = (byte)(Globals.toyamastx + Globals.deviceid + (byte)Byte.parseByte(sn, 16) + Globals.command + (byte)0x01 + index+exor);
		b=new byte[] {nh,nl,(byte)0x0A,Globals.toyamastx,Globals.deviceid,(byte)Byte.parseByte(sn, 16),Globals.command,(byte)0x01,index,exor,addsum};
		return b;
	}

	public static List<UsbSerialPort> getUSBSerialPorts() {
		final List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();	
		final List<UsbSerialDriver> drivers =
				UsbSerialProber.getDefaultProber().findAllDrivers(Globals.usbManager);

		//final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
		for (final UsbSerialDriver driver : drivers) {
			final List<UsbSerialPort> ports = driver.getPorts();
			mEntries.addAll(ports);
		}
		return mEntries;
	}

	static final protected char[] hexArray ={
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	public static String bytesToHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		String tempString=new String(hexChars);
		String returnString="";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<tempString.length();i++) {
			sb.append(tempString.charAt(i));
			if(i%2==1 && i<tempString.length()-1)
				sb.append(":");
		}
		returnString=sb.toString();
		return returnString;
	}

	public static String bytesToHexString(ArrayList<Byte> bytes) {
		char[] hexChars = new char[bytes.size() * 2];
		for ( int j = 0; j < bytes.size(); j++ ) {
			int v = bytes.get(j) & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		String tempString=new String(hexChars);
		String returnString="";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<tempString.length();i++) {
			sb.append(tempString.charAt(i));
			if(i%2==1 && i<tempString.length()-1)
				sb.append(":");
		}
		returnString=sb.toString();
		return returnString;
	}

	public static int getBit(byte source,int position) {
		return ((source >> position) & 1);
	}

	public static boolean isMSBSet(byte source) {
		return ((source >> 7) & 1)==1;
	}

	public static String addSpaces(String hexString) {
		String result="";
		for(int i=0; i<hexString.length(); i++) {
			if(i%2==0) {
				result+=hexString.indexOf(i);
			} else {
				result+=hexString.indexOf(i)+' ';
			}
		}
		return result;
	}

	public static Scene buildSceneToSave(String scenestr) throws Exception {
		String sprop="";
		String[] sprops;
		String ssprop="";
		String[] ssprops;
		Scene s=new Scene();
		SceneSwitch ss=new SceneSwitch();
		SceneSensor ssor=new SceneSensor();
		SceneIRIndex si=new SceneIRIndex();

		try {
			if(scenestr.startsWith("<Scene>")) {
				scenestr=scenestr.replace("<Scene>","");
				if(scenestr.endsWith("</Scene>")) {
					scenestr=scenestr.replace("</Scene>","");
				} else {
					throw new Exception("Scene not properly received. Pls try again.");
				}

				sprop=scenestr.substring(0,scenestr.indexOf("<SceneSwitches>"));
				sprops=sprop.split("\\*");
				s=new Scene();
				if(!sprop.isEmpty()) {
					s.SceneId=Integer.valueOf(sprops[0].trim());
					s.SceneName=sprops[1].trim();
				} else {
					s.SceneName="Error in Scene";
				}

				String sceneswistr=scenestr.substring(scenestr.indexOf("<SceneSwitches>"),scenestr.indexOf("<SceneSensors>")); // removing room props from this room string

				if(sceneswistr.startsWith("<SceneSwitches>")) {
					sceneswistr=sceneswistr.replace("<SceneSwitches>","");
					if(sceneswistr.endsWith("</SceneSwitches>")) {
						sceneswistr=sceneswistr.replace("</SceneSwitches>","");
					}
					String[] ssArray=sceneswistr.split("<ETXSceneSwitch>");
					for(String sswitchstr:ssArray) {
						ssprop=sswitchstr;//.substring(0,sswitchstr.indexOf("<NodeSwitches>"));
						ssprops=sswitchstr.split("\\*");
						ss=new SceneSwitch();
						if(!ssprop.isEmpty()) {
							ss.SceneSwitchId=Integer.valueOf(ssprops[0]);
							ss.NodeSwitchId=Integer.valueOf(ssprops[1]);
							ss.Level=Integer.valueOf(ssprops[2]);
							ss.IsOn=(Integer.valueOf(ssprops[3].trim()).equals(1));
							if(!ss.IsOn) ss.Level=0;
						}
						s.SceneSwitches.add(ss);
					}
				}

				//String scenesenstr=scenestr.substring(scenestr.indexOf("<SceneSensors>")); // removing room props from this room string
				String scenesenstr=scenestr.substring(scenestr.indexOf("<SceneSensors>"),scenestr.indexOf("<SceneIRIndices>"));
				if(scenesenstr.startsWith("<SceneSensors>")) {
					scenesenstr=scenesenstr.replace("<SceneSensors>","");
					if(scenesenstr.endsWith("</SceneSensors>")) {
						scenesenstr=scenesenstr.replace("</SceneSensors>","");
					}
					String[] ssArray=scenesenstr.split("<ETXSceneSensor>");
					for(String sswitchstr:ssArray) {
						ssprop=sswitchstr;
						ssprops=sswitchstr.split("\\*");
						ssor=new SceneSensor();
						if(!ssprop.isEmpty()) {
							ssor.SceneSensorId=Integer.valueOf(ssprops[0]);
							ssor.SensorId=Integer.valueOf(ssprops[1]);
							ssor.IsArmed=(1==Integer.valueOf(ssprops[2].trim()));
						}
						if(ssor.SensorId!=0) {
							s.SceneSensors.add(ssor);
						}
					}
				}
				String sceneirstr=scenestr.substring(scenestr.indexOf("<SceneIRIndices>"));
				if(sceneirstr.startsWith("<SceneIRIndices>")) {
					sceneirstr=sceneirstr.replace("<SceneIRIndices>","");
					if(sceneirstr.endsWith("</SceneIRIndices>")) {
						sceneirstr=sceneirstr.replace("</SceneIRIndices>","");
						String[] ssArray=sceneirstr.split("<ETXSceneIRIndex>");
						for(String sswitchstr:ssArray) {
							ssprop=sswitchstr;
							ssprops=sswitchstr.split("\\*");
							si=new SceneIRIndex();
							if(!ssprop.isEmpty()) {
								si.SceneIRIndexId=Integer.valueOf(ssprops[0].trim());
								si.IRIndexId=Integer.valueOf(ssprops[1].trim());
								si.Key=ssprops[2].trim();
								si.Value=ssprops[3].trim();
							}
							if(si.IRIndexId!=0)
								s.SceneIRIndices.add(si);
						}
					}
				}
			}
		} catch(Exception ex) {
			throw ex;
		} finally {
		}
		//returnString+="";
		return s;
	}

	public static Scene getScene(int sceneId) {
		for (Scene s: Globals.AllScenes) {
			if (s.SceneId==sceneId) {
				return s;
			}
		}
		return null;
	}

	public static Sensor getSensor(byte[] nodeid) {
		for (Sensor s: Globals.AllSensors) {
			if (s.NodeIdHigher==nodeid[0] && s.NodeIdLower==nodeid[1]) {
				return s;
			}
		}
		return null;
	}

	public static Sensor getSensor(int sid) {
		for (Sensor s: Globals.AllSensors) {
			if (s.SensorId==sid) {
				return s;
			}
		} 
		return null;
	}
	
	public static int getSensorIndex(int sid) {
		for(int i=0;i<Globals.AllSensors.size();i++) {
			if (Globals.AllSensors.get(i).SensorId==sid) {
				return i;
			}
		} 
		return -1;
	}
		
	public static User getUser(int uid) {
		for (User u: Globals.AllUsers) {
			if (u.UserId==uid) {
				return u;
			}
		}
		return null;
	}

	public static User getUser(String uname) {
		for (User u: Globals.AllUsers) {
			if (u.Username.equals(uname)) {
				return u;
			}
		}
		return null;
	}

	public static IRBlaster getIRBlaster(int irbid) {
		for (IRBlaster irb: Globals.AllIRBlasters) {
			if (irb.IRBlasterId==irbid) {
				return irb;
			}
		}
		return null;
	}

	public static IRBlaster getIRBlasterForIRIndex(int irid) {
		for (IRBlaster irb: Globals.AllIRBlasters) {
			for(IRIndex iri:irb.IRIndices) {
				if (iri.IRIndexId==irid) {
					return irb;
				}	
			}
		}
		return null;
	}

	public static IRIndex getIRIndexForChannelNumber(int chnum) {
		for (IRBlaster irb: Globals.AllIRBlasters) {
			for(IRIndex iri:irb.IRIndices) {
				if(iri.RemoteButton.equals("Number") && iri.Value==chnum) {
					return iri;
				}
			}
		}
		return null;
	}

	public static STBChannel getThisSTBChannel(int chid) {
		for (SetTopBox s: Globals.AllSetTopBoxes) {
			for (STBChannel ch: s.Channels) {
				if (ch.STBChannelId==chid) {
					return ch;
				}
			}	
		}
		return null;
	}

	public static int getSTBId(String stbname) {
		for (SetTopBox s: Globals.AllSetTopBoxes) {
			if (s.Name.equals(stbname)) {
				return s.SetTopBoxId;
			}
		}
		return -1;
	}

	public static IRIndex getIRIndex(int irid) {
		for(IRBlaster irb:Globals.AllIRBlasters) {
			for (IRIndex iri: irb.IRIndices) {
				if (iri.IRIndexId==irid) {
					return iri;
				}
			}	
		}
		return null;
	}

	public static IRBlaster getIRBlasterInRoom(int rid) {
		for (IRBlaster irb: Globals.AllIRBlasters) {
			if (irb.RoomId==rid) {
				return irb;
			}
		}
		return null;
	}

	public static Room getRoom(int rid) {
		for (Room r: Globals.AllRooms) {
			if (r.RoomId==rid) {
				return r;
			}
		}
		return null;
	}

	public static String getRoomName(int rid) {
		for (Room r: Globals.AllRooms) {
			if (r.RoomId==rid) {
				return r.RoomName;
			}
		}
		return "";
	}

	public static int getRoomId(int nsid) {
		for (Room r: Globals.AllRooms) {
			for(RoomNode rn:r.RoomNodes) {
				for(NodeSwitch ns:rn.NodeSwitches) {
					if (ns.NodeSwitchId==nsid) {
						return r.RoomId;
					}
				}
			}
		}
		return -1;
	}

	public static Room getRoomForRoomNode(int rnid) throws Exception {
		try {
			for(Room r:Globals.AllRooms) {
				for(RoomNode rn: r.RoomNodes) {
					if(rn.RoomNodeId==rnid) {
						return r;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		} finally {

		}
	}

	public static RoomNode getRoomNode(int rnid) throws Exception {
		try {
			for(Room r:Globals.AllRooms) {
				for(RoomNode rn: r.RoomNodes) {
					if(rn.RoomNodeId==rnid) {
						return rn;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		} finally {

		}
	}

	public static RoomNode getRoomNodeForNodeSwitch(int nsid) throws Exception {
		try {
			for(Room r:Globals.AllRooms) {
				for(RoomNode rn: r.RoomNodes) {
					for(NodeSwitch ns:rn.NodeSwitches) {
						if(ns.NodeSwitchId==nsid) {
							return rn;
						}
					}
				}
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		} finally {

		}
	}

	public static RoomNode getRoomNode(byte[] nodeid) throws Exception {
		try {
			for(Room r:Globals.AllRooms) {
				for(RoomNode rn: r.RoomNodes) {
					if(rn.NodeIdHigher==nodeid[0] && rn.NodeIdLower==nodeid[1]) {
						return rn;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static AHUStatus getAHUStatus(int rnid) throws Exception {
		try {
			for(AHUStatus as:Globals.ahuStatuses) {
				if(as.roomNodeId==rnid) {
					return as;
				}
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static NodeSwitch getNodeSwitch(RoomNode rn,int sNumber) throws Exception {
		try {
			for(NodeSwitch ns: rn.NodeSwitches) {
				if(ns.SwitchNumber==sNumber) {
					return ns;
				}
			}
			return null;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static int getNodeSwitchesCount(Room r) {
		int count=0;
		for(RoomNode rn: r.RoomNodes) {
			if(rn.NodeType.equals("Bell") || rn.NodeType.equals("AHU") || rn.NodeType.equals("Door"))
				continue;
			for(NodeSwitch ns: rn.NodeSwitches) {
				if(ns.SwitchNumber>0) count++;
			}
		}
		return count;
	}

	public static int getNodeSwitchId(int rnid,int sNumber) throws Exception {
		try {
			for(Room r:Globals.AllRooms) {
				for(RoomNode rn:r.RoomNodes) {
					if(rn.RoomNodeId==rnid) {
						for(NodeSwitch ns: rn.NodeSwitches) {
							if(ns.SwitchNumber==sNumber) {
								return ns.NodeSwitchId;
							}
						}	
					}
				}
			}
			return 0;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static OneTouchController getOneTouchController(byte[] nodeid) {
		for(OneTouchController sm:Globals.AllOneTouchControllers) {
			if(sm.NodeIdHigher==nodeid[0] && sm.NodeIdLower==nodeid[1]) {
				return sm;
			}
		}
		return null;
	}

	public static Room getThisRoom(int rid) {
		for (Room r: Globals.AllRooms) {
			if (r.RoomId==rid) {
				return r;
			}
		}
		return null;
	}

	public static SetTopBox getThisSetTopBox(int stbid) {
		for (SetTopBox s: Globals.AllSetTopBoxes) {
			if (s.SetTopBoxId==stbid) {
				return s;
			}
		} 
		return null;
	}

	public static IRDevice getIRDevice(int irdid) {
		for (IRDevice ird: Globals.AllIRDevices) {
			if (ird.IRDeviceId==irdid) {
				return ird;
			}
		}
		return null;
	}

	public static IRDevice getClimateDevice(int irdid) {
		for (IRDevice ird: Globals.climateDevices) {
			if (ird.IRDeviceId==irdid) {
				return ird;
			}
		}
		return null;
	}

	public static IRDevice getMediaDevice(int irdid) {
		for (IRDevice ird: Globals.mediaDevices) {
			if (ird.IRDeviceId==irdid) {
				return ird;
			}
		}
		return null;
	}

	public static NodeSwitch getNodeSwitch(int nsid) {
		for (Room r: Globals.AllRooms) {
			for(RoomNode rn:r.RoomNodes) {
				for(NodeSwitch ns:rn.NodeSwitches) {
					if (ns.NodeSwitchId==nsid) {
						return ns;
					}
				}
			}
		}
		return null;
	}

	public static boolean isIRDeviceLinkedToNodeSwitch(IRDevice ird) {
		for (Room r: Globals.AllRooms) {
			if(r.RoomId!=ird.RoomId) continue;
			for(RoomNode rn:r.RoomNodes) {
				for(NodeSwitch ns:rn.NodeSwitches) {
					if (ns.IRDeviceId==ird.IRDeviceId) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static User getCurrentUser(int userId) {
		for (User u: Globals.AllUsers) {
			if (u.UserId==userId) {
				return u;
			}
		}
		return null;
	}

	public static Scene getCurrentScene(int sceneId) {
		for (Scene s: Globals.AllScenes) {
			if (s.SceneId==sceneId) {
				return s;
			}
		}
		return null;
	}

	public static Schedule getCurrentSchedule(int schId) {
		for (Schedule s: Globals.AllSchedules) {
			if (s.ScheduleId==schId) {
				return s;
			}
		}
		return null;
	}

	public static Camera getCamera(int cid) {
		for (Camera c: Globals.AllCameras) {
			if (c.cameraId==cid) {
				return c;
			}
		}
		return null;
	}

	public static String getIPAddress() throws Exception {// used by gateway
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();
					if (inetAddress.isSiteLocalAddress()) {
						ip=inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Exception ex=new Exception("Error in getting IP Version");
			throw ex;
			//throw e;//lblStatus.append(e.toString());
		}
		return ip;
	}

	public static String getLocalIPAddress() throws Exception {// used by twiz
		String ip = "";
		try { 
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						ip=inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			//e.printStackTrace();
			Exception ex=new Exception("Error in getting IP Version");
			throw ex;
			//throw e;//lblStatus.append(e.toString());
		}
		return ip; 
	}

	public static void parseLocalIPAddress() throws Exception {// used by twiz
		try {
			//currentIPAddress=getIPAddress();
			Globals.currentIPAddress=getLocalIPAddress();
			Globals.IPParts=Globals.currentIPAddress.split("\\.");
			if(Globals.IPParts.length==4) {
				Globals.ipPart4=Integer.valueOf(Globals.IPParts[3]);	
			} else {
				throw new Exception("Invalid IP Version.");
			}
		} catch(Exception ex) {
			throw ex;
		}
	}

	public static String generateAllRoomsString() {
		String returnString="";
		try {
			returnString+="<Rooms>"; // beginning of rooms
			for(Room r:Globals.AllRooms) {
				returnString+=String.valueOf(r.RoomId)+"*";
				returnString+=r.RoomName;
				returnString+="<RoomNodes>"; // beginning of room nodes
				for(RoomNode rn:r.RoomNodes) {
					returnString+=String.valueOf(rn.RoomNodeId)+"*";
					returnString+=String.valueOf(rn.NodeId)+"*";
					returnString+=String.valueOf(rn.Version)+"*";
					returnString+=String.valueOf(rn.MacIdHigher)+"*";
					returnString+=String.valueOf(rn.MacIdLower)+"*";
					returnString+=String.valueOf(rn.NodeIdHigher)+"*";
					returnString+=String.valueOf(rn.NodeIdLower)+"*";
					returnString+=rn.NodeType+"*";
					returnString+=rn.NodeName;
					returnString+="<NodeSwitches>"; // beginning of room nodes
					for(NodeSwitch ns:rn.NodeSwitches) {
						returnString+=String.valueOf(ns.NodeSwitchId)+"*";
						returnString+=ns.SwitchName+"*";
						returnString+=ns.Category+"*";
						returnString+=ns.Type+"*";
						returnString+=String.valueOf(ns.SwitchNumber)+"*";
						returnString+=ns.CustomValue+"*";
						returnString+=String.valueOf(ns.IRDeviceId);
						returnString+="<ETXNodeSwitch>"; // delimiter for each nodeswitch
					}
					returnString+="</NodeSwitches>"; // end of room nodes
					returnString+="<ETXRoomNode>"; // delimiter for each roomnode
				}
				returnString+="</RoomNodes>"; // end of room nodes
				returnString+="<ETXRoom>"; // delimiter for each room
			}
			returnString+="</Rooms>"; // beginning of rooms
		} catch(Exception ex) {
			ex.printStackTrace();
			returnString="";
		}
		return returnString;
	}

	public static String generateAllUsersString() {
		String returnString="";
		try {
			returnString+="<Users>"; // beginning of scenes
			for(User u:Globals.AllUsers) {
				returnString+=String.valueOf(u.UserId)+"*";
				returnString+=u.Username+"*";
				returnString+=u.Password+"*";
				returnString+=u.Firstname+"*";
				returnString+=u.Lastname+"*";
				returnString+=String.valueOf(u.Role);
				returnString+="<ETXUser>"; // delimiter for each scene
			}
			returnString+="</Users>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllScenesString() {
		String returnString="";
		try {
			returnString+="<Scenes>"; // beginning of scenes
			for(Scene s:Globals.AllScenes) {
				returnString+=String.valueOf(s.SceneId)+"*";
				returnString+=s.SceneName;
				returnString+="<SceneSwitches>"; // beginning of scene switches
				for(SceneSwitch ss:s.SceneSwitches) {
					returnString+=String.valueOf(ss.SceneSwitchId)+"*";
					returnString+=String.valueOf(ss.NodeSwitchId)+"*";
					returnString+=String.valueOf(ss.Level)+"*";
					returnString+=String.valueOf(ss.IsOn? 1 : 0);
					returnString+="<ETXSceneSwitch>"; // delimiter for each scene switch
				}
				returnString+="</SceneSwitches>"; // end of scene switches
				returnString+="<SceneSensors>"; // beginning of scene switches
				for(SceneSensor ss:s.SceneSensors) {
					returnString+=String.valueOf(ss.SceneSensorId)+"*";
					returnString+=String.valueOf(ss.SensorId)+"*";
					returnString+=String.valueOf(ss.IsArmed? 1 : 0);
					returnString+="<ETXSceneSensor>"; // delimiter for each scene switch
				}
				returnString+="</SceneSensors>"; // end of scene switches
				returnString+="<SceneIRIndices>"; // beginning of scene switches
				for(SceneIRIndex si:s.SceneIRIndices) {
					returnString+=String.valueOf(si.SceneIRIndexId)+"*";
					returnString+=String.valueOf(si.IRIndexId)+"*";
					returnString+=si.Key+"*";
					returnString+=si.Value;
					returnString+="<ETXSceneIRIndex>"; // delimiter for each scene switch
				}
				returnString+="</SceneIRIndices>"; // end of scene switches
				returnString+="<ETXScene>"; // delimiter for each scene
			}
			returnString+="</Scenes>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllSchedulesString() {
		String returnString="";
		try {
			returnString+="<Schedules>"; // beginning of scenes
			for(Schedule s:Globals.AllSchedules) {
				returnString+=String.valueOf(s.ScheduleId)+"*";
				returnString+=String.valueOf(s.SceneId)+"*";
				returnString+=String.valueOf(s.StartTime)+"*";
				String da="";
				for(int i=0;i<s.DatesApplicable.length;i++) {
					da+=String.valueOf(s.DatesApplicable[i])+",";
				}
				if(!da.equals("")) da=da.substring(0,da.lastIndexOf(","));
				returnString+=da+"*";
				returnString+=String.valueOf(s.CanRunAfterElapsed?1:0);
				returnString+="<ETXSchedule>"; // delimiter for each scene
			}
			returnString+="</Schedules>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllSensorsString() {
		String returnString="";
		try {
			returnString+="<Sensors>"; // beginning of scenes
			for(Sensor s:Globals.AllSensors) {
				returnString+=String.valueOf(s.SensorId)+"*";
				returnString+=s.SensorName+"*";
				returnString+=String.valueOf(s.RoomId)+"*";
				returnString+=s.Type+"*";
				returnString+=String.valueOf(s.Version)+"*";
				returnString+=String.valueOf(s.MacIdHigher)+"*";
				returnString+=String.valueOf(s.MacIdLower)+"*";
				returnString+=String.valueOf(s.NodeIdHigher)+"*";
				returnString+=String.valueOf(s.NodeIdLower)+"*";
				returnString+=String.valueOf(s.IsArmed? 1 : 0);
				returnString+="<ETXSensor>"; // delimiter for each scene
			}
			returnString+="</Sensors>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllIRDevicesString() {
		String returnString="";
		try {
			returnString+="<IRDevices>"; // beginning of scenes
			for(IRDevice s:Globals.AllIRDevices) {
				returnString+=String.valueOf(s.IRDeviceId)+"*";
				returnString+=String.valueOf(s.RoomId)+"*";
				returnString+=s.DeviceName+"*";
				returnString+=s.Category+"*";
				returnString+=s.Location+"*";
				returnString+=s.CustomValue;
				returnString+="<ETXIRDevice>"; // delimiter for each scene
			}
			returnString+="</IRDevices>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllIRBlastersString() {
		String returnString="";
		try {
			returnString+="<IRBlasters>"; // beginning of scenes
			for(IRBlaster irb:Globals.AllIRBlasters) {
				returnString+=String.valueOf(irb.IRBlasterId)+"*";
				returnString+=String.valueOf(irb.RoomId)+"*";
				returnString+=String.valueOf(irb.Version)+"*";
				returnString+=String.valueOf(irb.MacIdHigher)+"*";
				returnString+=String.valueOf(irb.MacIdLower)+"*";
				returnString+=String.valueOf(irb.NodeIdHigher)+"*";
				returnString+=String.valueOf(irb.NodeIdLower);
				returnString+="<IRIndices>";
				for(IRIndex iri:irb.IRIndices) {
					returnString+=String.valueOf(iri.IRIndexId)+"*";
					returnString+=String.valueOf(iri.IRDeviceId)+"*";
					returnString+=String.valueOf(iri.IRIndex)+"*";
					returnString+=String.valueOf(iri.IRChannel)+"*";
					returnString+=iri.RemoteButton+"*";
					returnString+=String.valueOf(iri.Value);
					returnString+="<ETXIRIndex>"; // delimiter for each scene
				}
				returnString+="</IRIndices>"; // end of scenes
				returnString+="<ETXIRBlaster>"; // delimiter for each scene
			}
			returnString+="</IRBlasters>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllSetTopBoxesString() {
		String returnString="";
		try {
			returnString+="<SetTopBoxes>"; // beginning of scenes
			for(SetTopBox s:Globals.AllSetTopBoxes) {
				returnString+=String.valueOf(s.SetTopBoxId)+"*";
				returnString+=s.Name;
				returnString+="<STBChannels>"; // beginning of scene switches
				for(STBChannel ch:s.Channels) {
					returnString+=String.valueOf(ch.STBChannelId)+"*";
					returnString+=String.valueOf(ch.Number)+"*";
					returnString+=ch.Name+"*";
					returnString+=ch.Filename+"*";
					returnString+=ch.Category+"*";
					returnString+=ch.Language;
					returnString+="<ETXSTBChannel>"; // delimiter for each scene switch
				}
				returnString+="</STBChannels>"; // end of scene switches
				returnString+="<ETXSetTopBox>"; // delimiter for each scene
			}
			returnString+="</SetTopBoxes>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String generateAllCamerasString() {
		String returnString="";
		try {
			returnString+="<Cameras>"; // beginning of scenes
			for(Camera u:Globals.AllCameras) {
				returnString+=String.valueOf(u.cameraId)+"*";
				returnString+=String.valueOf(u.roomId)+"*";
				returnString+=u.cameraName+"*";
				returnString+=u.cameraModel+"*";
				returnString+=String.valueOf(u.isVisitorCamera?1:0)+"*";
				returnString+=u.serialNumber+"*";
				returnString+=u.customValue;
				returnString+="<ETXCamera>"; // delimiter for each scene
			}
			returnString+="</Cameras>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static boolean fillUsersList(String sString) throws Exception {
		boolean isDone=false;
		String uprop="";
		String[] uprops;
		User u=new User();
		Globals.AllUsers.clear();
		try {
			if(sString.startsWith("<Users>")) {
				sString=sString.replace("<Users>","");
				if(sString.endsWith("</Users>")) {
					sString=sString.replace("</Users>","");
				}
				if(!sString.equals("")) {
					String[] rArray=sString.split("<ETXUser>");
					for(String Schedulestr:rArray) {
						uprop=Schedulestr;
						uprops=uprop.split("\\*");
						u=new User();
						if(!uprop.isEmpty()) {
							u.UserId=Integer.valueOf(uprops[0].trim());
							u.Username=uprops[1].trim();
							u.Password=uprops[2].trim();
							u.Firstname=uprops[3].trim();
							u.Lastname=uprops[4].trim();
							u.Role=Integer.valueOf(uprops[5].trim());
							Globals.AllUsers.add(u);
						}
					}
				}
				//				else {
				//					u=new User();
				//					u.UserId=0;
				//					u.Username="No Users";
				//					u.IsMaster=false;
				//					Globals.AllUsers.add(u);
				//					return true;
				//				}
			}
			isDone=true;
		} catch(Exception ex) {
			throw ex;
		} finally {

		}
		return isDone;
	}

	public static boolean fillRoomsList(String rString) {
		boolean isDone=false;
		String rprop="";
		String[] rprops;
		String rnprop="";
		String[] rnprops;
		String nsprop="";
		String[] nsprops;
		Room room=new Room();
		RoomNode rn=new RoomNode();
		NodeSwitch ns=new NodeSwitch();
		Globals.AllRooms.clear();
		try {
			if(rString.startsWith("<Rooms>")) {
				rString=rString.replace("<Rooms>","");
				if(rString.endsWith("</Rooms>")) {
					rString=rString.replace("</Rooms>","");
					if(!rString.isEmpty()) {
						String[] rArray=rString.split("<ETXRoom>");
						for(String roomstr:rArray) {
							rprop=roomstr.substring(0,roomstr.indexOf("<RoomNodes>"));
							rprops=rprop.split("\\*");
							room=new Room();
							if(!rprop.isEmpty()) {
								room.RoomId=Integer.valueOf(rprops[0].trim());
								room.RoomName=rprops[1].trim();

								roomstr=roomstr.substring(roomstr.indexOf("<RoomNodes>")); // removing room props from this room string
								if(roomstr.startsWith("<RoomNodes>")) {
									roomstr=roomstr.replace("<RoomNodes>","");
									if(roomstr.endsWith("</RoomNodes>")) {
										roomstr=roomstr.replace("</RoomNodes>","");
										String[] rnArray=roomstr.split("<ETXRoomNode>");
										for(String rnodestr:rnArray) {
											if(!rnodestr.isEmpty()) {
												rnprop=rnodestr.substring(0,rnodestr.indexOf("<NodeSwitches>"));
												rnprops=rnprop.split("\\*");
												rn=new RoomNode();
												if(!rnprop.isEmpty()) {
													rn.RoomNodeId=Integer.valueOf(rnprops[0]);
													rn.NodeId=Integer.valueOf(rnprops[1]);
													rn.Version =Integer.valueOf(rnprops[2]);
													rn.MacIdHigher=Byte.valueOf(rnprops[3]);
													rn.MacIdLower=Byte.valueOf(rnprops[4]);
													rn.NodeIdHigher=Byte.valueOf(rnprops[5]);
													rn.NodeIdLower=Byte.valueOf(rnprops[6]);
													rn.NodeType=rnprops[7];
													rn.NodeName=rnprops[8];
													rnodestr=rnodestr.substring(rnodestr.indexOf("<NodeSwitches>")); // removing roomnode props from this roomnode string

													if(rnodestr.startsWith("<NodeSwitches>")) {
														rnodestr=rnodestr.replace("<NodeSwitches>","");
														if(rnodestr.endsWith("</NodeSwitches>")) {
															rnodestr=rnodestr.replace("</NodeSwitches>","");
															String[] nsArray=rnodestr.split("<ETXNodeSwitch>");
															for(String nsstr:nsArray) {
																nsprop=nsstr;//.substring(0,ns.indexOf("<NodeSwitches>"));
																nsprops=nsprop.split("\\*");
																ns=new NodeSwitch();
																if(!nsprop.isEmpty()) {
																	ns.NodeSwitchId=Integer.valueOf(nsprops[0]);
																	ns.SwitchName=nsprops[1];
																	ns.Category=nsprops[2];
																	ns.Type=nsprops[3];
																	ns.SwitchNumber=Integer.valueOf(nsprops[4]);
																	ns.CustomValue=nsprops[5];
																	ns.IRDeviceId=Integer.valueOf(nsprops[6]);
																	ns.RoomName=room.RoomName;
																	rn.NodeSwitches.add(ns);
																}
															}
														}
													}
													room.RoomNodes.add(rn);
												}
											}
										}
									}
								}
								Globals.AllRooms.add(room);
							} else { // not added to all rooms. only for our reference
								room.RoomId=0;
								room.RoomName="Error in Room";
							}
						}
						isDone=true;
					}
				}
			}
		} catch(Exception ex) {
			//			String ex1 = "";
			//			for (StackTraceElement a1 : ex.getStackTrace()) {
			//				ex1 += a1.toString();
			//			}
			//lblStatus.setText(ex1);
			//String x = ex1;
			//ex.printStackTrace();
			//return "";
			isDone=false;
		}
		return isDone;
	}

	public static boolean fillScenesList(String sString) throws Exception {
		boolean isDone=false;
		String sprop="";
		String[] sprops;
		String ssprop="";
		String[] ssprops;
		Scene s=new Scene();
		SceneSwitch ss=new SceneSwitch();
		SceneSensor ssor=new SceneSensor();
		SceneIRIndex si=new SceneIRIndex();

		Globals.AllScenes.clear();
		try {
			if(sString.startsWith("<Scenes>")) {
				sString=sString.replace("<Scenes>","");
				if(sString.endsWith("</Scenes>")) {
					sString=sString.replace("</Scenes>","");	
					if(!sString.equals("")) {
						String[] rArray=sString.split("<ETXScene>");
						for(String scenestr:rArray) {
							sprop=scenestr.substring(0,scenestr.indexOf("<SceneSwitches>"));
							sprops=sprop.split("\\*");
							s=new Scene();
							if(!sprop.isEmpty()) {
								s.SceneId=Integer.valueOf(sprops[0].trim());
								s.SceneName=sprops[1].trim();

								String sceneswistr=scenestr.substring(scenestr.indexOf("<SceneSwitches>"),scenestr.indexOf("<SceneSensors>"));
								if(sceneswistr.startsWith("<SceneSwitches>")) {
									sceneswistr=sceneswistr.replace("<SceneSwitches>","");
									if(sceneswistr.endsWith("</SceneSwitches>")) {
										sceneswistr=sceneswistr.replace("</SceneSwitches>","");
										String[] ssArray=sceneswistr.split("<ETXSceneSwitch>");
										for(String sswitchstr:ssArray) {
											ssprop=sswitchstr;
											ssprops=sswitchstr.split("\\*");
											ss=new SceneSwitch();
											if(!ssprop.isEmpty()) {
												ss.SceneSwitchId=Integer.valueOf(ssprops[0].trim());
												ss.NodeSwitchId=Integer.valueOf(ssprops[1].trim());
												ss.Level=Integer.valueOf(ssprops[2].trim());
												ss.IsOn=(1==Integer.valueOf(ssprops[3].trim()));
												s.SceneSwitches.add(ss);
											}
										}
									}
								}
								String scenesenstr=scenestr.substring(scenestr.indexOf("<SceneSensors>"),scenestr.indexOf("<SceneIRIndices>"));
								if(scenesenstr.startsWith("<SceneSensors>")) {
									scenesenstr=scenesenstr.replace("<SceneSensors>","");
									if(scenesenstr.endsWith("</SceneSensors>")) {
										scenesenstr=scenesenstr.replace("</SceneSensors>","");
										String[] ssArray=scenesenstr.split("<ETXSceneSensor>");
										for(String sswitchstr:ssArray) {
											ssprop=sswitchstr;
											ssprops=sswitchstr.split("\\*");
											ssor=new SceneSensor();
											if(!ssprop.isEmpty()) {
												ssor.SceneSensorId=Integer.valueOf(ssprops[0].trim());
												ssor.SensorId=Integer.valueOf(ssprops[1].trim());
												ssor.IsArmed=(1==Integer.valueOf(ssprops[2].trim()));
												s.SceneSensors.add(ssor);
											}
										}
									}
								}
								String sceneirstr=scenestr.substring(scenestr.indexOf("<SceneIRIndices>"));
								if(sceneirstr.startsWith("<SceneIRIndices>")) {
									sceneirstr=sceneirstr.replace("<SceneIRIndices>","");
									if(sceneirstr.endsWith("</SceneIRIndices>")) {
										sceneirstr=sceneirstr.replace("</SceneIRIndices>","");
										String[] ssArray=sceneirstr.split("<ETXSceneIRIndex>");
										for(String sswitchstr:ssArray) {
											ssprop=sswitchstr;
											ssprops=sswitchstr.split("\\*");
											si=new SceneIRIndex();
											if(!ssprop.isEmpty()) {
												si.SceneIRIndexId=Integer.valueOf(ssprops[0].trim());
												si.IRIndexId=Integer.valueOf(ssprops[1].trim());
												si.Key=ssprops[2].trim();
												si.Value=ssprops[3].trim();
												s.SceneIRIndices.add(si);
											}
										}
									}
								}
								Globals.AllScenes.add(s);
							} else {
								s.SceneName="Error in Scene";
							}
						}
					} else {
						// no scenes are available
					}
				}
				isDone=true;
			}
		} catch(Exception ex) {
			throw ex;
		} finally {

		}
		return isDone;
	}

	public static boolean fillSchedulesList(String sString) throws Exception {
		boolean isDone=false;
		String sprop="";
		String[] sprops;
		Schedule s=new Schedule();
		String da="";
		String[] das;
		Globals.AllSchedules.clear();
		try {
			if(sString.startsWith("<Schedules>")) {
				sString=sString.replace("<Schedules>","");
				if(sString.endsWith("</Schedules>")) {
					sString=sString.replace("</Schedules>","");
					if(!sString.equals("")) {
						String[] rArray=sString.split("<ETXSchedule>");
						for(String Schedulestr:rArray) {
							sprop=Schedulestr;//.substring(0,Schedulestr.indexOf("<ScheduleSwitches>"));
							sprops=sprop.split("\\*");
							s=new Schedule();
							if(!sprop.isEmpty()) {
								s.ScheduleId=Integer.valueOf(sprops[0].trim());
								s.SceneId=Integer.valueOf(sprops[1].trim());;
								s.StartTime=Integer.valueOf(sprops[2].trim());
								da=sprops[3].trim();
								if(!da.equals("")) {
									das=da.split("\\,");
									for(int i=0;i<das.length;i++) {
										s.DatesApplicable[i]=Integer.valueOf(das[i]);
									}
								}
								s.CanRunAfterElapsed=(Integer.valueOf(sprops[4].trim())==1);
								Globals.AllSchedules.add(s);
							}
						}
					} else {
						// no Schedules are available
					}
				}
			}
			isDone=true;
		} catch(Exception ex) {
			throw ex;
		} finally {

		}
		return isDone;
	}

	public static boolean fillSensorsList(String sString) throws Exception {
		boolean isDone=false;
		String sprop="";
		String[] sprops;
		Sensor s=new Sensor();
		Globals.AllSensors.clear();
		try {
			if(sString.startsWith("<Sensors>")) {
				sString=sString.replace("<Sensors>","");
				if(sString.endsWith("</Sensors>")) {
					sString=sString.replace("</Sensors>","");
					if(!sString.equals("")) {
						String[] rArray=sString.split("<ETXSensor>");
						for(String Schedulestr:rArray) {
							sprop=Schedulestr;//.substring(0,Schedulestr.indexOf("<ScheduleSwitches>"));
							sprops=sprop.split("\\*");
							s=new Sensor();
							if(!sprop.isEmpty()) {
								s.SensorId=Integer.valueOf(sprops[0].trim());
								s.SensorName=sprops[1].trim();
								s.RoomId=Integer.valueOf(sprops[2].trim());
								s.Type=sprops[3].trim();
								s.Version =Integer.valueOf(sprops[4].trim());
								s.MacIdHigher=Byte.valueOf(sprops[5].trim());
								s.MacIdLower=Byte.valueOf(sprops[6].trim());
								s.NodeIdHigher=Byte.valueOf(sprops[7].trim());
								s.NodeIdLower=Byte.valueOf(sprops[8].trim());
								s.IsArmed=(1==Integer.valueOf(sprops[9].trim()));
								Globals.AllSensors.add(s);
							}
						}
					} else {
						// no Schedules are available
					}
				}
			}
			isDone=true;
		} catch(Exception ex) {
			throw ex;
		} finally {

		}
		return isDone;
	}

	public static boolean fillIRBlastersList(String irbString) {
		boolean isDone=false;
		String irbprop="";
		String[] irbprops;
		//String iriprop="";
		String[] iriprops;
		IRBlaster irb=new IRBlaster();
		IRIndex iri=new IRIndex();
		Globals.AllIRBlasters.clear();
		try {
			if(irbString.startsWith("<IRBlasters>")) {
				irbString=irbString.replace("<IRBlasters>","");
				if(irbString.endsWith("</IRBlasters>")) {
					irbString=irbString.replace("</IRBlasters>","");
					if(!irbString.isEmpty()) {
						String[] rArray=irbString.split("<ETXIRBlaster>");
						for(String irbstr:rArray) {
							irbprop=irbstr.substring(0,irbstr.indexOf("<IRIndices>"));
							irbprops=irbprop.split("\\*");
							irb=new IRBlaster();
							if(!irbprop.isEmpty()) {
								irb.IRBlasterId=Integer.valueOf(irbprops[0].trim());
								irb.RoomId=Integer.valueOf(irbprops[1]);
								irb.Version =Integer.valueOf(irbprops[2]);
								irb.MacIdHigher=Byte.valueOf(irbprops[3]);
								irb.MacIdLower=Byte.valueOf(irbprops[4]);
								irb.NodeIdHigher=Byte.valueOf(irbprops[5]);
								irb.NodeIdLower=Byte.valueOf(irbprops[6]);

								irbstr=irbstr.substring(irbstr.indexOf("<IRIndices>")); // removing IRBlaster props from this IRBlaster string
								if(irbstr.startsWith("<IRIndices>")) {
									irbstr=irbstr.replace("<IRIndices>","");
									if(irbstr.endsWith("</IRIndices>")) {
										irbstr=irbstr.replace("</IRIndices>","");
										String[] iriArray=irbstr.split("<ETXIRIndex>");
										for(String iristr:iriArray) {
											if(!iristr.isEmpty()) {
												iriprops=iristr.split("\\*");
												iri=new IRIndex();
												iri.IRIndexId=Integer.valueOf(iriprops[0].trim());
												iri.IRDeviceId=Integer.valueOf(iriprops[1].trim());
												iri.IRIndex=Byte.valueOf(iriprops[2].trim());
												iri.IRChannel=Integer.valueOf(iriprops[3].trim());
												iri.RemoteButton=iriprops[4].trim();
												iri.Value=Integer.valueOf(iriprops[5].trim());
												irb.IRIndices.add(iri);
											}
										}
									}
								}
								Globals.AllIRBlasters.add(irb);
							}
						}
						isDone=true;
					}
				}
			}
		} catch(Exception ex) {
			//			String ex1 = "";
			//			for (StackTraceElement a1 : ex.getStackTrace()) {
			//				ex1 += a1.toString();
			//			}
			//lblStatus.setText(ex1);
			//String x = ex1;
			//ex.printStackTrace();
			//return "";
			isDone=false;
		}
		return isDone;
	}

	public static boolean fillIRDevicesList(String sString) throws Exception {
		boolean isDone=false;
		String iprop="";
		String[] iprops;
		IRDevice ird=new IRDevice();
		Globals.AllIRDevices.clear();
		try {
			if(sString.startsWith("<IRDevices>")) {
				sString=sString.replace("<IRDevices>","");
				if(sString.endsWith("</IRDevices>")) {
					sString=sString.replace("</IRDevices>","");
					if(!sString.equals("")) {
						String[] rArray=sString.split("<ETXIRDevice>");
						for(String str:rArray) {
							iprop=str;//.substring(0,Schedulestr.indexOf("<ScheduleSwitches>"));
							iprops=iprop.split("\\*");
							ird=new IRDevice();
							if(!iprop.isEmpty()) {
								ird.IRDeviceId=Integer.valueOf(iprops[0].trim());
								ird.RoomId=Integer.valueOf(iprops[1].trim());
								ird.DeviceName=iprops[2].trim();
								ird.Category=iprops[3].trim();
								ird.Location=iprops[4].trim();
								ird.CustomValue=iprops[5].trim();
								Globals.AllIRDevices.add(ird);
							}
						}
					} else {
						// no ir devices
					}
				}
			}
			isDone=true;
		} catch(Exception ex) {
			throw ex;
		} finally {

		}
		return isDone;
	}

	public static boolean fillCamerasList(String sString) throws Exception {
		boolean isDone=false;
		String uprop="";
		String[] uprops;
		Camera c=new Camera();
		Globals.AllCameras.clear();
		try {
			if(sString.startsWith("<Cameras>")) {
				sString=sString.replace("<Cameras>","");
				if(sString.endsWith("</Cameras>")) {
					sString=sString.replace("</Cameras>","");
					if(!sString.equals("")) {
						String[] rArray=sString.split("<ETXCamera>");
						for(String Schedulestr:rArray) {
							uprop=Schedulestr;
							uprops=uprop.split("\\*");
							c=new Camera();
							if(!uprop.isEmpty()) {
								c.cameraId=Integer.valueOf(uprops[0].trim());
								c.roomId=Integer.valueOf(uprops[1].trim());
								c.cameraName=uprops[2].trim();
								c.cameraModel=uprops[3].trim();
								c.isVisitorCamera=(Integer.valueOf(uprops[4].trim())==1);
								c.serialNumber=uprops[5].trim();
								c.customValue=uprops[6].trim();
								Globals.AllCameras.add(c);
							}
						}
					} else {
						// no cameras
					}
				}
			}
			isDone=true;
		} catch(Exception ex) {
			throw ex;
		} finally {

		}
		return isDone;
	}

	public static String toTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}
			titleCase.append(c);
		} 
		return titleCase.toString();
	}

	public static boolean isUsernameExists(String un) {
		boolean isExists=false;
		for (User u: Globals.AllUsers) {
			if (u.Username.equals(un)) {
				isExists=true;
			}
		}
		return isExists;
	}

	public static ArrayList<IRIndex> filterIRIndices() {
		ArrayList<IRIndex> iris = new ArrayList<IRIndex>();
		for(IRBlaster irb:Globals.AllIRBlasters) {
			for(IRIndex iri:irb.IRIndices) {
				if(iri.IRDeviceId==Globals.currentIRDevice.IRDeviceId) {
					iris.add(iri);
				}
			}
		}
		return iris;
	}

	public static String buildSceneString(Scene s) {
		String returnString="";
		try {
			returnString+="<Scene>"; // beginning of scenes
			returnString+=String.valueOf(s.SceneId)+"*";
			returnString+=s.SceneName;
			returnString+="<SceneSwitches>"; // beginning of scene switches
			for(SceneSwitch ss:s.SceneSwitches) {
				returnString+=String.valueOf(ss.SceneSwitchId)+"*";
				returnString+=String.valueOf(ss.NodeSwitchId)+"*";
				returnString+=String.valueOf(ss.Level)+"*";
				returnString+=String.valueOf(ss.IsOn? 1 : 0);
				returnString+="<ETXSceneSwitch>"; // delimiter for each scene switch
			}
			returnString+="</SceneSwitches>"; // end of scene switches
			returnString+="<SceneSensors>"; // beginning of scene switches
			for(SceneSensor ss:s.SceneSensors) {
				returnString+=String.valueOf(ss.SceneSensorId)+"*";
				returnString+=String.valueOf(ss.SensorId)+"*";
				returnString+=String.valueOf(ss.IsArmed? 1 : 0);
				returnString+="<ETXSceneSensor>"; // delimiter for each scene switch
			}
			returnString+="</SceneSensors>"; // end of scene switches
			returnString+="<SceneIRIndices>"; // beginning of scene switches
			for(SceneIRIndex si:s.SceneIRIndices) {
				returnString+=String.valueOf(si.SceneIRIndexId)+"*";
				returnString+=String.valueOf(si.IRIndexId)+"*";
				returnString+=si.Key+"*";
				returnString+=si.Value;
				returnString+="<ETXSceneIRIndex>"; // delimiter for each scene switch
			}
			returnString+="</SceneIRIndices>"; // end of scene switches
			returnString+="</Scene>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static String buildScheduleString(Schedule s) {
		String returnString="";
		try {
			returnString+="<Schedule>"; // beginning of scenes
			returnString+=String.valueOf(s.ScheduleId)+"*";
			returnString+=String.valueOf(s.SceneId)+"*";
			returnString+=String.valueOf(s.StartTime)+"*";
			String da="";
			for(int i=0;i<s.DatesApplicable.length;i++) {
				da+=String.valueOf(s.DatesApplicable[i])+",";
			}
			if(!da.equals("")) da=da.substring(0,da.lastIndexOf(","));
			returnString+=da+"*";
			returnString+=String.valueOf(s.CanRunAfterElapsed?1:0);
			returnString+="</Schedule>"; // end of scenes
			return returnString;
		} catch(Exception ex) {
			return "";
		}
	}

	public static void initializeValuesForGateway() throws Exception {
		try {			
			Globals.remoteServiceCaller="gateway";
			Globals.roomsString="";
			Globals.scenesString="";
			Globals.schedulesString="";
			Globals.sensorsString="";
			Globals.irDevicesString="";
			Globals.irBlastersString="";

			Globals.SensorTypes.clear();
			Globals.SensorTypes.add(new String("Motion"));
			Globals.SensorTypes.add(new String("Gas"));
			Globals.SensorTypes.add(new String("Smoke"));
			Globals.SensorTypes.add(new String("Magnetic"));

			Globals.SwitchCategories.clear();
			Globals.SwitchCategories.add(new String("Light"));
			Globals.SwitchCategories.add(new String("Fan"));
			Globals.SwitchCategories.add(new String("Curtain"));
			Globals.SwitchCategories.add(new String("TV"));
			Globals.SwitchCategories.add(new String("AC"));
			Globals.SwitchCategories.add(new String("Fridge"));
			Globals.SwitchCategories.add(new String("Door"));
			Globals.SwitchCategories.add(new String("Microwave"));
			Globals.SwitchCategories.add(new String("Appliance"));
			Globals.SwitchCategories.add(new String("Washing Machine"));
			Globals.SwitchCategories.add(new String("Music System"));
			Globals.SwitchCategories.add(new String("Set Top Box"));
			Globals.SwitchCategories.add(new String("Projector"));
			Globals.SwitchCategories.add(new String("Chandelier"));
			Globals.SwitchCategories.add(new String("Geyser"));

			Globals.IRDeviceCategories.clear();
			Globals.IRDeviceCategories.add(new String("TV"));
			Globals.IRDeviceCategories.add(new String("AC"));
			Globals.IRDeviceCategories.add(new String("Music System"));
			Globals.IRDeviceCategories.add(new String("Set Top Box"));
			Globals.IRDeviceCategories.add(new String("Projector"));

			Globals.IRChannels.clear();
			Globals.IRChannels.add(new IRChannel("Channel 1",1));
			Globals.IRChannels.add(new IRChannel("Channel 2",2));
			Globals.IRChannels.add(new IRChannel("Channel 3",3));
			Globals.IRChannels.add(new IRChannel("Channel 4",4));
		} catch(Exception ex) {
			throw ex;
		}
	}

	public static void showMessage(TextView lbl,String msg) {
		lbl.append(msg+"\n");
	}

	public static boolean writeDataToFile(Context fileContext) {
		FileOutputStream fos;
		StringBuilder sb=new StringBuilder();
		try {
			sb.append(Globals.usersString).append("\n");
			sb.append(Globals.roomsString).append("\n");
			sb.append(Globals.scenesString).append("\n");
			sb.append(Globals.schedulesString).append("\n");
			sb.append(Globals.sensorsString).append("\n");
			sb.append(Globals.irDevicesString).append("\n");
			sb.append(Globals.irBlastersString).append("\n");
			sb.append(Globals.camerasString).append("\n");
			fos = fileContext.openFileOutput(Globals.dataFilename, Context.MODE_PRIVATE);
			fos.write(sb.toString().getBytes());
			fos.close();

			fillData();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean fillData() {
		try {
			Globals.isUsersLoaded=Utilities.fillUsersList(Globals.usersString);
			Globals.isRoomsLoaded=Utilities.fillRoomsList(Globals.roomsString);
			Globals.isScenesLoaded=Utilities.fillScenesList(Globals.scenesString);
			Globals.isSchedulesLoaded=Utilities.fillSchedulesList(Globals.schedulesString);
			Globals.isSensorsLoaded=Utilities.fillSensorsList(Globals.sensorsString);
			Globals.isIRDevicesLoaded=Utilities.fillIRDevicesList(Globals.irDevicesString);
			Globals.isIRBlastersLoaded=Utilities.fillIRBlastersList(Globals.irBlastersString);
			Globals.isCamerasLoaded=Utilities.fillCamerasList(Globals.camerasString);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getCurrentLocalDateTimeString() {
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH)+1;
		int day=cal.get(Calendar.DAY_OF_MONTH);
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		int min=cal.get(Calendar.MINUTE);
		int secs=cal.get(Calendar.SECOND);
		String monstr="";
		String daystr="";
		String hourstr="";
		String minstr="";
		String secstr="";
		if(month<10) { monstr="0"+String.valueOf(month); }
		else { monstr=String.valueOf(month); }
		if(day<10) { daystr="0"+String.valueOf(day); }
		else { daystr=String.valueOf(day); }
		if(hour<10) { hourstr="0"+String.valueOf(hour); }
		else { hourstr=String.valueOf(hour); }
		if(min<10) { minstr="0"+String.valueOf(min); }
		else { minstr=String.valueOf(min); }
		if(secs<10) { secstr="0"+String.valueOf(secs); }
		else { secstr=String.valueOf(secs); }
		return String.valueOf(year)+"-"+monstr+"-"+daystr+" "+hourstr+":"+minstr+":"+secstr;
	}

	public static String getCurrentLocalTimeString() {
		String hourstr="";
		String minstr="";
		String secstr="";
		Calendar cal=Calendar.getInstance();
		int hour=cal.get(Calendar.HOUR_OF_DAY);
		int min=cal.get(Calendar.MINUTE);
		int secs=cal.get(Calendar.SECOND);
		if(hour<10) { hourstr="0"+String.valueOf(hour); }
		else { hourstr=String.valueOf(hour); }
		if(min<10) { minstr="0"+String.valueOf(min); }
		else { minstr=String.valueOf(min); }
		if(secs<10) { secstr="0"+String.valueOf(secs); }
		else { secstr=String.valueOf(secs); }
		return hourstr+":"+minstr+":"+secstr;
	}

	public static long getCurrentLocalTimeInMilliSeconds() {
		Calendar cal=Calendar.getInstance();
		return cal.getTimeInMillis();
	}

	public static boolean isDeviceTablet(Context activityContext) {
		// Verifies if the Generalized Size of the device is XLARGE to be 
		// considered a Tablet 
		boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  
				(Configuration.SCREENLAYOUT_SIZE_XLARGE));
		// If XLarge, checks if the Generalized Density is at least MDPI 
		// (160dpi) 
		if (xlarge) {
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160, 
			// DENSITY_TV=213, DENSITY_XHIGH=320 
			if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
					|| metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_TV
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

				// Yes, this is a tablet! 
				return true; 
			} 
		}
		// No, this is not a tablet! 
		return false; 
	}

	public static boolean isInteger(String str) {
		try { 
			int num = Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it. 
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it 
		if (view == null) {
			view = new View(activity);
		} 
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	} 

	public static String getSha1Hex(String clearString) { 
		try { 
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(clearString.getBytes("UTF-8"));
			byte[] bytes = messageDigest.digest();
			StringBuilder buffer = new StringBuilder();
			for (byte b : bytes) { 
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			} 
			return buffer.toString();
		} catch (Exception ignored) { 
			ignored.printStackTrace();
			return null; 
		}
	} 
}