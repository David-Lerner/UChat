package uchat.model;

import java.net.*;
import java.io.*;

public class ServerThread extends Thread
{  
    private Server server = null;
    private Socket socket = null;
    private int ID = -1;
    /*private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;*/
    private ObjectInputStream streamIn = null;
    private ObjectOutputStream streamOut = null;

    public ServerThread(Server serverIn, Socket socketIn)
    {  
        super();
        server = serverIn;
        socket = socketIn;
        ID = socket.getPort();
    }
    
    /*public void send(String msg)
    {   
        try
        {  
            streamOut.writeUTF(msg);
            streamOut.flush();
        }
        catch(IOException ioe)
        {  
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
    }*/
    protected void send(Message msg)
    {   
        try
        {  
            streamOut.writeObject(msg);
            streamOut.flush();
        }
        catch(IOException ioe)
        {  
            //System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.addMessage(new Message(ID + " ERROR sending: " + 
                    ioe.getMessage(), Message.messageType.ERROR));
            server.remove(ID);
            stop();
        }
    }
    
    protected int getID()
    {  
        return ID;
    }
    
    @Override
    /*public void run()
    {  
        System.out.println("Server Thread " + ID + " running.");
        while (true)
        {  
            try
            {  
                server.handle(ID, streamIn.readUTF());
            }
            catch(IOException ioe)
            {  
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }*/
    public void run()
    {  
        //System.out.println("Server Thread " + ID + " running.");
        server.addMessage(new Message("Server Thread " + ID + " running.",
                Message.messageType.ALERT));
        while (true)
        {  
            try
            {  
                server.handle(ID, (Message) streamIn.readObject());
            }
            catch(IOException ioe)
            {  
                //System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.addMessage(new Message(ID + " ERROR reading: " +
                        ioe.getMessage(), Message.messageType.ERROR));
                server.remove(ID);
                stop();
            } 
            catch (ClassNotFoundException ex) 
            {
                //System.out.println(ID + " ERROR reading: " + ex.getMessage());
                server.addMessage(new Message(ID + " ERROR reading: " +
                        ex.getMessage(), Message.messageType.ERROR));
                server.remove(ID);
                stop();
            }
        }
    }
    
    /*public void open() throws IOException
    {  
        streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
    }*/
    protected void open() throws IOException
    {  
        streamIn = new ObjectInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
        streamOut = new ObjectOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
        streamOut.flush();
    }
    
    public void close() throws IOException
    {  
        if (socket != null)    
            socket.close();
        if (streamIn != null)  
            streamIn.close();
        if (streamOut != null) 
            streamOut.close();
    }
}