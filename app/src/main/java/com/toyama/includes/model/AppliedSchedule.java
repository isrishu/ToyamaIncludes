package com.toyama.includes.model;

public class AppliedSchedule {
	public int AppliedId,ScheduleId;
	public long LastRunTime;
	
	public AppliedSchedule(int appliedId, int scheduleId, long lastRunTime) {
		super();
		AppliedId = appliedId;
		ScheduleId = scheduleId;
		LastRunTime = lastRunTime;
	}

	public AppliedSchedule() {
		super();
	}
}
