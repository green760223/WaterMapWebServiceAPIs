package tw.com.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessControlAllowOriginFilter implements Filter {


	Logger logger = LoggerFactory.getLogger(AccessControlAllowOriginFilter.class);

	public void destroy() {


	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		try {

			HttpServletRequest req = (HttpServletRequest)request;
			String clientOrigin = req.getHeader("origin");
			clientOrigin = StringUtils.isEmpty(clientOrigin) ? "*" : clientOrigin;
	
	
			HttpServletResponse hsr = (HttpServletResponse) response;
			hsr.setHeader("Access-Control-Expose-Headers", "TotalItem"); // 想要傳出去的header keyName(TotalItem)
			hsr.setHeader("Access-Control-Allow-Origin", clientOrigin);
			hsr.setHeader("Access-Control-Allow-Credentials", "true");
			hsr.setHeader("Access-Control-Allow-Methods", "POST,PUT,DELETE,GET,OPTIONS");
			hsr.setHeader("Access-Control-Allow-Headers", ((HttpServletRequest)request).getHeader("Access-Control-Request-Headers"));//"X-Requested-With, Content-Type, Accept, usertoken, socialId, email, pass, apiKey, userKey, eventId, PoiId, language, oldPass, newPass, userToken, securityCode, poiList");
			hsr.setHeader("Allow", "POST,PUT,DELETE,GET,OPTIONS");
	
 
					 
	        response.setCharacterEncoding("UTF-8") ;        
	
			filterChain.doFilter(request, response);


		} catch (Exception e ) {
			e.printStackTrace();
		}

	}

	public void init(FilterConfig filterChain) throws ServletException {
		// TODO Auto-generated method stub

	}
	
	
	
}
