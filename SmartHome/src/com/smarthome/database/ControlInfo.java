package com.smarthome.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ControlInfo")
public class ControlInfo {
	@DatabaseField(generatedId=true)
	public int id;
	@DatabaseField(foreign = true, canBeNull=false,columnName="remote_id",foreignAutoRefresh = true)
	public RemoteInfo remote;
	@DatabaseField
	public String name;
	@DatabaseField
	public String code;
	public ControlInfo() {

	}

}
