package cn.org.tpeach.nosql.exception;

public class ServiceException extends  RuntimeException{
	private static final long serialVersionUID = 217600907405125650L;

	public ServiceException(String message) {
        super(message);
    }
}
