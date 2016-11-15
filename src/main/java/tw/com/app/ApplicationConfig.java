package tw.com.app;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;


@ApplicationPath("api/v1")
public class ApplicationConfig extends ResourceConfig 
{

	public ApplicationConfig() 
	{
		packages("tw.com.controller");
		packages("tw.com.util");
		packages("com.wordnik.swagger.jersey.listing");
	}
}
