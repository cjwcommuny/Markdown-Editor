package client.exception;

public class WrongConnectionParameterFormatException extends Exception {
    public WrongConnectionParameterFormatException(String ip, String port) {
        super("Wrong ip or port, ip is " + ip + ", port is " + port);
    }
}
