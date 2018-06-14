package com.toyama.includes.utilities;

import java.util.ArrayList;

import com.toyama.includes.model.Alarm;
import com.toyama.includes.model.AppliedSchedule;
import com.toyama.includes.model.Camera;
import com.toyama.includes.model.CameraModel;
import com.toyama.includes.model.CustomButton;
import com.toyama.includes.model.IRBlaster;
import com.toyama.includes.model.IRDevice;
import com.toyama.includes.model.IRIndex;
import com.toyama.includes.model.Node;
import com.toyama.includes.model.NodeSwitch;
import com.toyama.includes.model.Room;
import com.toyama.includes.model.RoomNode;
import com.toyama.includes.model.STBChannel;
import com.toyama.includes.model.Scene;
import com.toyama.includes.model.SceneIRIndex;
import com.toyama.includes.model.OneTouchController;
import com.toyama.includes.model.OneTouchControllerMapping;
import com.toyama.includes.model.SceneSensor;
import com.toyama.includes.model.SceneSwitch;
import com.toyama.includes.model.Schedule;
import com.toyama.includes.model.Sensor;
import com.toyama.includes.model.SetTopBox;
import com.toyama.includes.model.User;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBLayer {
	private SQLiteDatabase db = null;
	private ContentValues cv=null;
	String path, query;
	double fromDBVersion=0;
	ArrayList<String> existingTables = new ArrayList<String>();

	public DBLayer() throws Exception {
		try {
			existingTables = new ArrayList<String>();
			path = android.os.Environment.getExternalStorageDirectory().getPath()+"/tgate" + "/ToyamaGatewayDB.sqlite";
			this.db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean isTableExists(String tblName) throws Exception {
		try {
			String sql = "SELECT tbl_name FROM sqlite_master WHERE type='table' AND tbl_name='"+tblName+"'";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.close();
				return true;
			} else {
				cr.close();
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public boolean isColumnExists(String tblName,String colName) throws Exception {
		try {
			if(!isTableExists(tblName)) return false;
			String sql = "PRAGMA table_info("+tblName+")";
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					int index = cr.getColumnIndex("name");
		            if(index!=-1 && cr.getString(index).equals(colName)) {
		            	cr.close();
		                return true;
		            }
				} while (cr.moveToNext());
				cr.close();
			} else {
				cr.close();
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return false;
	}
	
	public void emptyDBData() throws Exception {
		try {
			getExistingTablesList();
			
			for(String t:existingTables) {
				switch(t) {
				case "Users":
					if(isColumnExists("Users","Role")) {
						query="DELETE FROM Users WHERE Role!=1";	
					} else {
						if(isColumnExists("Users","IsMaster")) {
							query="DELETE FROM Users WHERE IsMaster!='1'";
						} else {
							query="DELETE FROM Users";
						}
					}
					break;
				case "Nodes":
				case "Globals":
				case "CameraModels":
				case "SetTopBoxes":
				case "STBChannels":
					query="";
					break;
				default:
					query="DELETE FROM "+t;
					break;
				}
				if(!query.isEmpty()) {
					this.db.execSQL(query);		
				}
			}
			intializeGateway(Globals.gatewayId,Globals.serialNumber,Globals.dateTested,Globals.baudRate);
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	public void resetDB() throws Exception {
		try {
			getExistingTablesList();
			
			for(String t:existingTables) {
				switch(t) {
				case "Users":
					if(isColumnExists("Users","Role")) {
						query="DELETE FROM Users WHERE Role!=1";	
					} else {
						if(isColumnExists("Users","IsMaster")) {
							query="DELETE FROM Users WHERE IsMaster!='1'";
						} else {
							query="DELETE FROM Users";
						}
					}
					break;
				case "Nodes":
				case "Globals":
				case "CameraModels":
				case "SetTopBoxes":
				case "STBChannels":
					query="";
					break;
				default:
					query="DELETE FROM "+t;
					break;
				}
				if(!query.isEmpty()) {
					this.db.execSQL(query);		
				}
			}
			intializeGateway(0,"",0,9600);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void getExistingTablesList() throws Exception {
		existingTables.clear();
		this.db.beginTransaction();
		String tblName="";

		try {
			String sql = "SELECT * FROM sqlite_master WHERE type='table'";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					tblName = cr.getString(cr.getColumnIndex("tbl_name"));
					if(tblName.equals("Customers")) Globals.dbVersion=3.6;
					existingTables.add(tblName);
				} while (cr.moveToNext());
			}
			cr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			this.db.endTransaction();
		}
	}

	public boolean getExistingData() throws Exception {
		this.db.beginTransaction();
		try {
			if(!getGlobals()) return false;
			
			Globals.AllAlarms=getAllAlarms();
			Globals.AllCameras=getAllCameras();
			Globals.AllCameraModels=getAllCameraModels();
			//Globals.AllCustomButtons=getAllCustomButtons();
			Globals.AllIRBlasters=getAllIRBlasters();
			Globals.AllIRDevices=getAllIRDevices();
			Globals.AllNodes=getAllNodes();
			Globals.AllRooms=getAllRooms();
			Globals.AllOneTouchControllers=getAllOneTouchControllers();
			Globals.AllScenes=getAllScenes();
			Globals.AllSchedules=getAllSchedules();
			Globals.AllSensors=getAllSensors();
			Globals.AllSetTopBoxes=getAllSetTopBoxes();
			Globals.AllUsers=getAllUsers();
			Globals.AllAppliedSchedules=getAllAppliedSchedules();
			// write code to also save nodes,camera models, stbchannels and any others
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			this.db.endTransaction();
		}
		return true;
	}

	public void deleteExistingTables() throws Exception {
		this.db.beginTransaction();
		try {
			for(String t:existingTables) {
				query="DROP TABLE "+t;
				this.db.execSQL(query);
			}
			query="COMMIT";
			this.db.execSQL(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			//this.db.endTransaction();
		}
	}
	
	public void createTables() throws Exception {
		this.db.beginTransaction();
		try {
			String [] sqls=Globals.createDBSQL.split(";");
			for(String s:sqls) {
				if(!s.isEmpty()) this.db.execSQL(s);
			}
			query="COMMIT";
			this.db.execSQL(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			//this.db.endTransaction();
			// looks like if COMMIT is used, end transaction is not required. but COMMIT is required to DD queries
		}
	}
	
	public void deleteScene(int sid) throws Exception {
		try {
			query="DELETE FROM SceneSwitches WHERE SceneId="+String.valueOf(sid);
			this.db.execSQL(query);

			query="DELETE FROM SceneSensors WHERE SceneId="+String.valueOf(sid);
			this.db.execSQL(query);

			query="DELETE FROM Schedules WHERE SceneId="+String.valueOf(sid);
			this.db.execSQL(query);

			query="DELETE FROM Scenes WHERE SceneId="+String.valueOf(sid);
			this.db.execSQL(query);

		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteSensor(int sid) throws Exception {
		try {
			query="DELETE FROM SceneSensors WHERE SensorId="+String.valueOf(sid);
			this.db.execSQL(query);

			query="DELETE FROM Sensors WHERE SensorId="+String.valueOf(sid);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteCamera(int cid) throws Exception {
		try {
			query="DELETE FROM Cameras WHERE CameraId="+String.valueOf(cid);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void deleteIRBlaster(int irbid) throws Exception {
		try {
			query="DELETE FROM IRIndices WHERE IRBlasterId="+String.valueOf(irbid);
			this.db.execSQL(query);

			query="DELETE FROM IRBlasters WHERE IRBlasterId="+String.valueOf(irbid);
			this.db.execSQL(query);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void deleteSchedule(int sid) throws Exception {
		try {
			query="DELETE FROM Schedules WHERE ScheduleId="+String.valueOf(sid);
			this.db.execSQL(query);

			query="DELETE FROM AppliedSchedules WHERE ScheduleId="+String.valueOf(sid);
			this.db.execSQL(query);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteUser(int uid) throws Exception {
		try {
			query="DELETE FROM Users WHERE UserId="+String.valueOf(uid);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteUser(String un) throws Exception {
		try {
			query="DELETE FROM Users WHERE Username='"+un+"'";
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteUsers() throws Exception {
		try {
			if(isColumnExists("Users","Role")) {
				query="DELETE FROM Users WHERE Role!=1";	
			} else {
				if(isColumnExists("Users","IsMaster")) {
					query="DELETE FROM Users WHERE IsMaster!='1'";
				} else {
					query="DELETE FROM Users";
				}
			}
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteAlarm(int aid) throws Exception {
		try {
			query="DELETE FROM Alarms WHERE AlarmId="+String.valueOf(aid);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteRoom(Room r) throws Exception {
		try {
			for(RoomNode rn:r.RoomNodes) {
				query="DELETE FROM NodeSwitches WHERE RoomNodeId="+String.valueOf(rn.RoomNodeId);
				this.db.execSQL(query);

				query="DELETE FROM RoomNodes WHERE RoomNodeId="+String.valueOf(rn.RoomNodeId);
				this.db.execSQL(query);

				for(NodeSwitch ns:rn.NodeSwitches) { // deleting these node switches from sceneswitches
					query="DELETE FROM SceneSwitches WHERE NodeSwitchId="+String.valueOf(ns.NodeSwitchId);
					this.db.execSQL(query);
				}
			}

			query="DELETE FROM Cameras WHERE RoomId="+String.valueOf(r.RoomId);
			this.db.execSQL(query);

			query="DELETE FROM Sensors WHERE RoomId="+String.valueOf(r.RoomId);
			this.db.execSQL(query);

			query="DELETE FROM IRBlasters WHERE RoomId="+String.valueOf(r.RoomId);
			this.db.execSQL(query);

			query="DELETE FROM IRDevices WHERE RoomId="+String.valueOf(r.RoomId);
			this.db.execSQL(query);

			query="DELETE FROM Rooms WHERE RoomId="+String.valueOf(r.RoomId);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteRoomNode(RoomNode rn) throws Exception {
		try {
			query="DELETE FROM NodeSwitches WHERE RoomNodeId="+String.valueOf(rn.RoomNodeId);
			this.db.execSQL(query);

			for(NodeSwitch ns:rn.NodeSwitches) { // deleting these node switches from sceneswitches
				query="DELETE FROM SceneSwitches WHERE NodeSwitchId="+String.valueOf(ns.NodeSwitchId);
				this.db.execSQL(query);
				query="DELETE FROM IRIndices WHERE IRDeviceId="+String.valueOf(ns.NodeSwitchId);
				this.db.execSQL(query);
			}
			query="DELETE FROM RoomNodes WHERE RoomNodeId="+String.valueOf(rn.RoomNodeId);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteIRDevice(IRDevice ird) throws Exception {
		try {
			query="DELETE FROM IRDevices WHERE IRDeviceId="+String.valueOf(ird.IRDeviceId);
			this.db.execSQL(query);

			query="DELETE FROM IRIndices WHERE IRDeviceId="+String.valueOf(ird.IRDeviceId);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean deleteIRBlaster(IRBlaster irb) throws Exception {
		try {
			query="DELETE FROM IRIndices WHERE IRBlasterId=="+String.valueOf(irb.IRBlasterId);
			this.db.execSQL(query);

			query="DELETE FROM IRBlasters WHERE IRBlasterId="+String.valueOf(irb.IRBlasterId);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void deleteKeypadMapping(int mid) throws Exception {
		try {
			query="DELETE FROM KeypadMappings WHERE MappingId="+String.valueOf(mid);
			this.db.execSQL(query);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean resetIRBlaster(IRBlaster irb) throws Exception {
		try {
			query="DELETE FROM IRIndices WHERE IRBlasterId=="+String.valueOf(irb.IRBlasterId);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean updateUser(User u) throws Exception {
		try {
			query="UPDATE Users SET Firstname='"+u.Firstname+"',Lastname='"+u.Lastname+"' WHERE UserId="+String.valueOf(u.UserId);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean changePassword(int uid, String pwd) throws Exception {
		try {
			query="UPDATE Users SET Password='"+pwd+"' WHERE UserId="+String.valueOf(uid);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean intializeGateway(int gid,String sn,long dt,int br) throws Exception {
		try {
			query="UPDATE Globals SET GatewayId="+String.valueOf(gid)+",SerialNumber='"+sn+"',CustomerId=0,Firstname='Demo',"
					+ "Lastname='Customer',Username='demo',Email='',Mobile='',DateTested="+String.valueOf(dt)
					+",DateAllocated=0,Baudrate="+String.valueOf(br);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean registerGateway(int cid,String fn,String ln,String un,String em,String mob,long dateAllocated) throws Exception {
		try {
			query="UPDATE Globals SET CustomerId="+String.valueOf(cid)+",Firstname='"+fn+"',Lastname='"+ln
					+"',Username='"+un+"',Email='"+em+"',Mobile='"+mob+"',DateAllocated="+String.valueOf(dateAllocated);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	public int addNode(Node n) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("OnOffCount",n.OnOffCount);
			cv.put("DimmerCount",n.DimmerCount);
			cv.put("NodeName",n.NodeName);
			cv.put("NodeType",n.NodeType);
			cv.put("HasMaster",n.HasMaster?1:0);
			cv.put("DeviceId",n.CDeviceId);
			int id = (int) this.db.insert("Nodes", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}
	
	public int addUser(User u) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("Username", u.Username);
			cv.put("Password", u.Password);
			cv.put("Firstname", u.Firstname);
			cv.put("Lastname", u.Lastname);
			cv.put("Email", u.Email);
			cv.put("Role",u.Role);
			int id = (int) this.db.insert("Users", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}
	
	public int addCamera(Camera c) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("RoomId", c.roomId);
			cv.put("Name", c.cameraName);
			cv.put("Model", c.cameraModel);
			cv.put("IsActive", 1);
			if(isColumnExists("Cameras","IsVisitorCamera")) {
				cv.put("IsVisitorCamera",c.isVisitorCamera?1:0);
			}
			if(isColumnExists("Cameras","SerialNumber")) {
				cv.put("SerialNumber",c.serialNumber);
			}
			if(isColumnExists("Cameras","CustomValue")) {
				cv.put("CustomValue",c.customValue);
			}
			if(isColumnExists("Cameras","Username")) {
				cv.put("Username",c.username);
			}
			if(isColumnExists("Cameras","Password")) {
				cv.put("Password",c.password);
			}
			int id = (int) this.db.insert("Cameras", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();			
			throw ex;
		}
	}

	public int addRoom(Room r) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("RoomName", r.RoomName);
			cv.put("IsActive", 1);
			int id = (int) this.db.insert("Rooms", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();			
			throw ex;
			//return 0;
		}
	}

	public int addOneTouchController(OneTouchController sm) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("MacIdHigher", Integer.valueOf(sm.MacIdHigher));
			cv.put("MacIdLower", Integer.valueOf(sm.MacIdLower));
			cv.put("NodeIdHigher", Integer.valueOf(sm.NodeIdHigher));
			cv.put("NodeIdLower", Integer.valueOf(sm.NodeIdLower));

			int id = (int) this.db.insert("OneTouchControllers", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public boolean addCustomButtons(int devid) throws Exception {
		try {
			cv = new ContentValues();
			String sql = "SELECT CustomButtonId FROM CustomButtons WHERE IRDeviceId="+devid;
			Cursor cr = this.db.rawQuery(sql, null);
			int sceneid=0;
			if (cr.getCount() > 0) { // custom buttons already created for this device
			} else { // buttons need to be created
				for(int i=1;i<=6;i++) {
					cv = new ContentValues();
					cv.put("RemoteButton","Custom"+String.valueOf(i));
					cv.put("ButtonName","Custom");
					cv.put("IRDeviceId", devid);
					sceneid = (int) this.db.insert("CustomButtons", null, cv);
				}
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return false;
		}
	}

	public int addIRDevice(IRDevice ird) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("DeviceName", ird.DeviceName);
			cv.put("Category", ird.Category);
			cv.put("RoomId", ird.RoomId);
			cv.put("CustomValue", ird.CustomValue);

			int id = (int) this.db.insert("IRDevices", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public int addOneTouchControllerMapping(OneTouchControllerMapping km,int smid) throws Exception {
		try {
			String sql = "DELETE FROM OneTouchControllerMappings WHERE SwitchNumber="+String.valueOf(km.SwitchNumber)+" AND "
					+ "SwitchState="+String.valueOf(km.SwitchState)+" AND OneTouchControllerId="+String.valueOf(smid);
			db.execSQL(sql);

			cv = new ContentValues();
			cv.put("OneTouchControllerId", smid);
			cv.put("SwitchNumber", km.SwitchNumber);
			cv.put("Key", km.Key);
			cv.put("Value", km.Value);
			cv.put("SwitchState", km.SwitchState);
			int id = (int) db.insert("OneTouchControllerMappings", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public int addRoomNode(RoomNode n,int roomId) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("RoomId", roomId);
			cv.put("NodeId", n.NodeId);
			cv.put("NodeName", n.NodeName);
			cv.put("MacIdHigher", Integer.valueOf(n.MacIdHigher));
			cv.put("MacIdLower", Integer.valueOf(n.MacIdLower));
			cv.put("NodeIdHigher", Integer.valueOf(n.NodeIdHigher));
			cv.put("NodeIdLower", Integer.valueOf(n.NodeIdLower));
			if(isColumnExists("RoomNodes","Version")) {
				cv.put("Version",n.Version);
			} else {
				cv.put("Address",n.Version);
			}
			int id = (int) this.db.insert("RoomNodes", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public boolean replaceRoomNode(RoomNode oldrn,RoomNode newrn) throws Exception {
		try {
			query="UPDATE RoomNodes SET MacIdHigher='"+newrn.MacIdHigher+"',MacIdLower='"+newrn.MacIdLower+"',NodeIdHigher='"+newrn.NodeIdHigher+"',NodeIdLower='"+newrn.NodeIdLower+"',NodeName='"+newrn.NodeName+"',Version='"+String.valueOf(newrn.Version)+"' WHERE RoomNodeId="+oldrn.RoomNodeId;
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int addNodeSwitch(NodeSwitch ns,int roomNodeId) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("RoomNodeId", roomNodeId);
			cv.put("Category", ns.Category);
			cv.put("Type", ns.Type);
			cv.put("SwitchNumber",ns.SwitchNumber);
			cv.put("SwitchName",ns.SwitchName);
			if(isColumnExists("NodeSwitches","IRDeviceId"))
				cv.put("IRDeviceId",ns.IRDeviceId);
			if(isColumnExists("NodeSwitches","CustomValue"))
				cv.put("CustomValue",ns.CustomValue);

			int id = (int) this.db.insert("NodeSwitches", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public int addSensor(Sensor s) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("Type", s.Type);
			cv.put("SensorName", s.SensorName);
			cv.put("RoomId", Integer.valueOf(s.RoomId));
			cv.put("DeviceId", Integer.valueOf(s.CDeviceId));
			cv.put("MacIdHigher", Integer.valueOf(s.MacIdHigher));
			cv.put("MacIdLower", Integer.valueOf(s.MacIdLower));
			cv.put("NodeIdHigher", Integer.valueOf(s.NodeIdHigher));
			cv.put("NodeIdLower", Integer.valueOf(s.NodeIdLower));
			if(isColumnExists("Sensors","Version")) {
				cv.put("Version",s.Version);
			} else {
				cv.put("Address",s.Version);
			}
			int id = (int) this.db.insert("Sensors", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}
	
	public int addSetTopBox(SetTopBox stb) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("Name", stb.Name);
			cv.put("Make", stb.Make);
			cv.put("Model",stb.Model);

			int id = (int) this.db.insert("SetTopBoxes", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}
	
	public int addAlarm(Alarm a) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("Type", a.Type);
			cv.put("AlarmName", a.AlarmName);
			cv.put("RoomId", Integer.valueOf(a.RoomId));
			cv.put("DeviceId", Integer.valueOf(a.CDeviceId));
			cv.put("MacIdHigher", Integer.valueOf(a.MacIdHigher));
			cv.put("MacIdLower", Integer.valueOf(a.MacIdLower));
			cv.put("NodeIdHigher", Integer.valueOf(a.NodeIdHigher));
			cv.put("NodeIdLower", Integer.valueOf(a.NodeIdLower));
			if(isColumnExists("Alarms","Version")) {
				cv.put("Version",a.Version);
			} else {
				cv.put("Address",a.Version);
			}
			int id = (int) this.db.insert("Alarms", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public int addIRBlaster(IRBlaster irb) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("RoomId", irb.RoomId);
			cv.put("DeviceId", Integer.valueOf(irb.CDeviceId));
			cv.put("MacIdHigher", Integer.valueOf(irb.MacIdHigher));
			cv.put("MacIdLower", Integer.valueOf(irb.MacIdLower));
			cv.put("NodeIdHigher", Integer.valueOf(irb.NodeIdHigher));
			cv.put("NodeIdLower", Integer.valueOf(irb.NodeIdLower));
			if(isColumnExists("IRBlasters","Version")) {
				cv.put("Version",irb.Version);
			} else {
				cv.put("Address",irb.Version);
			}
			int id = (int) this.db.insert("IRBlasters", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public int addIRIndex(IRIndex iri,int irbid) throws Exception {
		try {
			if(isColumnExists("IRIndices","IRDeviceId")) {
				String sql = "DELETE FROM IRIndices WHERE RemoteButton='"+iri.RemoteButton+"' AND IRDeviceId="+String.valueOf(iri.IRDeviceId)
				+" AND IRBlasterId="+String.valueOf(irbid)+" AND Channel="+String.valueOf(iri.IRChannel)
				+" AND Value="+String.valueOf(iri.Value);
				this.db.execSQL(sql);

				cv = new ContentValues();
				cv.put("RemoteButton", iri.RemoteButton);
				cv.put("IRDeviceId", iri.IRDeviceId);
				cv.put("IRBlasterId", irbid);
				cv.put("IRIndex", Integer.valueOf(iri.IRIndex));
				cv.put("Channel", iri.IRChannel);
				cv.put("Value", iri.Value);
				int id = (int) this.db.insert("IRIndices", null, cv);
				return id;	
			} else {
				String sql = "DELETE FROM IRIndices WHERE RemoteButton='"+iri.RemoteButton+"' AND DeviceId="+String.valueOf(iri.IRDeviceId)
				+" AND IRBlasterId="+String.valueOf(irbid)+" AND Channel="+String.valueOf(iri.IRChannel)
				+" AND Value="+String.valueOf(iri.Value);
				this.db.execSQL(sql);

				cv = new ContentValues();
				cv.put("RemoteButton", iri.RemoteButton);
				cv.put("DeviceId", iri.IRDeviceId);
				cv.put("IRBlasterId", irbid);
				cv.put("IRIndex", Integer.valueOf(iri.IRIndex));
				cv.put("Channel", iri.IRChannel);
				cv.put("Value", iri.Value);
				int id = (int) this.db.insert("IRIndices", null, cv);
				return id;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}

	public int addSTBChannel(STBChannel stbc,int stbid) throws Exception {
		try {
			cv = new ContentValues();
			cv.put("SetTopBoxId", stbid);
			cv.put("Number", Integer.valueOf(stbc.Number));
			cv.put("Name", stbc.Name);
			cv.put("Filename", stbc.Filename);
			cv.put("Language", stbc.Language);
			cv.put("Category",stbc.Category);

			int id = (int) this.db.insert("STBChannels", null, cv);
			return id;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			//return 0;
		}
	}
	
	public ArrayList<Node> getAllNodes() throws Exception {
		
		Node n;
		ArrayList<Node> nodes = new ArrayList<Node>();
		try {
			String sql = "SELECT * FROM Nodes ORDER BY NodeName ASC";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					n = new Node();
					n.NodeId = cr.getInt(cr.getColumnIndex("NodeId"));
					n.CDeviceId = cr.getInt(cr.getColumnIndex("DeviceId"));
					n.NodeName = cr.getString(cr.getColumnIndex("NodeName"));
					n.NodeType = cr.getString(cr.getColumnIndex("NodeType"));
					n.OnOffCount = cr.getInt(cr.getColumnIndex("OnOffCount"));
					n.DimmerCount = cr.getInt(cr.getColumnIndex("DimmerCount"));
					if(isColumnExists("Nodes","HasMaster")) {
						n.HasMaster=(cr.getInt(cr.getColumnIndex("HasMaster"))==1);
					}
					nodes.add(n);
				} while (cr.moveToNext());
			}
			cr.close();
			return nodes;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public ArrayList<Room> getAllRooms() throws Exception {
		Room r;
		ArrayList<Room> rooms = new ArrayList<Room>();
		try {
			String sql = "SELECT * FROM Rooms";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					r = new Room();
					r.RoomId = cr.getInt(cr.getColumnIndex("RoomId"));
					r.RoomName = cr.getString(cr.getColumnIndex("RoomName"));
					r.RoomNodes=getRoomNodes(r.RoomId);
					rooms.add(r);
				} while (cr.moveToNext());
			}
			cr.close();
			return rooms;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<User> getAllUsers() throws Exception {
		User u;
		ArrayList<User> users = new ArrayList<User>();

		try {
			String sql = "SELECT * FROM Users";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					u = new User();
					u.UserId = cr.getInt(cr.getColumnIndex("UserId"));
					u.Username = cr.getString(cr.getColumnIndex("Username"));
					u.Password = cr.getString(cr.getColumnIndex("Password"));
					u.Firstname = cr.getString(cr.getColumnIndex("Firstname"));
					u.Lastname = cr.getString(cr.getColumnIndex("Lastname"));
					if(isColumnExists("Users","Email")) {
						u.Email = cr.getString(cr.getColumnIndex("Email"));	
					} else {
						u.Email="customerEmail";
					}
					u.Role=2; // normal user
					if(isColumnExists("Users","Role")) {
						u.Role=cr.getInt(cr.getColumnIndex("Role"));
					} else {
						if(isColumnExists("Users","IsMaster")) {
							u.Role=cr.getInt(cr.getColumnIndex("IsMaster"));
							if(u.Role!=1) u.Role=2; // isMaster=0 cases
						} else {
							u.Role=2;
						}
					}
					users.add(u);
				} while (cr.moveToNext());
			}
			cr.close();
			return users;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<Camera> getAllCameras() throws Exception {
		Camera c;
		ArrayList<Camera> cameras= new ArrayList<Camera>();
		if(!isTableExists("Cameras")) return cameras;
		try {
			String sql = "SELECT * FROM Cameras";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					c = new Camera();
					c.cameraId = cr.getInt(cr.getColumnIndex("CameraId"));
					c.roomId = cr.getInt(cr.getColumnIndex("RoomId"));
					c.cameraName = cr.getString(cr.getColumnIndex("Name"));
					c.cameraModel = cr.getString(cr.getColumnIndex("Model"));
					if(isColumnExists("Cameras","SerialNumber")) {
						c.serialNumber=cr.getString(cr.getColumnIndex("SerialNumber"));
					} else {
						c.serialNumber="";
					}
					if(isColumnExists("Cameras","CustomValue")) {
						c.customValue=cr.getString(cr.getColumnIndex("CustomValue"));
					} else {
						c.customValue="";
					}
					if(isColumnExists("Cameras","Username")) {
						c.username=cr.getString(cr.getColumnIndex("Username"));
					} else {
						c.username="";
					}
					if(isColumnExists("Cameras","Password")) {
						c.password=cr.getString(cr.getColumnIndex("Password"));
					} else {
						c.password="";
					}
					if(isColumnExists("Cameras","IsVisitorCamera")) {
						c.isVisitorCamera=(cr.getInt(cr.getColumnIndex("IsVisitorCamera"))==1);
					} else {
						c.isVisitorCamera=false;
					}
					cameras.add(c);
				} while (cr.moveToNext());
			}
			cr.close();
			return cameras;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<CameraModel> getAllCameraModels() throws Exception {
		CameraModel cm;
		ArrayList<CameraModel> cmodels= new ArrayList<CameraModel>();
		if(!isTableExists("CameraModels")) return cmodels;
		try {
			String sql = "SELECT * FROM CameraModels";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					cm = new CameraModel();
					cm.ModelId = cr.getInt(cr.getColumnIndex("CameraModelId"));
					cm.Model = cr.getString(cr.getColumnIndex("Model"));
					cm.PlayStorePackage = cr.getString(cr.getColumnIndex("Package"));			
					cmodels.add(cm);
				} while (cr.moveToNext());
			}
			cr.close();
			return cmodels;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<RoomNode> getRoomNodes(int roomId) throws Exception {
		RoomNode rn;
		ArrayList<RoomNode> rnodes= new ArrayList<RoomNode>();

		try {
			String sql = "SELECT * FROM RoomNodes WHERE RoomId="+roomId;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					rn = new RoomNode();
					rn.RoomNodeId = cr.getInt(cr.getColumnIndex("RoomNodeId"));
					rn.NodeId=cr.getInt(cr.getColumnIndex("NodeId"));
					rn.NodeType=getNodeType(rn.NodeId);
					if(isColumnExists("RoomNodes","NodeName")) {
						rn.NodeName=cr.getString(cr.getColumnIndex("NodeName"));
					} else {
						rn.NodeName=getNodeName(rn.NodeId); //cr.getString(cr.getColumnIndex("NodeName"));
						if(isColumnExists("RoomNodes","RoomNodeName")) {
							rn.NodeName=cr.getString(cr.getColumnIndex("RoomNodeName"));
						}
					}
					if(isColumnExists("RoomNodes","Version")) {
						rn.Version = cr.getInt(cr.getColumnIndex("Version"));
					} else {
						rn.Version = cr.getInt(cr.getColumnIndex("Address"));
					}
					rn.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
					rn.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
					rn.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
					rn.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
					rn.NodeSwitches=getNodeSwitches(rn.RoomNodeId);
					rnodes.add(rn);
				} while (cr.moveToNext());
			}
			cr.close();
			return rnodes;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<IRDevice> getAllIRDevices() throws Exception {
		IRDevice ird;
		ArrayList<IRDevice> irds = new ArrayList<IRDevice>();
		try {
			String sql = "SELECT * FROM IRDevices";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					ird = new IRDevice();
					ird.IRDeviceId = cr.getInt(cr.getColumnIndex("IRDeviceId"));
					ird.RoomId = cr.getInt(cr.getColumnIndex("RoomId"));
					ird.DeviceName = cr.getString(cr.getColumnIndex("DeviceName"));
					ird.Category = cr.getString(cr.getColumnIndex("Category"));
					ird.CustomValue=cr.getString(cr.getColumnIndex("CustomValue"));
					ird.Location=getRoomName(ird.RoomId);
					irds.add(ird);
				} while (cr.moveToNext());
			}
			cr.close();
			return irds;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int getRoomNodeId(int nh,int nl) throws Exception {
		int rnid = 0;
		try {
			String sql = "SELECT RoomNodeId FROM RoomNodes WHERE NodeIdHigher="+nh+" AND NodeIdLower="+nl;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					rnid = cr.getInt(cr.getColumnIndex("RoomNodeId"));;
				} while (cr.moveToNext());
			}
			cr.close();
			return rnid;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public String getRoomName(int rid) throws Exception {
		String rn="";
		try {
			String sql = "SELECT RoomName FROM Rooms WHERE RoomId="+rid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				rn = cr.getString(cr.getColumnIndex("RoomName"));
			}
			cr.close();
			return rn;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public String getNodeName(int nid) throws Exception {
		String nn="";
		try {
			String sql = "SELECT NodeName FROM Nodes WHERE NodeId="+nid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				nn = cr.getString(cr.getColumnIndex("NodeName"));
			}
			cr.close();
			return nn;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public String getNodeType(int nid) throws Exception {
		String nt="";
		try {
			String sql = "SELECT NodeType FROM Nodes WHERE NodeId="+nid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				nt = cr.getString(cr.getColumnIndex("NodeType"));
			}
			cr.close();
			return nt;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public String getRoomNameForRoomNode(int rnid) throws Exception {
		String rn="No Room Found";
		try {
			String sql = "SELECT RoomName FROM Rooms WHERE RoomId=(SELECT RoomId FROM RoomNodes WHERE RoomNodeId="+rnid+")";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				rn = cr.getString(cr.getColumnIndex("RoomName"));
			}
			cr.close();	

			return rn;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int getRoomIdForRoomNode(int rnid) throws Exception {
		int rid=0;
		try {
			String sql = "SELECT RoomId FROM RoomNodes WHERE RoomNodeId="+String.valueOf(rnid);
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				rid = cr.getInt(cr.getColumnIndex("RoomId"));
			}
			cr.close();
			return rid;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public Sensor getSensor(int nh,int nl) throws Exception {
		Sensor s=null;
		try {
			String sql = "SELECT * FROM Sensors WHERE NodeIdHigher="+nh+" AND NodeIdLower="+nl;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				s = new Sensor();
				s.SensorId= cr.getInt(cr.getColumnIndex("SensorId"));
				if(isColumnExists("Sensors","RoomId")) {
					s.RoomId= cr.getInt(cr.getColumnIndex("RoomId"));
					s.Location = getRoomName(s.RoomId);
				}
				if(isColumnExists("Sensors","Location")) {
					s.Location = cr.getString(cr.getColumnIndex("Location"));
				}
				s.SensorName = cr.getString(cr.getColumnIndex("SensorName"));
				if(isColumnExists("Sensors","Version")) {
					s.Version = cr.getInt(cr.getColumnIndex("Version"));
				} else {
					s.Version = cr.getInt(cr.getColumnIndex("Address"));
				}
				s.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
				s.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
				s.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
				s.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
				s.Type = cr.getString(cr.getColumnIndex("Type"));
			}
			cr.close();
			return s;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<NodeSwitch> getNodeSwitches(int roomNodeId) throws Exception {
		NodeSwitch ns;
		ArrayList<NodeSwitch> nws= new ArrayList<NodeSwitch>();

		try {
			String sql = "SELECT * FROM NodeSwitches WHERE RoomNodeId="+roomNodeId+" ORDER BY SwitchNumber";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					ns = new NodeSwitch();
					ns.NodeSwitchId=cr.getInt(cr.getColumnIndex("NodeSwitchId"));
					if(isColumnExists("NodeSwitches","IRDeviceId")) {
						ns.IRDeviceId=cr.getInt(cr.getColumnIndex("IRDeviceId"));
					}
					ns.Category=cr.getString(cr.getColumnIndex("Category"));
					ns.SwitchName=cr.getString(cr.getColumnIndex("SwitchName"));
					ns.Type=cr.getString(cr.getColumnIndex("Type"));
					ns.SwitchNumber=cr.getInt(cr.getColumnIndex("SwitchNumber"));
					if(isColumnExists("NodeSwitches","CustomValue")) {
						ns.CustomValue=cr.getString(cr.getColumnIndex("CustomValue"));
					}
					nws.add(ns);
				} while (cr.moveToNext());
			}
			cr.close();
			return nws;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<CustomButton> getAllCustomButtons() throws Exception {
		CustomButton cb;
		ArrayList<CustomButton> cbs= new ArrayList<CustomButton>();

		try {
			String sql = "SELECT * FROM CustomButtons";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					cb=new CustomButton();
					cb.CustomButtonId = cr.getInt(cr.getColumnIndex("CustomButtonId"));
					if(isColumnExists("CustomButtons","IRDeviceId")) {
						cb.IRDeviceId = cr.getInt(cr.getColumnIndex("IRDeviceId"));
					} else {
						cb.IRDeviceId = cr.getInt(cr.getColumnIndex("DeviceId"));
					}
					cb.ButtonName = cr.getString(cr.getColumnIndex("ButtonName"));
					cb.RemoteButton = cr.getString(cr.getColumnIndex("RemoteButton"));
					cbs.add(cb);
				} while (cr.moveToNext());
			}
			cr.close();
			return cbs;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<OneTouchController> getAllOneTouchControllers() throws Exception {
		OneTouchController rn;
		ArrayList<OneTouchController> rnodes= new ArrayList<OneTouchController>();

		try {
			String sql = "";
			Cursor cr=null;
			if(isTableExists("OneTouchControllers")) {
				sql = "SELECT * FROM OneTouchControllers";
				cr = this.db.rawQuery(sql, null);

				if (cr.getCount() > 0) {
					cr.moveToFirst();
					do {
						rn = new OneTouchController();
						rn.OneTouchControllerId = cr.getInt(cr.getColumnIndex("OneTouchControllerId"));
						rn.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
						rn.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
						rn.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
						rn.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
						rn.Mappings=getOneTouchControllerMappings(rn.OneTouchControllerId);
						rnodes.add(rn);
					} while (cr.moveToNext());
				}
				cr.close();
				return rnodes;
			} 
			if(isTableExists("SceneMakers")) {
				sql = "SELECT * FROM SceneMakers";
				cr = this.db.rawQuery(sql, null);

				if (cr.getCount() > 0) {
					cr.moveToFirst();
					do {
						rn = new OneTouchController();
						rn.OneTouchControllerId = cr.getInt(cr.getColumnIndex("SceneMakerId"));
						rn.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
						rn.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
						rn.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
						rn.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
						rn.Mappings=getOneTouchControllerMappings(rn.OneTouchControllerId);
						rnodes.add(rn);
					} while (cr.moveToNext());
				}
				cr.close();
				return rnodes;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return rnodes;
	}

	public ArrayList<CustomButton> getCustomButtons(int devid) throws Exception {
		CustomButton cb;
		ArrayList<CustomButton> cbs= new ArrayList<CustomButton>();

		try {
			String sql = "SELECT * FROM CustomButtons WHERE IRDeviceId="+String.valueOf(devid);
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					cb=new CustomButton();
					cb.CustomButtonId = cr.getInt(cr.getColumnIndex("CustomButtonId"));
					cb.IRDeviceId = cr.getInt(cr.getColumnIndex("IRDeviceId"));
					cb.ButtonName = cr.getString(cr.getColumnIndex("ButtonName"));
					cb.RemoteButton = cr.getString(cr.getColumnIndex("RemoteButton"));
					cbs.add(cb);
				} while (cr.moveToNext());
			}
			cr.close();
			return cbs;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<OneTouchControllerMapping> getOneTouchControllerMappings(int smid) throws Exception {
		OneTouchControllerMapping km;
		ArrayList<OneTouchControllerMapping> kms= new ArrayList<OneTouchControllerMapping>();

		try {
			String sql = "";
			Cursor cr=null;
			if(isTableExists("OneTouchControllerMappings")) {
				sql = "SELECT * FROM OneTouchControllerMappings WHERE OneTouchControllerId="+String.valueOf(smid);
			}
			if(isTableExists("SceneMakerMappings")) {
				sql = "SELECT * FROM SceneMakerMappings WHERE SceneMakerId="+String.valueOf(smid);
			}
			if(!sql.isEmpty()) {
				cr = this.db.rawQuery(sql, null);
				if (cr.getCount() > 0) {
					cr.moveToFirst();
					do {
						km=new OneTouchControllerMapping();
						km.MappingId = cr.getInt(cr.getColumnIndex("MappingId"));
						km.SwitchNumber = cr.getInt(cr.getColumnIndex("SwitchNumber"));
						km.Key = cr.getString(cr.getColumnIndex("Key"));
						km.Value = cr.getInt(cr.getColumnIndex("Value"));
						km.SwitchState = cr.getInt(cr.getColumnIndex("SwitchState"));
						kms.add(km);
					} while (cr.moveToNext());
				}
				cr.close();
			}
			return kms;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public NodeSwitch getNodeSwitch(int nsid) throws Exception {
		NodeSwitch ns = null;
		try {
			String sql = "SELECT * FROM NodeSwitches WHERE NodeSwitchId="+nsid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				ns = new NodeSwitch();
				ns.NodeSwitchId=cr.getInt(cr.getColumnIndex("NodeSwitchId"));
				if(isColumnExists("NodeSwitches","IRDeviceId")) {
					ns.IRDeviceId=cr.getInt(cr.getColumnIndex("IRDeviceId"));	
				}
				ns.Category=cr.getString(cr.getColumnIndex("Category"));
				ns.SwitchName=cr.getString(cr.getColumnIndex("SwitchName"));
				ns.Type=cr.getString(cr.getColumnIndex("Type"));
				ns.SwitchNumber=cr.getInt(cr.getColumnIndex("SwitchNumber"));
				if(isColumnExists("NodeSwitches","CustomValue")) {
					ns.IRDeviceId=cr.getInt(cr.getColumnIndex("CustomValue"));	
				}
			}
			cr.close();
			return ns;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public RoomNode getRoomNode(int rnid) throws Exception {
		RoomNode rn = null;
		try {
			String sql = "SELECT * FROM RoomNodes WHERE RoomNodeId="+rnid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				rn = new RoomNode();
				rn.RoomNodeId = cr.getInt(cr.getColumnIndex("RoomNodeId"));
				rn.NodeId=cr.getInt(cr.getColumnIndex("NodeId"));
				rn.NodeName=cr.getString(cr.getColumnIndex("NodeName"));
				if(isColumnExists("RoomNodes","RoomNodeName")) {
					rn.NodeName=cr.getString(cr.getColumnIndex("RoomNodeName"));
				}
				if(isColumnExists("RoomNodes","Version")) {
					rn.Version = cr.getInt(cr.getColumnIndex("Version"));
				} else {
					rn.Version = cr.getInt(cr.getColumnIndex("Address"));
				}
				rn.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
				rn.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
				rn.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
				rn.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
			}
			cr.close();
			return rn;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public RoomNode getRoomNodeForNodeSwitch(int nsid) throws Exception {
		RoomNode rn = null;
		try {
			String sql = "SELECT * FROM RoomNodes WHERE RoomNodeId=(SELECT RoomNodeId FROM NodeSwitches WHERE NodeSwitchId="+nsid+")";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				rn = new RoomNode();
				rn.RoomNodeId = cr.getInt(cr.getColumnIndex("RoomNodeId"));
				rn.NodeId=cr.getInt(cr.getColumnIndex("NodeId"));
				rn.NodeType=getNodeType(rn.NodeId);
				rn.NodeName=getNodeName(rn.NodeId); //cr.getString(cr.getColumnIndex("NodeName"));
				if(isColumnExists("RoomNodes","RoomNodeName")) {
					rn.NodeName=cr.getString(cr.getColumnIndex("RoomNodeName"));
				}
				if(isColumnExists("RoomNodes","Version")) {
					rn.Version = cr.getInt(cr.getColumnIndex("Version"));
				} else {
					rn.Version = cr.getInt(cr.getColumnIndex("Address"));
				}
				rn.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
				rn.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
				rn.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
				rn.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
				rn.NodeSwitches=getNodeSwitches(rn.RoomNodeId);
			}
			cr.close();
			return rn;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<Scene> getAllScenes() throws Exception {
		Scene s;
		ArrayList<Scene> scenes = new ArrayList<Scene>();
		try {
			String sql = "SELECT * FROM Scenes";
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					s = new Scene();
					s.SceneId = cr.getInt(cr.getColumnIndex("SceneId"));
					s.SceneName = cr.getString(cr.getColumnIndex("SceneName"));
					s.SceneSwitches=getSceneSwitches(s.SceneId);
					s.SceneSensors=getSceneSensors(s.SceneId);
					if(isTableExists("SceneIRIndices")) {
						s.SceneIRIndices=getSceneIRIndices(s.SceneId);	
					}
					scenes.add(s);
				} while (cr.moveToNext());
			}
			cr.close();
			return scenes;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public Scene getScene(int sid) throws Exception {
		Scene s=null;
		try {
			String sql = "SELECT * FROM Scenes WHERE SceneId="+sid;
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					s = new Scene();
					s.SceneId = cr.getInt(cr.getColumnIndex("SceneId"));
					s.SceneName = cr.getString(cr.getColumnIndex("SceneName"));
					s.SceneSwitches=getSceneSwitches(s.SceneId);
					s.SceneSensors=getSceneSensors(s.SceneId);
					if(isTableExists("SceneIRIndices")) {
						s.SceneIRIndices=getSceneIRIndices(s.SceneId);	
					}
				} while (cr.moveToNext());
			}
			cr.close();
			return s;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean getGlobals() throws Exception {
		try {
			String sql="";
			Cursor cr = null;
			if(isTableExists("Customers")) {
				Globals.dbVersion=3.6;
				sql = "SELECT * FROM Customers";
				cr = this.db.rawQuery(sql, null);
				if (cr.getCount() > 0) {
					cr.moveToFirst();
					Globals.customerId = cr.getInt(cr.getColumnIndex("CustomerId"));
					Globals.customerUsername = cr.getString(cr.getColumnIndex("Username"));
					Globals.customerFirstname = cr.getString(cr.getColumnIndex("Firstname"));
					Globals.customerLastname = cr.getString(cr.getColumnIndex("Lastname"));
					Globals.baudRate = cr.getInt(cr.getColumnIndex("BaudRate"));
				}
			}
			if(isTableExists("Globals")) {
				sql = "SELECT * FROM Globals";
				cr = this.db.rawQuery(sql, null);
				if (cr.getCount() > 0) {
					cr.moveToFirst();
					Globals.customerId = cr.getInt(cr.getColumnIndex("CustomerId"));
					Globals.customerUsername = cr.getString(cr.getColumnIndex("Username"));
					Globals.customerFirstname = cr.getString(cr.getColumnIndex("Firstname"));
					Globals.customerLastname = cr.getString(cr.getColumnIndex("Lastname"));
					Globals.baudRate = cr.getInt(cr.getColumnIndex("BaudRate"));
					Globals.dbVersion = cr.getDouble(cr.getColumnIndex("Version"));
					if(Globals.dbVersion>=3.95) {
						Globals.gatewayId = cr.getInt(cr.getColumnIndex("GatewayId"));
						Globals.serialNumber = cr.getString(cr.getColumnIndex("SerialNumber"));
						Globals.customerEmail = cr.getString(cr.getColumnIndex("Email"));
						Globals.customerMobile = cr.getString(cr.getColumnIndex("Mobile"));
						Globals.dateTested = cr.getInt(cr.getColumnIndex("DateTested"));
						Globals.dateAllocated = cr.getInt(cr.getColumnIndex("DateAllocated"));
					}
				}
			}
			if(cr==null) {
				return false;
			} else {
				cr.close();
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<Schedule> getAllSchedules() throws Exception {
		Schedule s;
		ArrayList<Schedule> schs = new ArrayList<Schedule>();
		try {
			String sql = "SELECT * FROM Schedules";
			Cursor cr = this.db.rawQuery(sql, null);
			String da="";
			String[] das;
			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					s = new Schedule();
					s.ScheduleId = cr.getInt(cr.getColumnIndex("ScheduleId"));
					s.SceneId = cr.getInt(cr.getColumnIndex("SceneId"));
					s.StartTime=cr.getInt(cr.getColumnIndex("StartTime"));
					da=cr.getString(cr.getColumnIndex("DaysApplicable")).trim();
					if(!da.equals("")) {
						das=da.split("\\,");
						for(int i=0;i<das.length;i++) {
							s.DatesApplicable[i]=Integer.valueOf(das[i]);
						}
					}
					s.LastRunTime=getScheduleLastRunTime(s.ScheduleId);
					s.CanRunAfterElapsed=true;//(cr.getInt(cr.getColumnIndex("CanRunAfterElasped"))==1);
					schs.add(s);
				} while (cr.moveToNext());
			}
			cr.close();
			return schs;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<SceneSwitch> getSceneSwitches(int sceneId) throws Exception {
		SceneSwitch ss;
		ArrayList<SceneSwitch> sswitches= new ArrayList<SceneSwitch>();

		try {
			String sql = "SELECT * FROM SceneSwitches WHERE SceneId="+sceneId+" ORDER BY NodeSwitchId";
			Cursor cr = this.db.rawQuery(sql, null);


			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					ss = new SceneSwitch();
					ss.SceneSwitchId = cr.getInt(cr.getColumnIndex("SceneSwitchId"));
					ss.NodeSwitchId=cr.getInt(cr.getColumnIndex("NodeSwitchId"));
					ss.Level = cr.getInt(cr.getColumnIndex("Level"));
					Integer s=cr.getInt(cr.getColumnIndex("Status"));
					ss.IsOn=s.equals(1);
					sswitches.add(ss);
				} while (cr.moveToNext());
			}
			cr.close();
			return sswitches;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<SceneSensor> getSceneSensors(int sceneId) throws Exception {
		SceneSensor ss;
		ArrayList<SceneSensor> ssensors= new ArrayList<SceneSensor>();

		try {
			String sql = "SELECT * FROM SceneSensors WHERE SceneId="+sceneId;
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					ss = new SceneSensor();
					ss.SceneSensorId = cr.getInt(cr.getColumnIndex("SceneSensorId"));
					ss.SensorId=cr.getInt(cr.getColumnIndex("SensorId"));
					Integer s=cr.getInt(cr.getColumnIndex("IsArmed"));
					ss.IsArmed=s.equals(1);
					ssensors.add(ss);
				} while (cr.moveToNext());
			}
			cr.close();
			return ssensors;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public ArrayList<SceneIRIndex> getSceneIRIndices(int sceneId) throws Exception {
		SceneIRIndex ss;
		ArrayList<SceneIRIndex> sindices= new ArrayList<SceneIRIndex>();

		try {
			String sql = "SELECT * FROM SceneIRIndices WHERE SceneId="+sceneId;
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					ss = new SceneIRIndex();
					ss.SceneIRIndexId = cr.getInt(cr.getColumnIndex("SceneIRIndexId"));
					ss.IRIndexId=cr.getInt(cr.getColumnIndex("IRIndexId"));
					ss.Key=cr.getString(cr.getColumnIndex("Key"));
					ss.Value=cr.getString(cr.getColumnIndex("Value"));
					sindices.add(ss);
				} while (cr.moveToNext());
			}
			cr.close();
			return sindices;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public ArrayList<Sensor> getAllSensors() throws Exception {
		Sensor s;
		ArrayList<Sensor> ss= new ArrayList<Sensor>();

		try {
			String sql = "SELECT * FROM Sensors";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					s = new Sensor();
					s.SensorId= cr.getInt(cr.getColumnIndex("SensorId"));
					if(isColumnExists("Sensors","RoomId")) {
						s.RoomId= cr.getInt(cr.getColumnIndex("RoomId"));
						s.Location = getRoomName(s.RoomId);
					} else {
						if(isColumnExists("Sensors","Location")) {
							s.Location = cr.getString(cr.getColumnIndex("Location"));
						} else {
							s.Location = getRoomName(s.RoomId);
						}	
					}
					s.SensorName = cr.getString(cr.getColumnIndex("SensorName"));
					if(isColumnExists("Sensors","Version")) {
						s.Version = cr.getInt(cr.getColumnIndex("Version"));
					} else {
						s.Version = cr.getInt(cr.getColumnIndex("Address"));
					}
					s.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
					s.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
					s.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
					s.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
					s.Type = cr.getString(cr.getColumnIndex("Type"));
					if(s.Type.equals("Magentic"))
						s.Type="Magnetic";
					s.IsArmed=(cr.getInt(cr.getColumnIndex("IsArmed"))==1);
					ss.add(s);
				} while (cr.moveToNext());
			}
			cr.close();
			return ss;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public ArrayList<AppliedSchedule> getAllAppliedSchedules() throws Exception {
		AppliedSchedule s;
		ArrayList<AppliedSchedule> ss= new ArrayList<AppliedSchedule>();

		try {
			String sql = "SELECT * FROM AppliedSchedules";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					s = new AppliedSchedule();
					s.AppliedId= cr.getInt(cr.getColumnIndex("AppliedId"));
					s.ScheduleId= cr.getInt(cr.getColumnIndex("ScheduleId"));
					s.LastRunTime= cr.getLong(cr.getColumnIndex("LastRunTime"));
					ss.add(s);
				} while (cr.moveToNext());
			}
			cr.close();
			return ss;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<Alarm> getAllAlarms() throws Exception {
		Alarm a;
		ArrayList<Alarm> as= new ArrayList<Alarm>();

		try {
			String sql = "SELECT * FROM Alarms";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					a = new Alarm();
					a.AlarmId= cr.getInt(cr.getColumnIndex("AlarmId"));
					if(isColumnExists("Alarms","RoomId")) {
						a.RoomId= cr.getInt(cr.getColumnIndex("RoomId"));
						a.Location = getRoomName(a.RoomId); //cr.getString(cr.getColumnIndex("Location"));
					}
					if(isColumnExists("Alarms","Location")) {
						a.Location = cr.getString(cr.getColumnIndex("Location"));
					}
					a.AlarmName = cr.getString(cr.getColumnIndex("AlarmName"));
					if(isColumnExists("Alarms","Version")) {
						a.Version = cr.getInt(cr.getColumnIndex("Version"));
					} else {
						a.Version = cr.getInt(cr.getColumnIndex("Address"));
					}
					a.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
					a.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
					a.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
					a.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
					a.Type = cr.getString(cr.getColumnIndex("Type"));
					as.add(a);
				} while (cr.moveToNext());
			}
			cr.close();
			return as;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<IRBlaster> getAllIRBlasters() throws Exception {
		IRBlaster irb;
		ArrayList<IRBlaster> irbs= new ArrayList<IRBlaster>();

		try {
			String sql = "SELECT * FROM IRBlasters";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					irb = new IRBlaster();
					irb.IRBlasterId= cr.getInt(cr.getColumnIndex("IRBlasterId"));
					irb.RoomId= cr.getInt(cr.getColumnIndex("RoomId"));
					if(isColumnExists("IRBlasters","Version")) {
						irb.Version = cr.getInt(cr.getColumnIndex("Version"));
					} else {
						irb.Version = cr.getInt(cr.getColumnIndex("Address"));
					}
					irb.MacIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdHigher")));
					irb.MacIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("MacIdLower")));
					irb.NodeIdHigher = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdHigher")));
					irb.NodeIdLower = Byte.valueOf(cr.getString(cr.getColumnIndex("NodeIdLower")));
					irb.RoomName=getRoomName(irb.RoomId);
					irb.IRIndices=getIRIndices(irb.IRBlasterId);
					irbs.add(irb);
				} while (cr.moveToNext());
			}
			cr.close();
			return irbs;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public ArrayList<SetTopBox> getAllSetTopBoxes() throws Exception {
		SetTopBox stb;
		ArrayList<SetTopBox> stbs= new ArrayList<SetTopBox>();

		try {
			String sql = "SELECT * FROM SetTopBoxes";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					stb = new SetTopBox();
					if(isColumnExists("SetTopBoxes","SetTopBoxId")) {
						stb.SetTopBoxId= cr.getInt(cr.getColumnIndex("SetTopBoxId"));
					} else {
						stb.SetTopBoxId= cr.getInt(cr.getColumnIndex("STBoxId"));
					}
					stb.Name = cr.getString(cr.getColumnIndex("Name"));
					if(isColumnExists("SetTopBoxes","Make")) {
						stb.Make = cr.getString(cr.getColumnIndex("Make"));
					}
					if(isColumnExists("SetTopBoxes","Model")) {
						stb.Model = cr.getString(cr.getColumnIndex("Model"));
					}
					stb.Channels=getSTBChannels(stb.SetTopBoxId);
					stbs.add(stb);
				} while (cr.moveToNext());
			}
			cr.close();
			return stbs;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public ArrayList<IRIndex> getIRIndices(int irbid) throws Exception {
		IRIndex a;
		ArrayList<IRIndex> as= new ArrayList<IRIndex>();

		try {
			String sql = "SELECT * FROM IRIndices WHERE IRBlasterId="+String.valueOf(irbid);
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					a = new IRIndex();
					a.IRIndexId= cr.getInt(cr.getColumnIndex("IRIndexId"));
					if(isColumnExists("IRIndices","IRDeviceId")) {
						a.IRDeviceId= cr.getInt(cr.getColumnIndex("IRDeviceId"));	
					} else {
						a.IRDeviceId= cr.getInt(cr.getColumnIndex("DeviceId"));
					}
					a.RemoteButton = cr.getString(cr.getColumnIndex("RemoteButton"));
					a.IRChannel = cr.getInt(cr.getColumnIndex("Channel"));
					a.IRIndex = Byte.valueOf(cr.getString(cr.getColumnIndex("IRIndex")));
					a.Value = cr.getInt(cr.getColumnIndex("Value"));
					as.add(a);
				} while (cr.moveToNext());
			}
			cr.close();
			return as;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public ArrayList<STBChannel> getSTBChannels(int stbid) throws Exception {
		STBChannel stbc;
		ArrayList<STBChannel> channels= new ArrayList<STBChannel>();

		try {
			String sql="";
			if(isColumnExists("STBChannels","SetTopBoxId")) {
				sql = "SELECT * FROM STBChannels WHERE SetTopBoxId="+stbid;
			} else {
				sql = "SELECT * FROM STBChannels WHERE STBoxId="+stbid;
			}
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					stbc=new STBChannel();
					if(isColumnExists("STBChannels","STBChannelId")) {
						stbc.STBChannelId = cr.getInt(cr.getColumnIndex("STBChannelId"));	
					} else {
						stbc.STBChannelId = cr.getInt(cr.getColumnIndex("ChannelId"));
					}
					stbc.Number=cr.getInt(cr.getColumnIndex("Number"));
					stbc.Category = cr.getString(cr.getColumnIndex("Category"));
					stbc.Filename= cr.getString(cr.getColumnIndex("Filename"));
					stbc.Language= cr.getString(cr.getColumnIndex("Language"));
					stbc.Name= cr.getString(cr.getColumnIndex("Name"));
					channels.add(stbc);
				} while (cr.moveToNext());
			}
			cr.close();
			return channels;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public STBChannel getSTBChannel(int chid) throws Exception {
		STBChannel stbc=null;

		try {
			String sql = "SELECT * FROM STBChannels WHERE STBChannelId="+chid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();

				stbc=new STBChannel();
				stbc.STBChannelId = cr.getInt(cr.getColumnIndex("STBChannelId"));;
				stbc.Number=cr.getInt(cr.getColumnIndex("Number"));
				stbc.Category = cr.getString(cr.getColumnIndex("Category"));
				stbc.Filename= cr.getString(cr.getColumnIndex("Filename"));
				stbc.Language= cr.getString(cr.getColumnIndex("Language"));
				stbc.Name= cr.getString(cr.getColumnIndex("Name"));
			}
			cr.close();
			return stbc;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean isValidCredentials(String un,String pwd) throws Exception {
		try {
			String sql = "SELECT UserId FROM Users WHERE Username='"+un+"' AND Password='"+pwd+"'";
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.close();
				return true;
			} else {
				cr.close();
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean isUserExists(String un) throws Exception {
		try {
			String sql = "SELECT UserId FROM Users WHERE Username='"+un+"'";
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.close();
				return true;
			} else {
				cr.close();
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean isMaster(String un) throws Exception {
		try {
			String sql ="";
			if(isColumnExists("Users","Role")) {
				sql= "SELECT UserId FROM Users WHERE Username='"+un+"' AND Role=1";	
			} else {
				if(isColumnExists("Users","IsMaster")) {
					sql= "SELECT UserId FROM Users WHERE Username='"+un+"' AND IsMaster=1";
				} else {
					return false;
				}
			}			
			Cursor cr = this.db.rawQuery(sql, null);
			if (cr.getCount() > 0) {
				cr.close();
				return true;
			} else {
				cr.close();
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public boolean updateNodeSwitch(int nsid,String sn,String sc,int irdid) throws Exception {
		try {
			query="UPDATE NodeSwitches SET SwitchName='"+sn+"',Category='"+sc+"',IRDeviceId="+String.valueOf(irdid)+" WHERE NodeSwitchId="+nsid;
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean updateRoomName(int rid,String rn) throws Exception {
		try {
			query="UPDATE Rooms SET RoomName='"+rn+"' WHERE RoomId="+rid;
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public boolean toggleSensorArmed(int sid,boolean isa) throws Exception {
		int isai=0;
		try {
			isai=(isa ? 1:0);
			query="UPDATE Sensors SET IsArmed="+isai+" WHERE SensorId="+sid;
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public int saveSchedule(Schedule s) throws Exception {
		try {
			String sql = "SELECT ScheduleId FROM Schedules WHERE ScheduleId="+s.ScheduleId;
			Cursor cr = this.db.rawQuery(sql, null);
			int schid=0;
			int st=s.StartTime;
			String da="";
			for(int i=0;i<s.DatesApplicable.length;i++) {
				da+=String.valueOf(s.DatesApplicable[i])+",";
			}
			if(!da.equals("")) da=da.substring(0,da.lastIndexOf(","));

			if (cr.getCount() > 0) { // this schedule already exists and has to be altered	
				query="UPDATE Schedules SET StartTime="+String.valueOf(st)+",DaysApplicable='"+da+"',"
						+ "CanRunAfterElapsed="+String.valueOf(s.CanRunAfterElapsed?1:0)+" " 
						+ "WHERE ScheduleId="+String.valueOf(s.ScheduleId);
				this.db.execSQL(query);
				resetLastrunTime(s.ScheduleId);
				cr.close();
				return s.ScheduleId;
			} else { // doesn't exist and has to be added
				cv = new ContentValues();
				cv.put("SceneId", s.SceneId);
				cv.put("StartTime", st);
				cv.put("DaysApplicable", da);
				cv.put("IsActive", 1);
				cv.put("CanRunAfterElapsed", s.CanRunAfterElapsed?1:0);
				schid = (int) this.db.insert("Schedules", null, cv);
				resetLastrunTime(schid);
				cr.close();
				return schid;
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	// if mass edit required, please note that a commented version of earlier code is saved below
	public int saveScene(Scene s) throws Exception {
		int sceneid=0;
		try {
			cv = new ContentValues();
			String sql = "SELECT SceneId FROM Scenes WHERE SceneId="+s.SceneId;
			Cursor cr = this.db.rawQuery(sql, null);	
			if (cr.getCount() > 0) { // this scene already exists and has to be altered
				query="UPDATE Scenes SET SceneName='"+s.SceneName+"' WHERE SceneId="+String.valueOf(s.SceneId);
				this.db.execSQL(query);

				// remove all scene switches and scene sensors from this scene
				query="DELETE FROM SceneSwitches WHERE SceneId="+String.valueOf(s.SceneId);
				this.db.execSQL(query);

				query="DELETE FROM SceneSensors WHERE SceneId="+String.valueOf(s.SceneId);
				this.db.execSQL(query);
				
				query="DELETE FROM SceneIRIndices WHERE SceneId="+String.valueOf(s.SceneId);
				this.db.execSQL(query);

				sceneid=s.SceneId;
			} else { // scene doesn't exist and has to be added
				cv = new ContentValues();
				cv.put("SceneName", s.SceneName);
				cv.put("IsActive", 1);
				sceneid = (int) this.db.insert("Scenes", null, cv);
			}
			db.beginTransaction();
			// add scene switches and scene sensors either if scene exists or doesn't
			for(SceneSwitch ss:s.SceneSwitches) {
				query="INSERT INTO SceneSwitches (SceneId,NodeSwitchId,Status,Level,IsActive) VALUES ";
				query+="("+String.valueOf(sceneid)+","+String.valueOf(ss.NodeSwitchId)+","+String.valueOf((ss.IsOn) ? 1 : 0);
				query+=","+String.valueOf(ss.Level)+",1)";
				this.db.execSQL(query);
			}
			for(SceneSensor ss:s.SceneSensors) {
				if(ss.SensorId!=0) {
					cv = new ContentValues();
					cv.put("SceneId", sceneid);
					cv.put("SensorId",ss.SensorId);
					cv.put("IsArmed", ((ss.IsArmed) ? 1 : 0));
					cv.put("IsActive", 1);
					@SuppressWarnings("unused")
					int id = (int) this.db.insert("SceneSensors", null, cv);
				}
			}
			for(SceneIRIndex ss:s.SceneIRIndices) {
				if(ss.IRIndexId!=0) {
					cv = new ContentValues();
					cv.put("SceneId", sceneid);
					cv.put("IRIndexId",ss.IRIndexId);
					cv.put("Key",ss.Key);
					cv.put("Value",ss.Value);
					@SuppressWarnings("unused")
					int id = (int) this.db.insert("SceneIRIndices", null, cv);
				}
			}
			cr.close();
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			throw ex;
		} finally {
			db.endTransaction();
		}
		return sceneid;
	}

	public boolean isRoomInScene(int sceneid,int roomid) throws Exception {
		boolean isi=false;
		int nsid=0,rnid=0,rid=0;
		try {
			String sql = "SELECT NodeSwitchId FROM Scenes WHERE SceneId="+sceneid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) {
				cr.moveToFirst();
				do {
					nsid= cr.getInt(cr.getColumnIndex("NodeSwitchId"));
					sql = "SELECT RoomNodeId FROM NodeSwitches WHERE NodeSwitchId="+nsid;
					Cursor cr_rn = this.db.rawQuery(sql, null);

					if (cr_rn.getCount() > 0) {
						cr_rn.moveToFirst();
						do {
							rnid= cr_rn.getInt(cr_rn.getColumnIndex("RoomNodeId"));
							sql = "SELECT RoomId FROM RoomNodes WHERE RoomNodeId="+rnid;
							Cursor cr_r = this.db.rawQuery(sql, null);

							if (cr_r.getCount() > 0) {
								cr_r.moveToFirst();
								do {
									rid= cr_r.getInt(cr_r.getColumnIndex("RoomId"));
									if(rid==roomid) {
										isi=true;
										cr_r.close();
										cr_rn.close();
										cr.close();
										return isi;
									}
								} while (cr_r.moveToNext());
							}
							cr_r.close();
						} while (cr_rn.moveToNext());
					}
					cr_rn.close();
				} while (cr.moveToNext());
			}
			cr.close();
			return isi;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public int markScheduleApplied(int sid,long cTime) throws Exception {
		int apid=0;
		try {
			String sql = "SELECT ScheduleId FROM AppliedSchedules WHERE ScheduleId="+sid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) { // this scene already exists and has to be altered
				query="UPDATE AppliedSchedules SET LastRunTime="+String.valueOf(cTime)+" WHERE ScheduleId="+String.valueOf(sid);
				this.db.execSQL(query);
			} else { // doesn't exist and has to be added
				cv = new ContentValues();
				cv.put("ScheduleId", sid);
				cv.put("LastRunTime",cTime);
				apid = (int) this.db.insert("AppliedSchedules", null, cv);
				return apid;
			}
			cr.close();
		} catch (Exception ex) {
			throw ex;
		}
		return apid;
	}
	
	public int markChannelFavourite(STBChannel c,int uid) throws Exception {
		int apid=0;
		try {
			cv = new ContentValues();
			cv.put("STBChannelId",c.STBChannelId);
			cv.put("UserId",uid);
			apid = (int) this.db.insert("FavouriteChannels", null, cv);
			return apid;
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	public boolean unmarkChannelFavourite(STBChannel c,int uid) throws Exception {
		try {
			query="DELETE FROM FavouriteChannels WHERE STBChannelId="+String.valueOf(c.STBChannelId)+" AND UserId="+String.valueOf(uid);
			this.db.execSQL(query);
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void resetLastrunTime(int sid) throws Exception {
		try {
			query="UPDATE AppliedSchedules SET LastRunTime=0 WHERE ScheduleId="+String.valueOf(sid);
			this.db.execSQL(query);
		} catch (Exception ex) {
			throw ex;
		}
	}

	public long getScheduleLastRunTime(int sid) throws Exception {
		long lsr=0;
		try {
			String sql = "SELECT LastRunTime FROM AppliedSchedules WHERE ScheduleId="+sid;
			Cursor cr = this.db.rawQuery(sql, null);

			if (cr.getCount() > 0) { // this schedule has been run atleast once
				cr.moveToFirst();
				int ci=cr.getColumnIndexOrThrow("LastRunTime");
				lsr= cr.getLong(ci);
			} else { // not run atleast once
				lsr=0;
			}
			cr.close();
			return lsr;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public void dropTable(String tblName) throws Exception {
		try {
			this.db.beginTransaction();
			query="DROP TABLE "+tblName;
			this.db.execSQL(query);
		} catch (Exception ex) {
			throw ex;
		} finally {
			this.db.endTransaction();
		}
	}

	public void deleteAllUsers() throws Exception {
		try {
			this.db.beginTransaction();
			query="DELETE FROM Users";
			this.db.execSQL(query);
		} catch (Exception ex) {
			throw ex;
		} finally {
			this.db.endTransaction();
		}
	}

	public boolean upgradeDB() throws Exception {
		try {
			getExistingTablesList();
			getExistingData();
			deleteExistingTables();
			createTables();
			fillTablesWithData();
		} catch (Exception ex) {
			throw ex;
		}
		return true;
	}
	
	public void fillTablesWithData() throws Exception {
		this.db.beginTransaction();
		try {
			// Globals
			Globals.dbVersion=Globals.latestDBVersion;
			String gQuery="INSERT INTO `Globals` (GatewayId,SerialNumber,Email,Mobile,DateTested,DateAllocated,"
				+ "Version,BaudRate,Lastname,CustomerId,Firstname,Username) VALUES "
				+ "("+String.valueOf(Globals.gatewayId)+",'"+Globals.serialNumber+"','"+Globals.customerEmail
				+"','"+Globals.customerMobile +"',"+String.valueOf(Globals.dateTested)+","+String.valueOf(Globals.dateAllocated)
				+","+String.valueOf(Globals.dbVersion)+","+String.valueOf(Globals.baudRate)+",'"+Globals.customerLastname
				+"',"+String.valueOf(Globals.customerId)+",'"+Globals.customerFirstname +"','"+Globals.customerUsername +"')";
			if(!gQuery.isEmpty()) {
				this.db.execSQL(gQuery);
			}
			// Nodes
			String nQuery="";
			boolean is101pfound=false;
			if(Globals.AllNodes.size()>0) {
				nQuery="INSERT INTO `Nodes` (NodeId,DeviceId,NodeName,NodeType,OnOffCount,DimmerCount) VALUES ";
			}
			for(Node n:Globals.AllNodes) {
				nQuery+="("+String.valueOf(n.NodeId)+","+String.valueOf(n.CDeviceId)+",'"+n.NodeName
					+"','"+n.NodeType+"',"+String.valueOf(n.OnOffCount)+","+String.valueOf(n.DimmerCount)+"),";
				if(n.NodeName.equals("101P")) is101pfound=true;
			}
			if(!nQuery.isEmpty() && nQuery.endsWith(",")) {
				nQuery=nQuery.substring(0,nQuery.lastIndexOf(","));
				this.db.execSQL(nQuery);	
			}
			if(!is101pfound) {
				Node n=new Node();
				n.CDeviceId=3;
				n.DimmerCount=0;
				n.OnOffCount=1;
				n.HasMaster=false;
				n.NodeName="101P";
				n.NodeType="Normal";
				addNode(n);
			}
			// Users
			String uQuery="";
			boolean isMasterFound=false;
			if(Globals.AllUsers.size()>0) {
				uQuery="INSERT INTO `Users` (UserId,Username,Firstname,Lastname,Email,Password,Role) VALUES ";
			}
			for(User u:Globals.AllUsers) {
				uQuery+="("+String.valueOf(u.UserId)+",'"+u.Username+"','"+u.Firstname+"','"+u.Lastname
					+"','"+u.Email+"','"+u.Password+"',"+String.valueOf(u.Role)+"),";
				if(u.Role==1) isMasterFound=true;
			}
			if(!uQuery.isEmpty() && uQuery.endsWith(",")) {
				uQuery=uQuery.substring(0,uQuery.lastIndexOf(","));
				this.db.execSQL(uQuery);
			}
			if(!isMasterFound) {
				User u=new User();
				u.Email="master";
				u.Firstname="Master";
				u.Role=1;
				u.Lastname="Master";
				u.Password="master";
				u.Username="master";
				addUser(u);
			}
			//Rooms
			String rQuery="",rnQuery="",nsQuery="";
			if(Globals.AllRooms.size()>0) {
				rQuery="INSERT INTO `Rooms` (RoomId,RoomName) VALUES ";
			}
			for(Room r:Globals.AllRooms) {
				rQuery+="("+String.valueOf(r.RoomId)+",'"+r.RoomName+"'),";
				for(RoomNode rn:r.RoomNodes) {
					if(rnQuery.isEmpty()) 
						rnQuery="INSERT INTO `RoomNodes` (RoomNodeId,RoomId,Version,NodeId,NodeName,"
							+ "NodeIdHigher,NodeIdLower,MacIdHigher,MacIdLower) VALUES ";
					rnQuery+="("+String.valueOf(rn.RoomNodeId)+","+String.valueOf(r.RoomId)+","+String.valueOf(rn.Version)
						+","+String.valueOf(rn.NodeId)+",'"+rn.NodeName+"',"+String.valueOf(rn.NodeIdHigher)
						+","+String.valueOf(rn.NodeIdLower)+","+String.valueOf(rn.MacIdHigher)
						+","+String.valueOf(rn.MacIdLower)+"),";
					for(NodeSwitch ns:rn.NodeSwitches) {
						if(nsQuery.isEmpty()) 
							nsQuery="INSERT INTO `NodeSwitches` (NodeSwitchId,IRDeviceId,RoomNodeId,SwitchNumber,SwitchName,"
								+ "Type,Category,CustomValue) VALUES ";
						nsQuery+="("+String.valueOf(ns.NodeSwitchId)+","+String.valueOf(ns.IRDeviceId)+","
							+String.valueOf(rn.RoomNodeId)+","+String.valueOf(ns.SwitchNumber)+",'"+ns.SwitchName+"','"+ns.Type+"','"
							+ns.Category+"','"+ns.CustomValue+"'),";
					}
				}
			}
			if(!rQuery.isEmpty() && rQuery.endsWith(",")) {
				rQuery=rQuery.substring(0,rQuery.lastIndexOf(","));
				this.db.execSQL(rQuery);	
			}
			if(!rnQuery.isEmpty() && rnQuery.endsWith(",")) {
				rnQuery=rnQuery.substring(0,rnQuery.lastIndexOf(","));
				this.db.execSQL(rnQuery);	
			}
			if(!nsQuery.isEmpty() && nsQuery.endsWith(",")) {
				nsQuery=nsQuery.substring(0,nsQuery.lastIndexOf(","));
				this.db.execSQL(nsQuery);	
			}
			// Scenes
			String sQuery="",ssQuery="",ssenQuery="",siQuery="";
			if(Globals.AllScenes.size()>0) 
				sQuery="INSERT INTO `Scenes` (SceneId,SceneName) VALUES ";
			for(Scene s:Globals.AllScenes) {
				sQuery+="("+String.valueOf(s.SceneId)+",'"+s.SceneName+"'),";
				// scene switches
				for(SceneSwitch ss:s.SceneSwitches) {
					if(ssQuery.isEmpty())
						ssQuery="INSERT INTO `SceneSwitches` (SceneSwitchId,SceneId,NodeSwitchId,Status,Level) VALUES ";
					ssQuery+="("+String.valueOf(ss.SceneSwitchId)+","+String.valueOf(s.SceneId)
						+","+String.valueOf(ss.NodeSwitchId)+","+String.valueOf(ss.IsOn?1:0)+","+String.valueOf(ss.Level)+"),";
				}
				// scene sensors
				for(SceneSensor ss:s.SceneSensors) {
					if(ssenQuery.isEmpty())
						ssenQuery="INSERT INTO `SceneSensors` (SceneSensorId,SceneId,SensorId,IsArmed) VALUES ";
					ssenQuery+="("+String.valueOf(ss.SceneSensorId)+","+String.valueOf(s.SceneId)
						+","+String.valueOf(ss.SensorId)+","+String.valueOf(ss.IsArmed?1:0)+"),";
				}
				// scene irindices
				for(SceneIRIndex si:s.SceneIRIndices) {
					if(siQuery.isEmpty())
						siQuery="INSERT INTO `SceneIRIndices` (SceneIRIndexId,SceneId,IRIndexId,Key,Value) VALUES ";
					siQuery+="("+String.valueOf(si.SceneIRIndexId)+","+String.valueOf(s.SceneId)
						+","+String.valueOf(si.IRIndexId)+",'"+si.Key+"','"+si.Value+"'),";
				}
			}
			if(!sQuery.isEmpty() && sQuery.endsWith(",")) {
				sQuery=sQuery.substring(0,sQuery.lastIndexOf(","));
				this.db.execSQL(sQuery);	
			}
			if(!ssQuery.isEmpty() && ssQuery.endsWith(",")) {
				ssQuery=ssQuery.substring(0,ssQuery.lastIndexOf(","));
				this.db.execSQL(ssQuery);	
			}
			if(!ssenQuery.isEmpty() && ssenQuery.endsWith(",")) {
				ssenQuery=ssenQuery.substring(0,ssenQuery.lastIndexOf(","));
				this.db.execSQL(ssenQuery);	
			}
			if(!siQuery.isEmpty() && siQuery.endsWith(",")) {
				siQuery=siQuery.substring(0,siQuery.lastIndexOf(","));
				this.db.execSQL(siQuery);	
			}
			//Schedules
			sQuery="";
			if(Globals.AllSchedules.size()>0) 
				sQuery="INSERT INTO `Schedules` (ScheduleId,SceneId,StartTime,DaysApplicable,CanRunAfterElapsed) VALUES ";
			for(Schedule s:Globals.AllSchedules) {
				String da="";
				for(int i=0;i<s.DatesApplicable.length;i++) {
					da+=String.valueOf(s.DatesApplicable[i])+",";
				}
				if(!da.equals("")) da=da.substring(0,da.lastIndexOf(","));
				sQuery+="("+String.valueOf(s.ScheduleId)+","+String.valueOf(s.SceneId)+","+String.valueOf(s.StartTime)
					+",'"+da+"',"+String.valueOf(s.CanRunAfterElapsed?1:0)+"),";
			}
			if(!sQuery.isEmpty() && sQuery.endsWith(",")) {
				sQuery=sQuery.substring(0,sQuery.lastIndexOf(","));
				this.db.execSQL(sQuery);	
			}
			//One Touch Controllers
			String oQuery="",omQuery="";
			if(Globals.AllOneTouchControllers.size()>0)
				oQuery="INSERT INTO `OneTouchControllers` (OneTouchControllerId,NodeIdHigher,NodeIdLower,MacIdHigher,"
					+ "MacIdLower) VALUES ";
			for(OneTouchController oc:Globals.AllOneTouchControllers) {
				oQuery+="("+String.valueOf(oc.OneTouchControllerId)+","+String.valueOf(oc.NodeIdHigher)
					+","+String.valueOf(oc.NodeIdLower)+","+String.valueOf(oc.MacIdHigher)
					+","+String.valueOf(oc.MacIdLower)+"),";
				for(OneTouchControllerMapping om:oc.Mappings) {
					if(omQuery.isEmpty())
						omQuery="INSERT INTO `OneTouchControllerMappings` (MappingId,OneTouchControllerId,SwitchState,"
							+ "SwitchNumber,Key,Value) VALUES ";
					omQuery+="("+String.valueOf(om.MappingId)+","+String.valueOf(oc.OneTouchControllerId)
						+","+String.valueOf(om.SwitchState)+","+String.valueOf(om.SwitchNumber)+",'"+om.Key
						+"',"+String.valueOf(om.Value)+"),";
				}
			}
			if(!oQuery.isEmpty() && oQuery.endsWith(",")) {
				oQuery=oQuery.substring(0,oQuery.lastIndexOf(","));
				this.db.execSQL(oQuery);	
			}
			if(!omQuery.isEmpty() && omQuery.endsWith(",")) {
				omQuery=omQuery.substring(0,omQuery.lastIndexOf(","));
				this.db.execSQL(omQuery);	
			}
			//alarms
			String aQuery="";
			if(Globals.AllAlarms.size()>0)
				aQuery="INSERT INTO `Alarms` (AlarmId,Version,Type,RoomId,AlarmName,NodeIdHigher,NodeIdLower,MacIdHigher,"
					+ "MacIdLower) VALUES ";
			for(Alarm a:Globals.AllAlarms) {
				aQuery+="("+String.valueOf(a.AlarmId)+","+String.valueOf(a.Version)+",'"+a.Type+"',"+String.valueOf(a.RoomId)
					+",'"+a.AlarmName+"',"+String.valueOf(a.NodeIdHigher)+","+String.valueOf(a.NodeIdLower)
					+","+String.valueOf(a.MacIdHigher)+","+String.valueOf(a.MacIdLower)+"),";
			}
			if(!aQuery.isEmpty() && aQuery.endsWith(",")) {
				aQuery=aQuery.substring(0,aQuery.lastIndexOf(","));
				this.db.execSQL(aQuery);	
			}
			//cameras
			String cQuery="";
			if(Globals.AllCameras.size()>0)
				cQuery="INSERT INTO `Cameras` (CameraId,RoomId,Model,Name,IsVisitorCamera,SerialNumber,CustomValue," +
						"Username,Password) " +
						"VALUES ";
			for(Camera c:Globals.AllCameras) {
				cQuery+="("+String.valueOf(c.cameraId)+","+String.valueOf(c.roomId)+",'"+c.cameraModel+"','"
						+c.cameraName+"',"+String.valueOf(c.isVisitorCamera?1:0)+",'"+c.serialNumber+"','"
						+c.customValue+"','"+c.username+"','"+c.password+"'),";
			}
			if(!cQuery.isEmpty() && cQuery.endsWith(",")) {
				cQuery=cQuery.substring(0,cQuery.lastIndexOf(","));
				this.db.execSQL(cQuery);
			}
			//ir devices
			String irQuery="";
			if(Globals.AllIRDevices.size()>0)
				irQuery="INSERT INTO `IRDevices` (IRDeviceId,RoomId,DeviceName,Category,CustomValue) VALUES ";
			for(IRDevice ird:Globals.AllIRDevices) {
				irQuery+="("+String.valueOf(ird.IRDeviceId)+","+String.valueOf(ird.RoomId)+",'"+ird.DeviceName
					+"','"+ird.Category+"','"+ird.CustomValue+"'),";
			}
			if(!irQuery.isEmpty() && irQuery.endsWith(",")) {
				irQuery=irQuery.substring(0,irQuery.lastIndexOf(","));
				this.db.execSQL(irQuery);	
			}
			// ir blasters
			irQuery="";
			String iriQuery="";
			if(Globals.AllIRBlasters.size()>0)
				irQuery="INSERT INTO `IRBlasters` (IRBlasterId,RoomId,Version,NodeIdHigher,NodeIdLower,MacIdHigher,"
					+ "MacIdLower) VALUES ";
			for(IRBlaster irb:Globals.AllIRBlasters) {
				irQuery+="("+String.valueOf(irb.IRBlasterId)+","+String.valueOf(irb.RoomId)+","+String.valueOf(irb.Version)
					+","+String.valueOf(irb.NodeIdHigher)+","+String.valueOf(irb.NodeIdLower)
					+","+String.valueOf(irb.MacIdHigher)+","+String.valueOf(irb.MacIdLower)+"),";
				for(IRIndex iri:irb.IRIndices) {
					if(iriQuery.isEmpty())
						iriQuery="INSERT INTO `IRIndices` (IRIndexId,IRBlasterId,IRDeviceId,IRIndex,Value,Channel,"
								+ "RemoteButton) VALUES ";
					iriQuery+="("+String.valueOf(iri.IRIndexId)+","+String.valueOf(irb.IRBlasterId)+","
							+String.valueOf(iri.IRDeviceId)+","+String.valueOf(iri.IRIndex)+","+String.valueOf(iri.Value)+","
							+String.valueOf(iri.IRChannel)+",'"+iri.RemoteButton+"'),";
				}
			}
			if(!irQuery.isEmpty() && irQuery.endsWith(",")) {
				irQuery=irQuery.substring(0,irQuery.lastIndexOf(","));
				this.db.execSQL(irQuery);	
			}
			if(!iriQuery.isEmpty() && iriQuery.endsWith(",")) {
				iriQuery=iriQuery.substring(0,iriQuery.lastIndexOf(","));
				this.db.execSQL(iriQuery);	
			}
			// sensors
			sQuery="";
			if(Globals.AllSensors.size()>0)
				sQuery="INSERT INTO `Sensors` (SensorId,Version,Type,RoomId,SensorName,NodeIdHigher,NodeIdLower,MacIdHigher,"
					+ "MacIdLower,IsArmed) VALUES ";
			for(Sensor s:Globals.AllSensors) {
				sQuery+="("+String.valueOf(s.SensorId)+","+String.valueOf(s.Version)+",'"+s.Type+"',"+String.valueOf(s.RoomId)
					+",'"+s.SensorName+"',"+String.valueOf(s.NodeIdHigher)+","+String.valueOf(s.NodeIdLower)
					+","+String.valueOf(s.MacIdHigher)+","+String.valueOf(s.MacIdLower)+","+String.valueOf(s.IsArmed?1:0)+"),";
			}
			if(!sQuery.isEmpty() && sQuery.endsWith(",")) {
				sQuery=sQuery.substring(0,sQuery.lastIndexOf(","));
				this.db.execSQL(sQuery);	
			}
			// stbs
			sQuery="";ssQuery="";
			if(Globals.AllSetTopBoxes.size()>0)
				sQuery="INSERT INTO `SetTopBoxes` (SetTopBoxId,Name,Make,Model) VALUES ";
			for(SetTopBox stb:Globals.AllSetTopBoxes) {
				sQuery+="("+String.valueOf(stb.SetTopBoxId)+",'"+stb.Name+"','"+stb.Make+"','"+stb.Model+"'),";
				for(STBChannel stbc:stb.Channels) {
					if(ssQuery.isEmpty())
						ssQuery="INSERT INTO `STBChannels` (STBChannelId,SetTopBoxId,Language,Category,Number,Name,"
							+ "Filename) VALUES ";
					ssQuery+="("+String.valueOf(stbc.STBChannelId)+","+String.valueOf(stb.SetTopBoxId)+",'"+stbc.Language
						+"','"+stbc.Category+"',"+String.valueOf(stbc.Number)+",'"+stbc.Name+"','"+stbc.Filename+"'),";
				}
			}
			if(!sQuery.isEmpty() && sQuery.endsWith(",")) {
				sQuery=sQuery.substring(0,sQuery.lastIndexOf(","));
				this.db.execSQL(sQuery);	
			}
			if(!ssQuery.isEmpty() && ssQuery.endsWith(",")) {
				ssQuery=ssQuery.substring(0,ssQuery.lastIndexOf(","));
				this.db.execSQL(ssQuery);	
			}
			// applied schedules
			aQuery="";
			if(Globals.AllAppliedSchedules.size()>0)
				aQuery="INSERT INTO AppliedSchedules (AppliedId,ScheduleId,LastRunTime) VALUES ";
			for(AppliedSchedule s:Globals.AllAppliedSchedules) {
				aQuery+="("+String.valueOf(s.AppliedId)+","+String.valueOf(s.ScheduleId)+","+String.valueOf(s.LastRunTime)+"),";
			}
			if(!aQuery.isEmpty() && aQuery.endsWith(",")) {
				aQuery=aQuery.substring(0,aQuery.lastIndexOf(","));
				this.db.execSQL(aQuery);	
			}
			query="COMMIT";
			this.db.execSQL(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			//this.db.endTransaction();
		}
	}
}