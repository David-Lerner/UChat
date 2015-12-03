package uchat.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable
{
    private Socket socket = null;
    private Thread thread = null;
    /*private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;*/
    private ObjectInputStream streamIn = null;
    private ObjectOutputStream streamOut = null;
    private ClientThread client = null;
    private String id;
  public String name;
    private BufferedImage pic;
    private ArrayList<Message> messageBuffer;
    private Message fromUI;
    private final Object moniter = new Object();
    private boolean hasLog;

    public Client(String serverName, int serverPort, String name, 
            BufferedImage pic)
    {
        this.name = name;
        this.pic = pic;
        messageBuffer = new ArrayList<>();
        id = "";
        hasLog = false;
        //System.out.println("Establishing connection. Please wait ...");
      addMessage(new Message("Establishing connection. Please wait ...",
          Message.messageType.ALERT));
        try
        {  
            socket = new Socket(serverName, serverPort);
            id = socket.toString();
            fromUI = new Message(name, socket.getLocalAddress().toString(), Message.messageType.CONNECT);
            //System.out.println(socket.getLocalAddress().toString());
            //System.out.println("Connected: " + socket);
            addMessage(new Message("Connected: " + socket, 
                    Message.messageType.ALERT));
            start();
        }
        catch(UnknownHostException uhe)
        {  
            //System.out.println("Host unknown: " + uhe.getMessage());
            addMessage(new Message("Host unknown: " + uhe.getMessage(), 
                    Message.messageType.ERROR));
        }
        catch(IOException ioe)
        {  
            //System.out.println("Unexpected exception: " + ioe.getMessage());
            addMessage(new Message("Unexpected exception: " + ioe.getMessage(), 
                    Message.messageType.ERROR));
        }
    }
    
    protected synchronized void addMessage(Message msg)
    {
        messageBuffer.add(msg);
    }
    
    /**
     * Used to obtain all the connection/error/chat messages received by the client.
     * 
     * @return a list of Message objects
     */
    public synchronized ArrayList<Message> getMessages()
    {
        ArrayList<Message> temp = messageBuffer;
        messageBuffer = new ArrayList<>();
        return temp;
    }
    
    @Override
    /*public void run()
    {  
        System.out.println("run");
        while (thread != null)
        {  
            try
            {
                Scanner in = new Scanner(System.in);
                String temp = in.nextLine();
                streamOut.writeUTF(temp);
                //streamOut.writeUTF(streamIn.readLine());
                streamOut.flush();
                
            }
            catch(IOException ioe)
            {  
                System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }*/
    public void run()
    {
        while (thread != null)
        {
            try
            {
                /*Scanner in = new Scanner(System.in);
                String temp = in.nextLine();
                Message msg = new Message(name + ": " + temp, id, image);
                streamOut.writeObject(msg);*/
                synchronized (moniter) 
                {
                    if (hasLog)
                        moniter.wait();
                }
                Message msg = getMessageFromUI();
                if (msg != null)
                {
                    if (msg.getType() == Message.messageType.CONNECT)
                        hasLog = true;
                    streamOut.writeObject(msg);
                    streamOut.flush();
                }
                //Thread.sleep(random.nextInt(500));
            }
            catch(IOException ioe)
            {  
                //System.out.println("Sending error: " + ioe.getMessage());
                addMessage(new Message("Sending error: " + ioe.getMessage(), 
                        Message.messageType.ERROR));
                stop();
            } 
            catch (InterruptedException ie) 
            {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ie);
            }
        }
    }
    
    private synchronized Message getMessageFromUI()
    {
        Message temp = fromUI;
        fromUI = null;
        return temp;
    }
    
    /**
     * Used by the UI to send chat messages.
     * 
     * @param text The text message to be sent to the chat server
     * @param img An accompanying image (not supported -nothing will be sent)
     */
    public void sendMessage(String text, BufferedImage img)
    {
        synchronized (this) 
        {
            fromUI = new Message(name, text, id, pic, img);
            fromUI = new Message(name, text, id, null, null); //comment out this line when object sending is implemented
        }
        synchronized (moniter) 
        {
            moniter.notify();
        }
    }
    
    /*public void handle(String msg)
    {  
        if (msg.equals(".bye"))
        {  
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        }
        else
            System.out.println(msg);
    }*/
    protected void handle(Message msg)
    {
        if (msg.getType() == Message.messageType.EXIT)
        {  
            stop();
        }
        else
        {
            if (msg.getId().equals(id))
                addMessage(new Message("(You) " + msg.getName(), msg.getText(), 
                        msg.getId(), msg.getPicture(), msg.getImage()));
            else
                addMessage(msg);
            //System.out.println(msg.getName()+": "+msg.getText());
        }
    }
    
    /*public void start() throws IOException
    {  
        streamIn = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null)
        {  
            client = new ClientThread(this, socket);
            thread = new Thread(this);                   
            thread.start();
        }
    }*/
    public void start() throws IOException
    {  
        //streamIn = new ObjectInputStream(System.in);
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        if (thread == null)
        {  
            client = new ClientThread(this, socket);
            thread = new Thread(this);  
            thread.start();
        }
    }
    
    public void stop()
    {  
        if (thread != null)
        {  
            thread.stop();  
            thread = null;
        }
        try
        {  
            if (streamIn != null)  
                streamIn.close();
            if (streamOut != null)  
                streamOut.close();
            if (socket != null)  
                socket.close();
        }
        catch(IOException ioe)
        {  
            //System.out.println("Error closing ...");
            addMessage(new Message("Error closing: " + ioe.getMessage(), 
                    Message.messageType.ERROR));
        }
        client.close();  
        client.stop();
    }
    
}
