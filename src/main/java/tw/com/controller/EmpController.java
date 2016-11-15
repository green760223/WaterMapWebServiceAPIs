package tw.com.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import tw.com.entity.User;
import tw.com.model.Employee;
import tw.com.util.DbUtil;

@Path("/emp")
@Api(value = "/emp", description = "xxxxxxxxxx")
public class EmpController 
{	
	/**
	 * @return
	 *
	 * http://localhost:8080/WaterMap/api/v1/emp/get
	 * 
	 */
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "dgdsgdg", notes = "dgdgd", response = Employee.class)
	public User test() {

		Employee employee = new Employee();
		employee.setAddress("xxxxxxx");
		employee.setName("ggggggggg");
		
		
		Session session = DbUtil.getCurrentSession();
		session.beginTransaction();
		
		Criteria criteria = session.createCriteria(User.class);
		List<User> users = criteria.list();

		session.close();
		return users.get(0);



	}

}
