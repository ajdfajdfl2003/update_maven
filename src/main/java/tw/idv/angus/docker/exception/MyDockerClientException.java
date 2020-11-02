package tw.idv.angus.docker.exception;

public class MyDockerClientException extends Throwable {
    public MyDockerClientException(String message, Throwable e) {
        super(message, e);
    }

    public MyDockerClientException(String message) {
        super(message);
    }
}
