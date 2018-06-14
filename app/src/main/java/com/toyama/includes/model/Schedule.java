package com.toyama.includes.model;


public class Schedule {
	
	public int ScheduleId,SceneId;
	public boolean IsActive,CanRunAfterElapsed;
	public int StartTime;
	public int[] DatesApplicable;
	public long LastRunTime;
	public String SceneName;
	
	public Schedule() {
		super();
		DatesApplicable=new int[7];
		LastRunTime=0;
		for(int i=0;i<DatesApplicable.length;i++) {
			DatesApplicable[i]=0;
		}
	}

	public String toString() {
		String schDet="";
        
        if(this.DatesApplicable[0]==1) schDet+="SU,";
		if(this.DatesApplicable[1]==1) schDet+="M,";
		if(this.DatesApplicable[2]==1) schDet+="T,";
		if(this.DatesApplicable[3]==1) schDet+="W,";
		if(this.DatesApplicable[4]==1) schDet+="TH,";
		if(this.DatesApplicable[5]==1) schDet+="F,";
		if(this.DatesApplicable[6]==1) schDet+="SA";
		
		int sTime=this.StartTime/60; // first convert to minutes after 00:00 in the day
		
		int sHours=sTime/60;
		int sMins=sTime%60;
		
		if(sHours<10) schDet+=" at 0"+sHours;
		else schDet+=" at "+sHours;
		
		if(sMins<10) schDet+=":0"+sMins;
		else schDet+=":"+sMins;

		return schDet;
	}
}
