package tw.com.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;	
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import tw.com.util.FrensworkzException;
import tw.com.util.DbUtil;
import tw.com.util.FrensworkzErrorMsg;
import tw.com.entity.User;
import tw.com.model.Employee;
import tw.com.model.UserInputModel;
import tw.com.model.UserOutputModel;
import tw.com.service.CrudService;

@Path("/user")
@Api(value="/user", description="User APIs")
public class UserController
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
	 * 使用者註冊
	 * @author LawrenceChuang 
	 * @throws FrensworkzException 
	 */
	@POST
	@Path("/userRegister")
	@ApiOperation(value = "使用者註冊", notes = "使用者註冊", response = User.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public User userRegister(
			//@ApiParam(value = "[本平台使用者token(登入時取得)] 或 [第三方APP userKey]", required = false) @HeaderParam("userToken") String userToken,
			@ApiParam(required = true, value = "UserInput model") UserInputModel body
			) throws FrensworkzException
	{
		Session session = DbUtil.getOpenSession();
		Transaction tx = session.beginTransaction();
		
		User user = new User();
		
		try
		{
			String uuid = UUID.randomUUID().toString();
			
			//使用者資訊
			user.setUuid(uuid);
			user.setAccount(body.getAccount());
			user.setEmail(body.getEmail());
			user.setName(body.getName());
			
			session.saveOrUpdate(user);
			
			tx.commit();
			System.out.println("results");
		
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("Exception");
		}

		session.close();
		return user;
	}
	//end of userRegister()
	
	
	
	/**查詢所有使用者基本資料
	 * @author LawrenceChuang 
	 * @throws Exception 
	 */
	@GET
	@Path("/getAllUserList")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "查詢所有使用者基本資料", notes = "查詢所有使用者基本資料", response = User.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 400, message = "Error Message", response = FrensworkzErrorMsg.class)
	})
	public List<User> getAllUserBasicInfo() 
	{
	
		Session session = DbUtil.getCurrentSession();
		Transaction tx = session.beginTransaction();
		
		List<User> users = new ArrayList<User>();
		
		try
		{
			Criteria criteria = session.createCriteria(User.class);
			users = criteria.list();

			tx.commit();
		}
		catch(Exception e)
		{
			tx.rollback();
		}
		
		return users;
	}

}
