package client.exception;

public class DialogNotShowException extends Exception {
    public DialogNotShowException() {
        super("Cannot open dialog.");
    }
}
