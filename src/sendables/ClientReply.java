package sendables;

import java.io.Serializable;

public class ClientReply implements Serializable {
    private int optionNumber;

    public ClientReply(int optionNumber) {
        this.optionNumber = optionNumber;
    }

    public int getOptionNumber() {
        return optionNumber;
    }
}
