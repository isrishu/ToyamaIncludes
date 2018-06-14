package com.toyama.includes.utilities;

import android.net.Uri;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ServiceLayer {
	private String requestMethod ="";
	private String urlPath="http://toyamawizhom.com/v11/api/ToyamaAPI.php";
	private String paramString="";
	private String caller="";
	private String timeStampString="";
	private OutputStream outputStream=null;
	private OutputStreamWriter outputStreamWriter=null;
	private InputStream inputStream = null;
	private InputStream inputErrorStream = null;
	private BufferedReader bufferedReader=null;
	private BufferedWriter bufferedWriter=null;
	private HttpURLConnection httpURLConnection = null;
	private HttpsURLConnection httpsURLConnection = null;
	private URL url = null;
	private Uri.Builder uriBuilder=null;
	StringBuilder responseSB=null;
	private HashMap<String, String> paramHashMap = null;
	private int customerId=0,gatewayId=0;
    private int responseCode=0;
    private String responseStatus="";
    private String responseString="";
	private boolean isErrorSet=false;

	public ServiceLayer() {
		try {
			this.url = new URL(this.urlPath);
			this.httpURLConnection = (HttpURLConnection) this.url.openConnection();
			this.httpURLConnection.setRequestMethod("POST");
			this.httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			this.httpURLConnection.setUseCaches(false);
			this.httpURLConnection.setDoInput(true);
			this.httpURLConnection.setDoOutput(true);
			this.httpURLConnection.setReadTimeout(10000);
			this.httpURLConnection.setConnectTimeout(15000);

			this.outputStream=this.httpURLConnection.getOutputStream();
			if(this.outputStream==null) {
				this.responseString+=" OS Null";
				this.isErrorSet=true;
			}
			this.outputStreamWriter=new OutputStreamWriter(this.outputStream, "UTF-8");
			if(this.outputStreamWriter==null) {
				this.responseString+=" OSW Null";
				this.isErrorSet=true;
			}
			this.bufferedWriter = new BufferedWriter(this.outputStreamWriter);
			if(this.bufferedWriter==null) {
				this.responseString+=" BW Null";
				this.isErrorSet=true;
			}

			this.paramHashMap = new HashMap<String,String>();
			this.responseSB=new StringBuilder();
			this.customerId=Globals.customerId;
			this.gatewayId=Globals.gatewayId;
			this.caller=Globals.remoteServiceCaller;
		} catch (Exception ex) {
			this.responseString+=" "+ex.getMessage();
			this.isErrorSet=true;
		}
		if(isErrorSet) {
			this.responseString="-1#Unable to connect to the Internet. Please check and try again. "+this.responseString;
		}
	}

	public String getResponseString() {
		return this.responseString;
	}

	public boolean getIsErrorSet() {
		return this.isErrorSet;
	}
	
	private boolean postData() {
		String line="";
		boolean status=false;
		try {
			this.uriBuilder=new Uri.Builder();
			this.uriBuilder.appendQueryParameter("rquest", this.requestMethod);
			this.uriBuilder.appendQueryParameter("gid", String.valueOf(this.gatewayId));
			this.uriBuilder.appendQueryParameter("caller", this.caller);
			this.uriBuilder.appendQueryParameter("paramString",this.paramString);

			this.bufferedWriter.write(this.uriBuilder.build().getEncodedQuery());
			this.bufferedWriter.flush();
			this.bufferedWriter.close();
			this.httpURLConnection.getOutputStream().close();
			this.httpURLConnection.connect();

			this.responseCode=this.httpURLConnection.getResponseCode();
			if (this.httpURLConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
				this.inputStream = this.httpURLConnection.getInputStream();
			} else {
				this.inputStream = this.httpURLConnection.getErrorStream();
				this.responseString="Status Code: "+this.responseCode+" ";
			}
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
			while ((line = this.bufferedReader.readLine()) != null) {
				this.responseSB.append(line);
			}
			this.responseString+=this.responseSB.toString();
			status=true;
		} catch (IOException e) {
			this.responseString="-1#Unable to connect to the Internet. Please check and try again.";
			this.responseString+="Error: "+e.getMessage();
		} catch(Exception ex) {
			this.responseString="-1#Unexpected error occured. Please check and try again.";
			this.responseString+="Error: "+ex.getMessage();
		}
		return status;
	}

	private String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for(Map.Entry<String, String> entry : params.entrySet()){
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		return result.toString();
	}

	public boolean toggleSwitch(String paramString) {
		this.requestMethod="toggleSwitch";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean toggleSensor(String paramString) {
		this.requestMethod="toggleSensor";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean sendIR(String paramString) {
		this.requestMethod="sendIRCommand";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean toggleSwitches(String paramString) {
		this.requestMethod="toggleSwitches";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean toggleAHUStatuses(String paramString) {
		this.requestMethod="toggleAHUStatuses";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean getSwitchesToToggle() {
		this.requestMethod="getSwitchesToToggle";
		this.paramString="";
		return this.postData();
	}

	public boolean getSensorsToToggle() {
		this.requestMethod="getSensorsToToggle";
		this.paramString="";
		return this.postData();
	}

	public boolean querySensors() {
		this.requestMethod="querySensors";
		this.paramString="";
		return this.postData();
	}

	public boolean getScenesToApply() {
		this.requestMethod="getScenesToApply";
		this.paramString="";
		return this.postData();
	}

	public boolean getIRCommandsToExecute() {
		this.requestMethod="getIRCommandsToExecute";
		this.paramString="";
		return this.postData();
	}

	public boolean applyScene(String paramString) {
		this.requestMethod="applyScene";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean querySwitch(String paramString) {
		this.requestMethod="querySwitch";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean queryAHU(String paramString) {
		this.requestMethod="queryAHU";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean addGateway() {
		this.requestMethod="addGateway";
		this.paramString=String.valueOf(Globals.serialNumber)+"*"+String.valueOf(Globals.dbVersion)
				+"*"+String.valueOf(Globals.dateTested)+"*"+String.valueOf(Globals.baudRate);
		return this.postData();
	}

	public boolean registerGateway() {
		this.requestMethod="registerGateway";
		this.paramString=String.valueOf(Globals.customerId)+"*"+String.valueOf(Globals.dateAllocated);
		return this.postData();
	}

	public boolean uploadUsers() {
		this.requestMethod="saveUsers";
		this.paramString=Utilities.generateAllUsersString();
		return this.postData();
	}

	public boolean uploadRooms() {
		this.requestMethod="saveRooms";
		this.paramString=Utilities.generateAllRoomsString();
		return this.postData();
	}

	public boolean uploadScenes() {
		this.requestMethod="saveScenes";
		this.paramString=Utilities.generateAllScenesString();
		return this.postData();
	}

	public boolean uploadSchedules() {
		this.requestMethod="saveSchedules";
		this.paramString=Utilities.generateAllSchedulesString();
		return this.postData();
	}

	public boolean uploadSensors() {
		this.requestMethod="saveSensors";
		this.paramString=Utilities.generateAllSensorsString();
		return this.postData();
	}

	public boolean uploadIRBlasters() {
		this.requestMethod="saveIRBlasters";
		this.paramString=Utilities.generateAllIRBlastersString();
		return this.postData();
	}

	public boolean uploadIRDevices() {
		this.requestMethod="saveIRDevices";
		this.paramString=Utilities.generateAllIRDevicesString();
		return this.postData();
	}

	public boolean uploadSetTopBoxes() {
		this.requestMethod="saveSetTopBoxes";
		this.paramString=Utilities.generateAllSetTopBoxesString();
		return this.postData();
	}

	public boolean uploadCameras() {
		this.requestMethod="saveCameras";
		this.paramString=Utilities.generateAllCamerasString();
		return this.postData();
	}

	public boolean getCustomerDetailsByUsername(String paramString) {
		this.requestMethod="getCustomerDetailsByUsername";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean getUsers(String paramString) {
		this.requestMethod="getUsers";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean login() {
		this.requestMethod="login";
		this.paramString=Globals.customerUsername +"_"+Globals.password;
		return this.postData();
	}

	public boolean markSceneApplied(String paramString) {
		this.requestMethod="markSceneApplied";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean markIRCommandExecuted(String paramString) {
		this.requestMethod="markIRCommandExecuted";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean updateFCMToken(String paramString) {
		this.requestMethod="updateFCMToken";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean saveGatewayVersion(String paramString) {
		this.requestMethod="saveGatewayVersion";
		this.paramString=paramString;
		return this.postData();
	}

	public boolean checkFCMTokenValidity(String paramString) {
		this.requestMethod="checkFCMTokenValidity";
		this.paramString=paramString;
		return this.postData();
	}
	
	public void close() {
		try {
			if(this.inputStream!=null) 
				this.inputStream.close();
			this.httpURLConnection=null;
			this.bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}