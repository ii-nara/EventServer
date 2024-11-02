package kr.com.ureca.exception;

public class AlreadyExistException extends Exception {

    public AlreadyExistException() {
        super();
    }

    public AlreadyExistException(String message) {
        super(message);
    }
}
