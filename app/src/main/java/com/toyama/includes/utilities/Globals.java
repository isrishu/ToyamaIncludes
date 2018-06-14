package com.toyama.includes.utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import com.toyama.includes.hikvision.HikVisionCameraManager;
import com.toyama.includes.model.AHUStatus;
import com.toyama.includes.model.Alarm;
import com.toyama.includes.model.AppliedSchedule;
import com.toyama.includes.model.Camera;
import com.toyama.includes.model.CameraModel;
import com.toyama.includes.model.CustomButton;
import com.toyama.includes.model.IRBlaster;
import com.toyama.includes.model.IRChannel;
import com.toyama.includes.model.IRDevice;
import com.toyama.includes.model.Node;
import com.toyama.includes.model.NodeSwitch;
import com.toyama.includes.model.Room;
import com.toyama.includes.model.Scene;
import com.toyama.includes.model.OneTouchController;
import com.toyama.includes.model.Schedule;
import com.toyama.includes.model.Sensor;
import com.toyama.includes.model.SetTopBox;
import com.toyama.includes.model.ToyamaSwitch;
import com.toyama.includes.model.User;
import com.easylogic.usbserial.driver.UsbSerialPort;

import android.content.SharedPreferences;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

public class Globals {
    public static String IPAddress="";
    public static int localPort=0;
    public static boolean isRemoteTaskRunning=false;
	public static String customerUsername ="",customerFirstname ="",customerLastname ="",
			serialNumber="", customerEmail ="", customerMobile ="";
    
    // gateway/gatewaysetup variables
	public static UsbManager usbManager;
	public static UsbSerialPort coordinatorUSBSerialPort=null;
	public static UsbDeviceConnection connection;
	public static DBLayer dbLayer;
 	public static int gatewayId=0,customerId=0;
 	public static long dateTested=0,dateAllocated=0;
    public static int baudRate=9600;
    public static String gatewayVersion="";
    public static double dbVersion=3.95,latestDBVersion=3.991;
    public static boolean isGatewayLoaded=true,isFCMTokenRefreshed=false;

    //wizhom variables
	public static String username ="",password="";
    public static String wizhomVersion="";
    public static final String ipFilename = "WizHomIPs";
	public static final String dataFilename = "WizHomData";
	public static final String credsFilename = "WizHomCredentials";
	public static final String credsXMLFilename = "WizHomCredentials.xml";
	public static SharedPreferences sharedPreferences;
	public static String connectionMode = "Local";
	public static String dataTransferMode="android";
	public static String networkCableConnectionMode="WiFi";
	public static ArrayList<String> ipList=new ArrayList<String>();
	public static String gatewayIPAddress="192.168.0.100";
	public static int ipPart4=0;
	public static String[] IPParts;
	public static final int serverSocketPORT = 8080;
	public static boolean isGatewayFound=false;
	public static ArrayList<String> potentialIPs=new ArrayList<String>();
	public static boolean isUserMaster=false;
	public static boolean didUserLogout=false,isUserJustLoggedIn=false;
	public static boolean isScheduleEditing=false;
	public static boolean isSceneEditing=false;
	public static String connectionLostMessage="Connection to Gateway lost. Please check if Gateway is running and WiFi is connected. If yes, please restart the gateway. If the problem persists, contact support.";
	public static String unexpectedErrorMessage="Oops, an unexpected error occured. Please try again. If problem persists, please contact support.";
	public static String loginMessageToSend="";

    // common variables	
	public static final byte[] stx={(byte)0xAA,(byte)0x55};
    public static final byte[] etx={(byte)0xAA,(byte)0x55};

    //hikvision variables
	public static String hikvisionCameraIP="",hikvisionUsername="",hikvisionPassword="";
	public static Integer hikvisionPort=8000;
	public static HikVisionCameraManager hikVisionCameraManager;

