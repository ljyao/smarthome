package com.smarthome.database;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "RemoteInfo")
public class RemoteInfo implements Serializable {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String name;
	@DatabaseField
	public int type;
	public static final int tv = 1;
	public static final int air = 2;
	public static final int diy = 3;

	public RemoteInfo() {
	}

}
