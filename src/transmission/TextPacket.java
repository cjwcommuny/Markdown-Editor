package transmission;

import java.io.Serializable;

public class TextPacket implements Serializable {
    private String text;

    public TextPacket(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

