package com.smarthome.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "CameraDeviceInfo")
public class CameraDeviceInfo implements Serializable {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String name;
	@DatabaseField
	public String username;
	@DatabaseField
	public String userpwd;
	@DatabaseField
	public String serverip;
	@DatabaseField(defaultValue="8000")
	public int serverport;

	public CameraDeviceInfo() {

	}
}