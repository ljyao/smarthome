package com.smarthome.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "BLDevice")
public class DeviceInfo {
	@DatabaseField(id = true)
	public String mac;
	@DatabaseField
	public int type;
	@DatabaseField
	public String name;
	@DatabaseField
	public Boolean lock;
	@DatabaseField
	public int password;
	@DatabaseField
	public int id;
	@DatabaseField
	public String key;
	@DatabaseField
	public int subdevice;
	@DatabaseField
	public String lanaddr;
	public boolean status=false;

	public DeviceInfo() {

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMac() {
		return mac;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Boolean getLock() {
		return lock;
	}

	public int getPassword() {
		return password;
	}

	public int getId() {
		return id;
	}

	public int getSubdevice() {
		return subdevice;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLock(Boolean lock) {
		this.lock = lock;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public String getLanaddr() {
		return lanaddr;
	}

	public void setLanaddr(String lanaddr) {
		this.lanaddr = lanaddr;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSubdevice(int subdevice) {
		this.subdevice = subdevice;
	}

}