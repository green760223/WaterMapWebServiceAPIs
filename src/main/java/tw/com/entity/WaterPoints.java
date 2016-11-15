package tw.com.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name="WATER_POINTS")
public class WaterPoints implements Serializable
{	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="uuid")
	private String uuid;
	
	@Column(name="water_point_name")
	private String waterPointName;
	
	@Column(name="location", columnDefinition="Geometry")
	@Type(type="org.hibernate.spatial.GeometryType")
	private Point location;
	
	@Column(name="facility")
	private String facility;
	
	@Column(name="facility_brand")
	private String facilityBrand;

	@Column(name="floor")
	private String floor;
	
	@Column(name="opening_hours")
	private String openingHours;
	
	@Column(name="source")
	private String source;
	
	@Column(name="description")
	private String description;
	
	@Column(name="hot_water")
	private String hotWater;
	
	@Column(name="warm_water")
	private String warmWater;
	
	@Column(name="cold_water")
	private String coldWater;
	
	@Column(name="iced_water")
	private String icedWater;
	
	@Column(name="rate")
	private String rate;
	
	@Column(name="address")
	private String address;
	
	@Column(name="status")
	private String status;
	
	@Column(name="image")
	private String image;
	
	@Column(name="water_point_types")
	private String waterPointTypes;
	
	@Column(name="city")
	private String city;
	
	@Column(name="district")
	private String district;

	@Column(name="update_time")
	private String updateTime;
	
	
	
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
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

	@Override
	public String toString() {
		return "WaterPoints [uuid=" + uuid + ", waterPointName=" + waterPointName + ", location=" + location
				+ ", facility=" + facility + ", facilityBrand=" + facilityBrand + ", floor=" + floor + ", openingHours="
				+ openingHours + ", source=" + source + ", description=" + description + ", hotWater=" + hotWater
				+ ", warmWater=" + warmWater + ", coldWater=" + coldWater + ", icedWater=" + icedWater + ", rate="
				+ rate + ", address=" + address + ", status=" + status + ", image=" + image + ", waterPointTypes="
				+ waterPointTypes + ", city=" + city + ", district=" + district + ", updateTime=" + updateTime + "]";
	}

	
}
