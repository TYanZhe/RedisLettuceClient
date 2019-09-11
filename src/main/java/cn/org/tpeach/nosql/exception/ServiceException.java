package cn.org.tpeach.nosql.exception;

public class ServiceException extends  RuntimeException{

    public ServiceException(String message) {
        super(message);
    }
}
