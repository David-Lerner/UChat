package uchat.model;

/**
 * Used to hold information about the users in a chat room
 * 
 * @author David
 */
public class User 
{
    private String name;
    private String address;
    private int id;
    private boolean banned;
    
    public User(String name, String address, int id) 
    {
        this.name = name;
        this.address = address;
        this.id = id;
        banned = false;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public boolean isBanned() {
        return banned;
    }

    protected void setBanned(boolean banned) {
        this.banned = banned;
    }
    
}
