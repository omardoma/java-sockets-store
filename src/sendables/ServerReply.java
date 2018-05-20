package sendables;

import java.io.Serializable;

public class ServerReply implements Serializable {
    private String message;
    private boolean error;

    public ServerReply(String message) {
        this.message = message;
        this.error = false;
    }

    public ServerReply(String message, boolean error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }
}
