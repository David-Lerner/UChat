package uchat.model;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Message implements Serializable
{
    private String text;
    private String name;
    private String id;
    private BufferedImage picture;
    private BufferedImage image;
    private messageType type;
    
    public enum messageType 
    {
        TEXT,
        ALERT,
        ERROR,
        CONNECT,
        EXIT
    }

    public Message(String name, String text, String id, BufferedImage picture, BufferedImage image) 
    {
        this.name = name;
        this.text = text;
        this.id = id;
        this.picture = picture;
        this.image = image;
        type = messageType.TEXT;
    }

    public Message(String text, messageType type)
    {
        name = "UChat";
        this.text = text;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    
    public BufferedImage getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public messageType getType() {
        return type;
    }

    public BufferedImage getPicture() {
        return picture;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
}
