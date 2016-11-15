package tw.com.util;

import java.io.Serializable;

public class FrensworkzException extends Exception implements Serializable
{
    private static final long serialVersionUID = 1L;
    public int statusCode = 400 ;
    public FrensworkzException() {
        super();
    }
    public FrensworkzException(String msg)   {
        super(msg);
    }
    public FrensworkzException(String msg, int needStatusCode) {    	
    	super(msg);
    	statusCode = needStatusCode ;
    }
    public FrensworkzException(String msg, Exception e)  {
        super(msg, e);
    }
}