    public static int[] OneTouchControllerSwitchStates=new int[9];
    public static ArrayList<Node> AllNodes=new ArrayList<Node>();
    public static ArrayList<User> AllUsers=new ArrayList<User>();
    public static ArrayList<ToyamaSwitch> Switches=new ArrayList<ToyamaSwitch>();
    public static ArrayList<Room> AllRooms=new ArrayList<Room>();
    public static ArrayList<Scene> AllScenes=new ArrayList<Scene>();
    public static ArrayList<Schedule> AllSchedules=new ArrayList<Schedule>();
    public static ArrayList<Sensor> AllSensors=new ArrayList<Sensor>();
    public static ArrayList<Alarm> AllAlarms=new ArrayList<Alarm>();
    public static ArrayList<IRBlaster> AllIRBlasters=new ArrayList<IRBlaster>();
    public static ArrayList<SetTopBox> AllSetTopBoxes=new ArrayList<SetTopBox>();
    public static ArrayList<IRDevice> AllIRDevices=new ArrayList<IRDevice>();
    public static ArrayList<CustomButton> AllCustomButtons=new ArrayList<CustomButton>();
    public static ArrayList<OneTouchController> AllOneTouchControllers=new ArrayList<OneTouchController>();
    public static ArrayList<Camera> AllCameras=new ArrayList<Camera>();
    public static ArrayList<CameraModel> AllCameraModels=new ArrayList<CameraModel>();
    public static ArrayList<AppliedSchedule> AllAppliedSchedules=new ArrayList<AppliedSchedule>();
    public static ArrayList<IRDevice> climateDevices=new ArrayList<IRDevice>();
    public static ArrayList<IRDevice> mediaDevices=new ArrayList<IRDevice>();
    public static ArrayList<AHUStatus> ahuStatuses=new ArrayList<AHUStatus>();
    
    public static ArrayList<String> SensorTypes=new ArrayList<String>();
    public static ArrayList<String> SwitchCategories=new ArrayList<String>();
    public static ArrayList<String> IRDeviceCategories=new ArrayList<String>();
    public static ArrayList<String> SetTopBoxCategories=new ArrayList<String>();
    public static ArrayList<IRChannel> IRChannels=new ArrayList<IRChannel>();
    
    public static Scene CurrentScene=new Scene();
    public static Camera CurrentCamera=new Camera();
    public static IRDevice currentIRDevice=new IRDevice();
    public static NodeSwitch currentNodeSwitch=new NodeSwitch();
    
    public static byte toyamastx=(byte)0xF7;
    public static byte deviceid=(byte)0x03;
    public static byte command=(byte)0x41;
	public static byte updateall_command=(byte)0x51;
    public static byte onoff=(byte)0x01;
    
