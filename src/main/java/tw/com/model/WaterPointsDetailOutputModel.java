package tw.com.model;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class WaterPointsDetailOutputModel 
{
	private String uuid;
	
	private String waterPointName;
	
	private Map<String, Object> location;

	private String facility;
	
	private String facilityBrand;
	
	private String floor;
	
	private String openingHours;
	
	private String source;
	
	private String description;
	
	private String hotWater;
	
	private String warmWater;
	
	private String coldWater;
	
	private String icedWater;
	
	private String rate;
	
	private String address;
	
	private String status;
	
	private String image; 
	
	private String waterPointTypes;
	
	private String city;
	
	private String district;
	
	private Double distance;

	
	
	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
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

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getFacilityBrand() {
		return facilityBrand;
	}

	public void setFacilityBrand(String facilityBrand) {
		this.facilityBrand = facilityBrand;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getWaterPointTypes() {
		return waterPointTypes;
	}
	
	public void setWaterPointTypes(String waterPointTypes) {
		this.waterPointTypes = waterPointTypes;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	
	public Map<String, Object> getLocation() {
		return location;
	}

	public void setLocation(Map<String, Object> location) 
	{	
		this.location = location;
	}


	@Override
	public String toString() {
		return "WaterPointsOutputModel [uuid=" + uuid + ", waterPointName=" + waterPointName + ", location=" + location
				+ ", facility=" + facility + ", facilityBrand=" + facilityBrand + ", floor=" + floor + ", openingHours="
				+ openingHours + ", source=" + source + ", description=" + description + ", hotWater=" + hotWater
				+ ", warmWater=" + warmWater + ", coldWater=" + coldWater + ", icedWater=" + icedWater + ", rate="
				+ rate + ", address=" + address + ", status=" + status + ", image=" + image + ", waterPointTypes="
				+ waterPointTypes + ", city=" + city + ", district=" + district + "]";
	}

}
