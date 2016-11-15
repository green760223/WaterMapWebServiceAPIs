package tw.com.util;

public class FrensworkzErrorMsg {
	private String errorCode;
	private String errorMsg;

	public FrensworkzErrorMsg(String errorMsg) {
		super();
		this.errorMsg = errorMsg;
	}
	public FrensworkzErrorMsg(String errorCode, String errorMsg) {
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}