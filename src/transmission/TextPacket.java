package transmission;

import java.io.Serializable;

public class TextPacket implements Serializable {
    private PacketType packetType;
    private String text;
    private int id;

    public TextPacket(PacketType packetType, String text) {
        this.packetType = packetType;
        this.text = text;
    }

    public TextPacket() {
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextPacket(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static enum PacketType {
        //join a cooperation editing
        JOIN,
        //establish a cooperation editing
        ESTABLISH,
        //normal text packet
        TEXT,
        //close connection
        CLOSE,
        //reply
        REPLY
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public int getId() {
        return id;
    }
}

