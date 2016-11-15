package tw.com.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.spatial.SpatialFunction;
import org.hibernate.spatial.criterion.SpatialFilter;
import org.hibernate.spatial.criterion.SpatialRestrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;


import tw.com.entity.WaterPoints;
import tw.com.model.BooleanModel;
import tw.com.model.WaterPointsBasicOutputModel;
import tw.com.model.WaterPointsDetailInputModel;
import tw.com.model.WaterPointsDetailOutputModel;
import tw.com.util.DbUtil;
import tw.com.util.FrensworkzErrorMsg;
import tw.com.util.FrensworkzException;

@Path("/waterPoints")
@Api(value = "/waterPoints", description = "Water Points APIs")
public class WaterPointsController
{
	static 
	{
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
	}
	
	@Context
	ServletContext ctx;
	@Context
	private HttpServletResponse response;
	
	
	
	/**
	 * 查詢所有水點詳細資訊
	 * @author LawrenceChuang 
	 * @throws IOException 
	 * http://drinkingwatermap-watermap.rhcloud.com/WaterMap/api/v1/waterPoints/getAllDetailWaterPoints
	 */
	@GET
	@Path("/getAllDetailWaterPoints")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查詢所有水點詳細資訊", notes = "查詢所有水點詳細資訊", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public List<WaterPointsDetailOutputModel> getAllDetailWaterPoints() 
	{
		//String path = "C:\\Users\\lawrencechuang\\Desktop\\export.geojson";
		
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		List<WaterPointsDetailOutputModel> pointModel = new ArrayList<WaterPointsDetailOutputModel>();
		
		try 
		{
			Criteria criteria = session.createCriteria(WaterPoints.class);
			List<WaterPoints> point = criteria.list();
			
			Gson gson = new Gson();
			
			for(WaterPoints wp : point)
			{
				WaterPointsDetailOutputModel wpom = new WaterPointsDetailOutputModel();
				wpom.setUuid(wp.getUuid());
				wpom.setWaterPointName(wp.getWaterPointName());
				wpom.setAddress(wp.getAddress());
				wpom.setCity(wp.getCity());
				wpom.setDescription(wp.getDescription());
				wpom.setDistrict(wp.getDistrict());
				wpom.setColdWater(wp.getColdWater());
				wpom.setHotWater(wp.getHotWater());
				wpom.setWarmWater(wp.getWarmWater());
				wpom.setIcedWater(wp.getIcedWater());
				wpom.setFacility(wp.getFacility());
				wpom.setFacilityBrand(wp.getFacilityBrand());
				wpom.setOpeningHours(wp.getOpeningHours());
				wpom.setFloor(wp.getFloor());
				wpom.setImage(wp.getImage());
				wpom.setRate(wp.getRate());
				wpom.setSource(wp.getSource());
				wpom.setStatus(wp.getStatus());
				wpom.setWaterPointTypes(wp.getWaterPointTypes());
				
				//顯示水點座標經緯度
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("longitude", gson.toJson(wp.getLocation().getX()));
				map.put("latitude", gson.toJson(wp.getLocation().getY()));
				wpom.setLocation(map);
				
				pointModel.add(wpom);
			}
			
			//osmDataToMysqlDB();
			
			tx.commit();
		}
		catch (java.lang.Exception e) 
		{
			tx.rollback();
		} 
		finally 
		{
			session.close();
		}

		return pointModel;
	}
	//end of getAllDetailWaterPoints()
	
	

