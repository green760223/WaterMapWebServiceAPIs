package tw.com.model;

import java.util.Map;

public class WaterPointsBasicOutputModel 
{
	private String uuid;
	
	private String waterPointName;
	
	private Map<String, Object> location;
	
	private String description;
	
	private String hotWater;
	
	private String warmWater;
	
	private String coldWater;
	
	private String icedWater;
	
	private String waterPointTypes;
	

	
	public String getWaterPointTypes() {
		return waterPointTypes;
	}

	public void setWaterPointTypes(String waterPointTypes) {
		this.waterPointTypes = waterPointTypes;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getWaterPointName() {
		return waterPointName;
	}

	public void setWaterPointName(String waterPointName) {
		this.waterPointName = waterPointName;
	}

	public Map<String, Object> getLocation() {
		return location;
	}

	public void setLocation(Map<String, Object> location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHotWater() {
		return hotWater;
	}

	public void setHotWater(String hotWater) {
		this.hotWater = hotWater;
	}

	public String getWarmWater() {
		return warmWater;
	}

	public void setWarmWater(String warmWater) {
		this.warmWater = warmWater;
	}

	public String getColdWater() {
		return coldWater;
	}

	public void setColdWater(String coldWater) {
		this.coldWater = coldWater;
	}

	public String getIcedWater() {
		return icedWater;
	}

	public void setIcedWater(String icedWater) {
		this.icedWater = icedWater;
	}

	
	@Override
	public String toString() {
		return "WaterPointsBasicOutputModel [uuid=" + uuid + ", waterPointName=" + waterPointName + ", location="
				+ location + ", description=" + description + ", hotWater=" + hotWater + ", warmWater=" + warmWater
				+ ", coldWater=" + coldWater + ", icedWater=" + icedWater + ", waterPointTypes=" + waterPointTypes
				+ "]";
	}
	
}