    public static byte[] StatusQueryV1 ={(byte)0xF7,(byte)0x03,(byte)0xF0,(byte)0x01,(byte)0x00,(byte)0x05,(byte)0xF0};
	public static byte[] StatusQueryV2={(byte)0xF7,(byte)0x03,(byte)0x10,(byte)0x01,(byte)0x00,(byte)0xE5,(byte)0xF0};
	public static byte[] AHUStatusQuery={(byte)0xF7,(byte)0x03,(byte)0x10,(byte)0x01,(byte)0x01,(byte)0x00,(byte)0xE4,
			(byte)0xF0};
    public static byte[] AHUStatusQueryNew={(byte)0xF7,(byte)0x03,(byte)0x10,(byte)0x01,(byte)0x01,(byte)0x00,
			(byte)0xE4,(byte)0xF0};
	public static byte[] UpdateAllSwitchStates ={(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,
			(byte)0x01,(byte)0x01,(byte)0x01};

    public static String remoteServiceCaller="";
	public static String currentIPAddress="";
	public static Socket socket=null;
	public static SocketAddress serverSocketAddress=null;
	public static DataOutputStream dataOutputStream = null;
	public static DataInputStream dataInputStream = null;
	public static boolean isUsersLoaded=false;
	public static boolean isRoomsLoaded=false;
	public static boolean isScenesLoaded=false;
	public static boolean isSchedulesLoaded=false;
	public static boolean isSensorsLoaded=false;
	public static boolean isForScene=false; // will be set to true if rooms are displayed for scenes
	public static boolean isIRBlastersLoaded=false;
	public static boolean isRoomSaved=false;
	public static boolean isSceneSaved=false;
	public static boolean isScheduleSaved=false;
	public static boolean isSetTopBoxesLoaded=false;
	public static boolean isIRDevicesLoaded=false;
	public static boolean isCamerasLoaded=false;
	
	public static int currentRoomId=0;
	public static int currentRoomNodeId=0;
	public static int currentSceneId=0;
	public static int currentScheduleId=0;
	public static int currentNodeSwitchId=0;
	public static int currentIRDeviceId=0;
	public static int currentSetTopBoxId=0;
	public static int currentCameraId=0;
	public static int currentAHUId=0;
	public static String currentChannelsMode="All";
	public static String loginError="";
	public static String irMode="";

	public static User CurrentUser=new User();
	public static Schedule CurrentSchedule=new Schedule();
	
	public static String usersString="",roomsString="",scenesString="",schedulesString="",sensorsString="",
			irBlastersString="",stbStr="",irDevicesString="",camerasString="";

	// only create statements...
	public static String createDBSQL="" +
			"CREATE TABLE 'Users' (`Role` NUMERIC,`Lastname` TEXT,`Firstname` TEXT,`UserId` INTEGER," +
			"`Username` TEXT,`Password` TEXT,`Email` TEXT,PRIMARY KEY(`UserId`));" +
			"CREATE TABLE 'SetTopBoxes' (`SetTopBoxId` INTEGER,`Name` TEXT,`Make` TEXT,`Model` TEXT," +
			"PRIMARY KEY(`SetTopBoxId`));" +
			"CREATE TABLE 'Sensors' (`IsArmed`	NUMERIC,`Version`	NUMERIC,`Type`	TEXT,`DeviceId`	NUMERIC," +
			"`MacIdLower` NUMERIC,`MacIdHigher`	NUMERIC,`NodeIdLower` NUMERIC,`NodeIdHigher` NUMERIC," +
			"`SensorId`	INTEGER,`SensorName` TEXT,`RoomId` INTEGER,PRIMARY KEY(`SensorId`));" +
			"CREATE TABLE 'Schedules' (`CanRunAfterElapsed`	NUMERIC,`DateCreated` NUMERIC,`IsActive` NUMERIC," +
			"`CreatorId` NUMERIC,`DaysApplicable` TEXT,`StartTime` NUMERIC,`ScheduleId`	INTEGER," +
			"`SceneId` NUMERIC,PRIMARY KEY(`ScheduleId`));" +
			"CREATE TABLE 'Scenes' (`CreatorId`	NUMERIC,`DateCreated` TEXT,`IsActive` NUMERIC,`SceneId`	INTEGER," +
			"`SceneName` TEXT,PRIMARY KEY(`SceneId`));" +
			"CREATE TABLE SceneSwitches (IsActive NUMERIC, SceneSwitchId INTEGER PRIMARY KEY, SceneId NUMERIC, " +
			"NodeSwitchId NUMERIC, Status NUMERIC, Level NUMERIC);" +
			"CREATE TABLE SceneSensors (SceneSensorId INTEGER PRIMARY KEY, SceneId NUMERIC, SensorId NUMERIC, " +
			"IsArmed NUMERIC, IsActive NUMERIC);" +
			"CREATE TABLE OneTouchControllers (MacIdLower NUMERIC, MacIdHigher NUMERIC, NodeIdLower NUMERIC, " +
			"NodeIdHigher NUMERIC, Version NUMERIC, OneTouchControllerId INTEGER PRIMARY KEY);" +
			"CREATE TABLE OneTouchControllerMappings (OneTouchControllerId NUMERIC, SwitchState NUMERIC, " +
			"MappingId INTEGER PRIMARY KEY, SwitchNumber NUMERIC, Key TEXT, Value NUMERIC);" +
			"CREATE TABLE SceneIRIndices (SceneIRIndexId INTEGER PRIMARY KEY,SceneId NUMERIC, IRIndexId NUMERIC," +
			"Key TEXT,Value TEXT);" +
			"CREATE TABLE 'STBChannels' ( `Language` TEXT, `Category` TEXT, `Number` NUMERIC, `Name` TEXT, " +
			"`Filename` TEXT, `SetTopBoxId` NUMERIC, `STBChannelId` INTEGER, PRIMARY KEY(`STBChannelId`));" +
			"CREATE TABLE 'Rooms' (`RoomId`	INTEGER,`RoomName`	TEXT,`IsActive`	NUMERIC,PRIMARY KEY(`RoomId`));" +
			"CREATE TABLE 'RoomNodes' (`Version` NUMERIC,`NodeIdLower` NUMERIC,`NodeIdHigher` NUMERIC," +
			"`MacIdLower` NUMERIC,`MacIdHigher`	NUMERIC,`RoomNodeId` INTEGER,`NodeName`	TEXT," +
			"`NodeId` NUMERIC,`RoomId` NUMERIC,`IsActive` NUMERIC,PRIMARY KEY(`RoomNodeId`));" +
			"CREATE TABLE Nodes (DeviceId NUMERIC, NodeId INTEGER PRIMARY KEY, NodeName TEXT, NodeType TEXT, " +
			"OnOffCount NUMERIC, DimmerCount NUMERIC,HasMaster NUMERIC);" +
			"CREATE TABLE 'NodeSwitches' (`SwitchNumber` NUMERIC, `RoomNodeId` NUMERIC, `NodeSwitchId` INTEGER, " +
			"`SwitchName` TEXT, `Category` TEXT, `Type` TEXT, `CustomValue` TEXT, `IsActive` NUMERIC, " +
			"`IRDeviceId` NUMERIC, PRIMARY KEY(`NodeSwitchId`));" +
			"CREATE TABLE IRIndices (Value NUMERIC, IRBlasterId NUMERIC, Channel NUMERIC, " +
			"IRIndexId INTEGER PRIMARY KEY, IRDeviceId NUMERIC, RemoteButton TEXT, IRIndex NUMERIC);" +
			"CREATE TABLE IRDevices (CustomValue TEXT, IRDeviceId INTEGER PRIMARY KEY, DeviceName TEXT, " +
			"Category TEXT, RoomId NUMERIC);" +
			"CREATE TABLE IRBlasters (RoomId NUMERIC, Version NUMERIC, IRBlasterId INTEGER PRIMARY KEY, " +
			"DeviceId NUMERIC, MacIdHigher NUMERIC, MacIdLower NUMERIC, NodeIdHigher NUMERIC, NodeIdLower NUMERIC);" +
			"CREATE TABLE Globals (GatewayId INTEGER PRIMARY KEY,SerialNumber TEXT,Email TEXT,Mobile TEXT," +
			"DateTested INTEGER,DateAllocated INTEGER, Version TEXT, BaudRate NUMERIC, Lastname TEXT, " +
			"CustomerId INTEGER, Firstname TEXT, Username TEXT, IsActive NUMERIC);" +
			"CREATE TABLE CustomButtons (CustomButtonId INTEGER PRIMARY KEY, ButtonName TEXT, RemoteButton TEXT, " +
			"IRDeviceId NUMERIC);" +
			"CREATE TABLE 'Cameras' (`CameraId` INTEGER, `RoomId` NUMERIC, `Model` TEXT, `Name` TEXT, " +
			"`IsActive` NUMERIC, `IsVisitorCamera` INTEGER,`SerialNumber` TEXT, " +
			"`CustomValue` TEXT,`Username` TEXT,`Password` TEXT, PRIMARY KEY(`CameraId`));" +
			"CREATE TABLE CameraModels (CameraModelId INTEGER PRIMARY KEY, Model TEXT, Package TEXT);" +
			"CREATE TABLE AppliedSchedules (AppliedId INTEGER PRIMARY KEY, ScheduleId NUMERIC, LastRunTime NUMERIC);" +
			"CREATE TABLE 'Alarms' (`AlarmId`	INTEGER,`Version`	NUMERIC,`Type`	TEXT,`DeviceId`	NUMERIC," +
			"`MacIdHigher`	NUMERIC,`MacIdLower`	NUMERIC,`NodeIdHigher`	NUMERIC,`NodeIdLower`	NUMERIC," +
			"`AlarmName`	TEXT,`RoomId`	INTEGER,PRIMARY KEY(`AlarmId`));" +
			"CREATE TABLE FavouriteChannels (FavouriteChannelId INTEGER PRIMARY KEY,STBChannelId NUMERIC, " +
			"UserId NUMERIC);";
}