	/**
	 * 新增水點資訊
	 * @author LawrenceChuang 
	 * @throws JsonProcessingException 
	 */
	@POST
	@Path("/addWaterPoint")
	@ApiOperation(value = "新增水點資訊", notes = "新增水點資訊", response = WaterPointsDetailOutputModel.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "InteralError"),
			@ApiResponse(code = 400, message = "Error message", response = FrensworkzErrorMsg.class)
	})
	public WaterPointsDetailOutputModel addWaterPoint(
			@ApiParam(required = true, value = "water points input model") WaterPointsDetailInputModel body
			) throws JsonProcessingException
	{
		Session session = DbUtil.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		WaterPoints wp = new WaterPoints();
		
		String uuid =UUID.randomUUID().toString();
		
		//取得台灣時區
		DateTimeZone gmt = DateTimeZone.forID("Asia/Taipei");
		DateTime datetime = new DateTime(gmt);
					
		WaterPointsDetailOutputModel wpom = new WaterPointsDetailOutputModel();
		
		try
		{
			//寫入水點資訊
			wp.setAddress(body.getAddress());
			wp.setCity(body.getCity());
			wp.setColdWater(body.getColdWater());
			wp.setDescription(body.getDescription());
			wp.setDistrict(body.getDistrict());
			wp.setFacility(body.getFacility());
			wp.setFacilityBrand(body.getFacilityBrand());
			wp.setFloor(body.getFloor());
			wp.setHotWater(body.getHotWater());
			wp.setIcedWater(body.getIcedWater());
			wp.setImage(body.getImage());
			wp.setLocation(stringToPoint(body.getLocation()));	
			wp.setOpeningHours(body.getOpeningHours());
			wp.setRate(body.getRate());
			wp.setSource(body.getSource());
			wp.setStatus("0");
			wp.setUpdateTime(datetime.toLocalDateTime().toString("yyyy/MM/dd"));
			wp.setUuid(uuid);
			wp.setWarmWater(body.getWarmWater());
			wp.setWaterPointName(body.getWaterPointName());
			wp.setWaterPointTypes(body.getWaterPointTypes());
			
			session.saveOrUpdate(wp);
			
			wpom = display(wp);
	
			tx.commit();
	
		}
		catch(java.lang.Exception e)
		{
			tx.rollback();
		}
			
		return wpom;
	}
	//end of addWaterPoint()
	
	
	
	/**
	 * 刪除單筆水點資訊 by id
	 * @author LawrenceChuang 
	 * @throws FrensworkzException 
	 */
	@DELETE
	@Path("/deleteWaterPointById/{id}")
	@ApiOperation(value = "刪除單筆水點資訊 by id", notes = "刪除單筆水點資訊 by id", response = WaterPoints.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public BooleanModel delete(
			@ApiParam(required = true, value = "水點 id")@PathParam("id") String id
			) throws FrensworkzException
	{
		Session session = DbUtil.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		try
		{
			WaterPoints wp = (WaterPoints) session.get(WaterPoints.class, id);
			
			if(wp == null)
			{
				throw new FrensworkzException("資料不存在 ");
			}
			
			session.delete(wp);
			
			tx.commit();	
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		
		
		return new BooleanModel(true);
	}
	//end of delete()
	
	
	
	/**
	 * 查詢單筆水點詳細資訊 by id
	 * @author LawrenceChuang 
	 * @throws FrensworkzException
	 *  http://drinkingwatermap-watermap.rhcloud.com/WaterMap/api/v1/waterPoints/getDetailWaterPointById
	 */
	@GET
	@Path("/getDetailWaterPointById")
	@ApiOperation(value = "查詢單筆水點詳細資訊 by id", notes = "查詢單筆水點詳細資訊 by id", response = WaterPointsDetailOutputModel.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public WaterPointsDetailOutputModel getWaterPointById(
			@ApiParam(required = true, value = "水點 id")@QueryParam("id")String id
			) throws FrensworkzException
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		WaterPointsDetailOutputModel wpom = new WaterPointsDetailOutputModel();

		try
		{
			WaterPoints wp = (WaterPoints)session.get(WaterPoints.class, id);
		
			if(wp == null)
				throw new FrensworkzException("can't find this water point!");
			
			Gson gson = new Gson();
			
			wpom.setUuid(wp.getUuid());
			wpom.setWaterPointName(wp.getWaterPointName());
			wpom.setAddress(wp.getAddress());
			wpom.setCity(wp.getCity());
			wpom.setDescription(wp.getDescription());
			wpom.setDistrict(wp.getDistrict());
			wpom.setColdWater(wp.getColdWater());
			wpom.setHotWater(wp.getHotWater());
			wpom.setWarmWater(wp.getWarmWater());
			wpom.setIcedWater(wp.getIcedWater());
			wpom.setFacility(wp.getFacility());
			wpom.setFacilityBrand(wp.getFacilityBrand());
			wpom.setOpeningHours(wp.getOpeningHours());
			wpom.setFloor(wp.getFloor());
			wpom.setImage(wp.getImage());
			wpom.setRate(wp.getRate());
			wpom.setSource(wp.getSource());
			wpom.setStatus(wp.getStatus());
			wpom.setWaterPointTypes(wp.getWaterPointTypes());
			
			//顯示水點座標經緯度
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("longitude", gson.toJson(wp.getLocation().getX()));
			map.put("latitude", gson.toJson(wp.getLocation().getY()));
			wpom.setLocation(map);
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		finally
		{
			session.close();
		}
		
		return wpom;
	}
	//end of getDetailWaterPointById()
	
	
	
	/**
	 * 修改單筆水點資訊 by id
	 * @author LawrenceChuang 
	 * @throws FrensworkzException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@PUT
	@Path("/updateWaterPointById/{id}")
	@ApiOperation(value = "修改單筆水點資訊 by id",
				  notes = "修改單筆水點資訊 by id", 
				  response = BooleanModel.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public BooleanModel updateWaterPointById(
			@ApiParam(required = true, value = "水點 id")@PathParam("id")String id,
			@ApiParam(required = true, value = "Water Point Model") WaterPointsDetailInputModel body
			) throws FrensworkzException, JsonParseException, JsonMappingException, IOException
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
	
		DateTime dt = new DateTime();
		String dateFormat = "yyyy/MM/dd hh:mm";	 //寫入DB的時間格式
		
		try
		{
			WaterPoints wp = (WaterPoints)session.get(WaterPoints.class, id);
			
			wp.setAddress(body.getAddress());
			wp.setCity(body.getCity());
			wp.setColdWater(body.getColdWater());
			wp.setDescription(body.getDescription());
			wp.setDistrict(body.getDistrict());
			wp.setFacility(body.getFacility());
			wp.setFacilityBrand(body.getFacilityBrand());
			wp.setFloor(body.getFloor());
			wp.setHotWater(body.getHotWater());
			wp.setIcedWater(body.getIcedWater());
			wp.setImage(body.getImage());
			wp.setLocation(stringToPoint(body.getLocation().toString()));	
			wp.setOpeningHours(body.getOpeningHours());
			wp.setRate(body.getRate());
			wp.setSource(body.getSource());
			wp.setWarmWater(body.getWarmWater());
			wp.setWaterPointName(body.getWaterPointName());
			wp.setWaterPointTypes(body.getWaterPointTypes());
			wp.setUpdateTime(dt.toString(dateFormat));
			session.saveOrUpdate(wp);
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		finally
		{
			session.close();
		}
		
		
		return 	new BooleanModel(true);
	}
	//end of updateWaterPointById()
	
	
	
	/**
	 * 判定單筆水點資訊通過審核 by id
	 * @author LawrenceChuang 
	 * @throws FrensworkzException 
	 */
	@PUT
	@Path("/verifyWaterPointById/{id}")
	@ApiOperation(value = "判定單筆水點資訊通過審核 by id",
				  notes = "判定單筆水點資訊通過審核 by id", 
				  response = BooleanModel.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public BooleanModel verifyWaterPointById(
			@ApiParam(required = true, value = "水點 id")@PathParam("id")String id
			) throws FrensworkzException
	{
		Session session = DbUtil.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		try
		{
			//抓取該筆id的水點資訊
			WaterPoints wp = (WaterPoints) session.get(WaterPoints.class, id);
			
			if(wp != null)
			{
				//寫入1代表水點資訊正確
				wp.setStatus("1");
				session.saveOrUpdate(wp);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		
		return new BooleanModel(true);
	}
	//end of verifyWaterPointById()
	
	
	
	/**
	 * 查詢所有水點基本資訊(for google map marker)
	 * @author LawrenceChuang 
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws Exception 
	 * http://drinkingwatermap-watermap.rhcloud.com/WaterMap/api/v1/waterPoints/getAllBasicWaterPoints
	 */
	@GET
	@Path("/getAllBasicWaterPoints")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查詢所有水點基本資訊(for google map marker)", notes = "查詢所有水點基本資訊(for google map marker)", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public List<WaterPointsBasicOutputModel> getAllBasicWaterPoints() throws IOException 
	{
		//String path = "C:\\Users\\lawrencechuang\\Desktop\\export.geojson";
		
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		List<WaterPointsBasicOutputModel> pointModel = new ArrayList<WaterPointsBasicOutputModel>();
		
		try
		{
			Criteria criteria = session.createCriteria(WaterPoints.class);
			
			List<WaterPoints> point = criteria.list();
		
			Gson gson = new Gson();
			
			for(WaterPoints wp : point)
			{
				WaterPointsBasicOutputModel wpom = new WaterPointsBasicOutputModel();
				
				wpom.setUuid(wp.getUuid());
				wpom.setWaterPointName(wp.getWaterPointName());
				wpom.setDescription(wp.getDescription());
				wpom.setColdWater(wp.getColdWater());
				wpom.setHotWater(wp.getHotWater());
				wpom.setWarmWater(wp.getWarmWater());
				wpom.setIcedWater(wp.getIcedWater());
				wpom.setWaterPointTypes(wp.getWaterPointTypes());
			
				//顯示水點座標經緯度
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("longitude", gson.toJson(wp.getLocation().getX()));
				map.put("latitude", gson.toJson(wp.getLocation().getY()));
				wpom.setLocation(map);
				
				pointModel.add(wpom);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		finally
		{
			session.close();
		}
		
		//osmDataToMysqlDB();
	
		return pointModel;
	}
	//end of getAllBasicWaterPoints()
	
	
	
	/**
	 * 查詢單筆水點基本資訊(for google map marker)
	 * @author LawrenceChuang 
	 * @throws FrensworkzException 
	 * http://drinkingwatermap-watermap.rhcloud.com/WaterMap/api/v1/waterPoints/getBasicWaterPoints
	 */
	@GET
	@Path("/getBasicWaterPointsById")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查詢單筆水點基本資訊 by id (for google map marker)", notes = "查詢單筆水點基本資訊 by id (for google map marker)", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public WaterPointsBasicOutputModel getBasicWaterPointById(
			@ApiParam(required = true, value = "水點 id")@QueryParam("id")String id
			) throws FrensworkzException
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		WaterPointsBasicOutputModel wpom = new WaterPointsBasicOutputModel();

		try
		{
			WaterPoints wp = (WaterPoints) session.get(WaterPoints.class, id);
			
			if(wp == null)
				throw new FrensworkzException("can't find this water point!");
			
			WaterPointsBasicOutputModel results = new WaterPointsBasicOutputModel();
		
			Gson gson = new Gson();
			
			wpom.setUuid(wp.getUuid());
			wpom.setWaterPointName(wp.getWaterPointName());
			wpom.setDescription(wp.getDescription());
			wpom.setColdWater(wp.getColdWater());
			wpom.setHotWater(wp.getHotWater());
			wpom.setWarmWater(wp.getWarmWater());
			wpom.setIcedWater(wp.getIcedWater());
			wpom.setWaterPointTypes(wp.getWaterPointTypes());
			
			//顯示水點座標經緯度
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("longitude", gson.toJson(wp.getLocation().getX()));
			map.put("latitude", gson.toJson(wp.getLocation().getY()));
			wpom.setLocation(map);
			
				
			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		finally
		{
			session.close();
		}
		
		return wpom;
	}
	//end of getBasicWaterPointById()
	
	
	/**
	 * 查詢目前所在座標某個距離內的水點資訊
	 * @author LawrenceChuang 
	 * @throws FrensworkzException 
	 * @parametors Double latitude, Double longitude
	 * URL:
	 */
	@GET
	@Path("/getWaterPointsNearBy")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查詢目前所在座標某個距離內的水點資訊", notes = "查詢目前所在座標某個距離內的水點資訊", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public List<WaterPointsDetailOutputModel> getWaterPointsNearBy(
			@ApiParam(required = true, value = "latitude")@QueryParam("latitude")Double latitude,
			@ApiParam(required = true, value = "longitude")@QueryParam("longitude")Double longitude,
			@ApiParam(required = true, value = "area")@QueryParam("area")Double area
			) 
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		Double lat = latitude;
		Double lng = longitude;
		Double earthRadius = 6371.0;
		Double pi = Math.PI;
		Double distance = area; //查找多少距離範圍(單位:公里)
		
		Double range = 180 / pi * area / earthRadius;
		Double lngR = range / Math.cos(lat * pi / 180.0);
		
		//找出範圍內最大的四個角座標
		Double maxLat = lat + range;
		Double minLat = lat - range;
		Double maxLng = lng + lngR;
		Double minLng = lng - lngR;
		
		String point1 = "(" + maxLat + "," + minLng + ")";
		String point2 = "(" + maxLat + "," + maxLng + ")";
		String point3 = "(" + minLat + "," + maxLng + ")";
		String point4 = "(" + minLat + "," + minLng + ")";
		
		Point p1 = stringToPoint(point1); 
		Point p2 = stringToPoint(point2);
		Point p3 = stringToPoint(point3);
		Point p4 = stringToPoint(point4);
		
		List<WaterPoints> nearByList = new ArrayList<WaterPoints>();
		List<WaterPointsDetailOutputModel> resultList = new ArrayList<WaterPointsDetailOutputModel>();
	
		String hql = "FROM WaterPoints where ((X(location) between " + minLat + " and " + maxLat + ") "
				+ "and (Y(location) between " + minLng + " and " + maxLng + "))";
		System.out.println(hql);
		Query query = session.createQuery(hql);
		
		nearByList = query.list();
		Gson gson = new Gson();
		
		for(WaterPoints wp : nearByList) {
			WaterPointsDetailOutputModel wpom = new WaterPointsDetailOutputModel();
			wpom.setUuid(wp.getUuid());
			wpom.setWaterPointName(wp.getWaterPointName());
			wpom.setAddress(wp.getAddress());
			wpom.setCity(wp.getCity());
			wpom.setDescription(wp.getDescription());
			wpom.setDistrict(wp.getDistrict());
			wpom.setColdWater(wp.getColdWater());
			wpom.setHotWater(wp.getHotWater());
			wpom.setWarmWater(wp.getWarmWater());
			wpom.setIcedWater(wp.getIcedWater());
			wpom.setFacility(wp.getFacility());
			wpom.setFacilityBrand(wp.getFacilityBrand());
			wpom.setOpeningHours(wp.getOpeningHours());
			wpom.setFloor(wp.getFloor());
			wpom.setImage(wp.getImage());
			wpom.setRate(wp.getRate());
			wpom.setSource(wp.getSource());
			wpom.setStatus(wp.getStatus());
			wpom.setWaterPointTypes(wp.getWaterPointTypes());
			
			//顯示經緯度座標
			Map<String, Object> map = new HashMap<String, Object>();
			//經緯度 (X, Y) => (latitude, longitude) 
			map.put("latitude", wp.getLocation().getX());
			map.put("longitude", wp.getLocation().getY());
			wpom.setLocation(map);
			
			//計算兩點之間的距離
			Double lngEnd = (Double) wpom.getLocation().get("longitude");
			Double latEnd = (Double) wpom.getLocation().get("latitude");
			Double dis = getDistance(lat, lng, latEnd, lngEnd);
			wpom.setDistance(dis);
			
			//只顯示兩點距離不超過某設定範圍公里的水點資訊
			if(wpom.getDistance() <= distance * 1000) {
				resultList.add(wpom);
			}
		}
		
		//對水點與使用者目前位置進行距離排序(由距離最近到距離最遠)
		Collections.sort(resultList, new DistanceComparator());
		
		session.close();
		return resultList;
	}
	
	
	/**
	 * 解析OSM Json object 
	 * @author LawrenceChuang 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * http://drinkingwatermap-watermap.rhcloud.com/WaterMap/api/v1/waterPoints/getAllDetailWaterPoints
	 */
	@GET
	@Path("/parseWaterPointsForOSM")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "解析OSM Json object ", notes = "解析OSM Json object ", response = BooleanModel.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public Boolean parseWaterPointsForOSM() throws FrensworkzException, FileNotFoundException
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		String path = "C:\\Users\\Lawrence\\Desktop\\export.geojson";
		File file =  new File(path);
		Scanner sc = null;
		sc = new Scanner(file, "utf-8");
		StringBuilder buffer = new StringBuilder();
	
		while(sc.hasNextLine())
		{
			buffer.append(sc.nextLine());
		}

		JSONObject obj = new JSONObject(buffer.toString());
		JSONArray results = obj.getJSONArray("features");
		System.out.println("size: " + results.length());
		
		for(int i=0; i<results.length(); i++)
		{
			JSONObject json = results.getJSONObject(i);
			String properties = null;
			WaterPoints point = new WaterPoints();
			
			//解析osmAPI資料，並轉到mysql相對應欄位，如果有name這個屬性才開始寫入DB
			if(!(json.getJSONObject("properties").isNull("name")))
			{
				
				//建立水點id
				String uuid = UUID.randomUUID().toString(); 
				point.setUuid(uuid);
				
				//取得水點名稱
				String name = json.getJSONObject("properties").getString("name");
				point.setWaterPointName(name);
				System.out.println("name:" + name);
				
				//取得樓層資訊
				if(!(json.getJSONObject("properties").isNull("level") && !(json.getJSONObject("properties").isNull("name"))))
				{
					String floor = json.getJSONObject("properties").get("level").toString();
					point.setFloor(floor);
					System.out.println("floor:" + floor);
				}
				
				//取得營業時間
				if(!(json.getJSONObject("properties").isNull("opening_hours")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String openTime = json.getJSONObject("properties").get("opening_hours").toString();
					point.setOpeningHours(openTime);
					System.out.println("openTime:" + openTime);
				}
				
				//取得飲水機品牌
				if(!(json.getJSONObject("properties").isNull("brand")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String facilityBrand = json.getJSONObject("properties").get("brand").toString();
					point.setFacilityBrand(facilityBrand);
					System.out.println("facilityBrand:" + facilityBrand);
				}
				
				//取得水點描述
				if(!(json.getJSONObject("properties").isNull("description")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String description = json.getJSONObject("properties").get("description").toString();
					point.setDescription(description);
					System.out.println("description:" + description);
				}
				
				//取得熱水資訊
				if(!(json.getJSONObject("properties").isNull("hot_water")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String hotWater = json.getJSONObject("properties").get("hot_water").toString();
					point.setHotWater("1");
					System.out.println("hotWater:" + 1);
				}
				else
				{
					point.setHotWater("0");
					System.out.println("hotWater:" + 0);
				}
				
				//取得溫水資訊
				if(!(json.getJSONObject("properties").isNull("warm_water")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String warmWater = json.getJSONObject("properties").get("warm_water").toString();
					point.setWarmWater("1");
					System.out.println("warmWater:" + 1);
				}
				else
				{
					point.setWarmWater("0");
					System.out.println("warmWater:" + 0);
				}
				
				//取得冷水資訊
				if(!(json.getJSONObject("properties").isNull("cold_water")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String coldWater = json.getJSONObject("properties").get("cold_water").toString();
					point.setColdWater("1");
					System.out.println("coldWater:" + 1);
				}
				else
				{
					point.setColdWater("0");
					System.out.println("coldWater:" + 0);
				}
				
				//取得冰水資訊
				if(!(json.getJSONObject("properties").isNull("iced_water")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String icedWater = json.getJSONObject("properties").get("iced_water").toString();
					point.setIcedWater("1");
					System.out.println("icedWater:" + 1);
				}
				else
				{
					point.setIcedWater("0");
					System.out.println("icedWater:" + 0);
				}
				
				//設定尚未審核狀態
				point.setStatus("0");
				
				//取得資料來源
				if(!(json.getJSONObject("properties").isNull("source")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String source = json.getJSONObject("properties").get("source").toString();
					point.setHotWater(source);
				}
				
				//取得經緯度座標
				if(!(json.getJSONObject("geometry").isNull("coordinates")) && !(json.getJSONObject("properties").isNull("name")))
				{
					String location = null;
					String pointY = null;
					String pointX = null;
					location = json.getJSONObject("geometry").get("coordinates").toString();
					pointY = location.substring(1, location.indexOf(","));
					pointX = location.substring(location.indexOf(",") + 1, location.indexOf("]"));
					
					//建立座標型態
					GeometryFactory gf = new GeometryFactory();
					Coordinate coordinate = new Coordinate(Double.parseDouble(pointX), Double.parseDouble(pointY));
					Point pt = gf.createPoint(coordinate);
					
					point.setLocation(pt);
					System.out.println("point:" + pt.toString());
				}
				session.save(point);
				System.out.println("i:" + i);
			}
			
			if(i % 20 == 0) {
				session.flush();
				session.clear();
			}
		}
		
		tx.commit();
		session.close();
				
		return true;
	}
	
	
	
/**------------------------------------------------------------------------------------------------------------------------------------*/
	
	static class DistanceComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			WaterPointsDetailOutputModel p1 = (WaterPointsDetailOutputModel) o1; 
			WaterPointsDetailOutputModel p2 = (WaterPointsDetailOutputModel) o2;
			
			return new Double(p1.getDistance()).compareTo(new Double(p2.getDistance()));
		}
	}
	
	/**
	 * 字串轉座標
	 * @throws IOException 
	 */
	private Point stringToPoint(String point)
	{
		String pointY = null;
		String pointX = null;
		pointX = point.substring(1, point.indexOf(","));
		pointY = point.substring(point.indexOf(",") + 1, point.indexOf(")"));
		
		System.out.println("x:" + pointX);
		System.out.println("y:" + pointY);
		
		//建立座標型態
		GeometryFactory gf = new GeometryFactory();
		Coordinate coordinate = new Coordinate(Double.parseDouble(pointX), Double.parseDouble(pointY));
		Point pt = gf.createPoint(coordinate);
		System.out.println("pt:" + pt);
		
		return pt;	
	}
	
	
	/**
	 * 新增水點顯示結果
	 * @throws IOException 
	 */
	private WaterPointsDetailOutputModel display(WaterPoints wp)
	{
		WaterPointsDetailOutputModel wpom = new WaterPointsDetailOutputModel();
		wpom.setAddress(wp.getAddress());
		wpom.setCity(wp.getCity());
		wpom.setColdWater(wp.getColdWater());
		wpom.setDescription(wp.getDescription());
		wpom.setDistrict(wp.getDistrict());
		wpom.setFacility(wp.getFacility());
		wpom.setFacilityBrand(wp.getFacilityBrand());
		wpom.setFloor(wp.getFloor());
		wpom.setHotWater(wp.getHotWater());
		wpom.setIcedWater(wp.getIcedWater());
		wpom.setImage(wp.getImage());	
		wpom.setOpeningHours(wp.getOpeningHours());
		wpom.setRate(wp.getRate());
		wpom.setSource(wp.getSource());
		wpom.setStatus(wp.getStatus());
		wpom.setUuid(wp.getUuid());
		wpom.setWarmWater(wp.getWarmWater());
		wpom.setWaterPointName(wp.getWaterPointName());
		wpom.setWaterPointTypes(wp.getWaterPointTypes());
	
		//顯示水點座標經緯度
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("longitude", gson.toJson(wp.getLocation().getX()));
		map.put("latitude", gson.toJson(wp.getLocation().getY()));
		wpom.setLocation(map);
		
		return wpom;	
	}
	
	
	/**
	 * 新增基本水點顯示結果
	 * @throws IOException 
	 */
	private WaterPointsBasicOutputModel displayBasic(WaterPoints wp)
	{
		WaterPointsBasicOutputModel wpom = new WaterPointsBasicOutputModel();
		wpom.setColdWater(wp.getColdWater());
		wpom.setDescription(wp.getDescription());
		wpom.setHotWater(wp.getHotWater());
		wpom.setIcedWater(wp.getIcedWater());
		wpom.setUuid(wp.getUuid());
		wpom.setWarmWater(wp.getWarmWater());
		wpom.setWaterPointName(wp.getWaterPointName());
		wpom.setWaterPointTypes(wp.getWaterPointTypes());
	
		//顯示水點座標經緯度
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("longitude", gson.toJson(wp.getLocation().getX()));
		map.put("latitude", gson.toJson(wp.getLocation().getY()));
		wpom.setLocation(map);
		
		return wpom;	
	}
	
	
	/**
	 * 讀取Json文件
	 * @throws IOException 
	 */
	private void osmDataToMysqlDB() throws IOException
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		
		String path = "C:\\Users\\Lawrence\\Desktop\\export.geojson";
		File file =  new File(path);
		Scanner sc = null;
		sc = new Scanner(file, "utf-8");
		StringBuilder buffer = new StringBuilder();
		
		try
		{
			while(sc.hasNextLine())
			{
				buffer.append(sc.nextLine());
			}
	
			JSONObject obj = new JSONObject(buffer.toString());
			JSONArray results = obj.getJSONArray("features");
			System.out.println("size: " + results.length());
			
			for(int i=0; i<results.length(); i++)
			{
				JSONObject json = results.getJSONObject(i);
				String properties = null;
				WaterPoints point = new WaterPoints();
				
				//解析osmAPI資料，並轉到mysql相對應欄位，如果有name這個屬性才開始寫入DB
				if(!(json.getJSONObject("properties").isNull("name")))
				{
					
					//建立水點id
					String uuid = UUID.randomUUID().toString(); 
					point.setUuid(uuid);
					
					//取得水點名稱
					String name = json.getJSONObject("properties").getString("name");
					point.setWaterPointName(name);
					System.out.println("name:" + name);
					
					//取得樓層資訊
					if(!(json.getJSONObject("properties").isNull("level") && !(json.getJSONObject("properties").isNull("name"))))
					{
						String floor = json.getJSONObject("properties").get("level").toString();
						point.setFloor(floor);
						System.out.println("floor:" + floor);
					}
					
					//取得營業時間
					if(!(json.getJSONObject("properties").isNull("opening_hours")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String openTime = json.getJSONObject("properties").get("opening_hours").toString();
						point.setOpeningHours(openTime);
						System.out.println("openTime:" + openTime);
					}
					
					//取得飲水機品牌
					if(!(json.getJSONObject("properties").isNull("brand")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String facilityBrand = json.getJSONObject("properties").get("brand").toString();
						point.setFacilityBrand(facilityBrand);
						System.out.println("facilityBrand:" + facilityBrand);
					}
					
					//取得水點描述
					if(!(json.getJSONObject("properties").isNull("description")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String description = json.getJSONObject("properties").get("description").toString();
						point.setDescription(description);
						System.out.println("description:" + description);
					}
					
					//取得熱水資訊
					if(!(json.getJSONObject("properties").isNull("hot_water")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String hotWater = json.getJSONObject("properties").get("hot_water").toString();
						point.setHotWater("1");
						System.out.println("hotWater:" + 1);
					}
					else
					{
						point.setHotWater("0");
						System.out.println("hotWater:" + 0);
					}
					
					//取得溫水資訊
					if(!(json.getJSONObject("properties").isNull("warm_water")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String warmWater = json.getJSONObject("properties").get("warm_water").toString();
						point.setWarmWater("1");
						System.out.println("warmWater:" + 1);
					}
					else
					{
						point.setWarmWater("0");
						System.out.println("warmWater:" + 0);
					}
					
					//取得冷水資訊
					if(!(json.getJSONObject("properties").isNull("cold_water")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String coldWater = json.getJSONObject("properties").get("cold_water").toString();
						point.setColdWater("1");
						System.out.println("coldWater:" + 1);
					}
					else
					{
						point.setColdWater("0");
						System.out.println("coldWater:" + 0);
					}
					
					//取得冰水資訊
					if(!(json.getJSONObject("properties").isNull("iced_water")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String icedWater = json.getJSONObject("properties").get("iced_water").toString();
						point.setIcedWater("1");
						System.out.println("icedWater:" + 1);
					}
					else
					{
						point.setIcedWater("0");
						System.out.println("icedWater:" + 0);
					}
					
					//設定尚未審核狀態
					point.setStatus("0");
					
					//取得資料來源
					if(!(json.getJSONObject("properties").isNull("source")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String source = json.getJSONObject("properties").get("source").toString();
						point.setHotWater(source);
					}
					
					//取得經緯度座標
					if(!(json.getJSONObject("geometry").isNull("coordinates")) && !(json.getJSONObject("properties").isNull("name")))
					{
						String location = null;
						String pointY = null;
						String pointX = null;
						location = json.getJSONObject("geometry").get("coordinates").toString();
						pointY = location.substring(1, location.indexOf(","));
						pointX = location.substring(location.indexOf(",") + 1, location.indexOf("]"));
						
						//建立座標型態
						GeometryFactory gf = new GeometryFactory();
						Coordinate coordinate = new Coordinate(Double.parseDouble(pointX), Double.parseDouble(pointY));
						Point pt = gf.createPoint(coordinate);
						
						point.setLocation(pt);
						System.out.println("point:" + pt.toString());
					}
					
					session.saveOrUpdate(point);
					System.out.println("i:" + i);
				}
				
				if(i % 20 == 0) {
					session.flush();
					session.clear();
				}
			}
			
				tx.commit();
				session.close();
			}
			catch (Exception e) {
				if (tx!=null && tx.isActive()) {
					 tx.rollback();
				}
			}	
		}	
	

	/**
	 * 計算兩經緯度座標之間的直線距離
	 * @throws  
	 * parameters: Double latStart, Double lngStart, Double latEnd, Double lngEnd
	 */
	public Double getDistance(Double latStart, Double lngStart, Double latEnd, Double lngEnd) {
		//location1
		Double latLocation1 = latStart;
		Double lngLocation1 = lngStart;
		//location2
		Double latLocation2 = latEnd;
		Double lngLocation2 = lngEnd;
		
		Double pi = Math.PI;
		
		Double radLat1 = latLocation1 * pi / 180;
		Double radLat2 = latLocation2 * pi / 180;
		
		Double l = radLat1 - radLat2;
		Double p = lngLocation1 * pi / 180 - lngLocation2 * pi / 180;
		Double distance =  2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
                + Math.cos(latLocation1) * Math.cos(latLocation2)
                * Math.pow(Math.sin(p / 2), 2)));
		
		distance = distance * 6378137.0;
	    distance = Math.round(distance * 10000) / 10000.0;
	    //四捨五入至小數點第二位
	    distance = new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		
		return distance;
	}
	
	
	/**
	 * 計算兩經緯度座標之間的距離大小排序(由最近到最遠)
	 * @throws  
	 * parameters: Double latStart, Double lngStart, Double latEnd, Double lngEnd
	 */
	
	
}
